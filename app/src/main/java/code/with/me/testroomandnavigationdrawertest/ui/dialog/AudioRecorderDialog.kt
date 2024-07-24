package code.with.me.testroomandnavigationdrawertest.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.AUDIO_PATH
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.ROUNDED_CORNERS
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.STANDARD_BLUR_RADIUS
import code.with.me.testroomandnavigationdrawertest.data.utils.setCancelButton
import code.with.me.testroomandnavigationdrawertest.data.utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.databinding.AudioRecorderBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseDialog
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.AudioRecorderViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRecorderDialog() :
    BaseDialog<AudioRecorderBinding>(AudioRecorderBinding::inflate) {
    // надо подумать как сделать норм

    @Inject
    lateinit var audioController: AudioController

//    @Inject
//    @Named("previewVMFactory")
//    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: AudioRecorderViewModel by viewModels()
// lazy {
//        ViewModelProvider(this, factory)[AudioRecorderViewModel::class.java]
//    }

    // need refactor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.apply {
            saveAudioButton.setCancelButton()
            listenAudioControllerScaleCallback()
        }
        initClickListeners()
        setCancelable(false)
        setUpDialogWindow()
        initViewModelObservers()
        viewModel.startRecording(audioController, activity())
    }

    private fun AudioRecorderBinding.listenAudioControllerScaleCallback() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.scale.collect { scale ->
                    micImage.animate()
                        .scaleX(scale)
                        .scaleY(scale)
                        .setInterpolator(OvershootInterpolator())
                        .duration = AudioController.VOLUME_UPDATE_DURATION
                }
            }
        }
    }

    private fun initViewModelObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.recordingTime.drop(1).collect {
                    withContext(Dispatchers.Main.immediate) {
                        binding.recordTime.text = DateUtils.formatElapsedTime(it)
                    }
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.saveAudioButton.setOnClickListener {
            viewModel.stopRecording(audioController)
            setFragmentResult(
                AUDIO_PATH,
                Bundle().apply {
                    this.putString(AUDIO_PATH, viewModel.getAudioPath(audioController, activity()))
                },
            )
//            result.invoke(viewModel.getAudioPath(audioController, activity()))
            dismiss()
        }
    }

    private fun setUpDialogWindow() {
        binding.root.setRoundedCornersView(ROUNDED_CORNERS)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            this.dialog?.window?.attributes?.blurBehindRadius = STANDARD_BLUR_RADIUS
        }
    }

    override fun onDestroyView() {
        viewModel.stopRecording(audioController)
        viewModel.stopPlaying(audioController)
        super.onDestroyView()
    }
}
