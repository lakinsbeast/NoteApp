package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.animation.OvershootInterpolator
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.data.utils.setCancelButton
import code.with.me.testroomandnavigationdrawertest.data.utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.databinding.AudioRecorderBinding
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRecorderDialog(private val myContext: Context, private val result: (String) -> Unit) :
    Dialog(myContext) {
    private lateinit var binding: AudioRecorderBinding

    private val timer: Job by lazy {
        CoroutineScope(Dispatchers.IO.limitedParallelism(1)).launch {
            while (true) {
                withContext(Dispatchers.Main.immediate) {
                    binding.recordTime.text = DateUtils.formatElapsedTime(seconds)
                }
                delay(1000)
                seconds++
            }
        }
    }
    private var seconds = 0L

    @Inject
    lateinit var audioController: AudioController

    // need refactor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppComponent()
        binding = AudioRecorderBinding.inflate(LayoutInflater.from(myContext))
        setContentView(binding.root)

        binding.apply {
            saveAudioButton.setCancelButton()
            audioController.startRecording(myContext as MainActivity) { scale ->
                micImage.animate()
                    .scaleX(scale)
                    .scaleY(scale)
                    .setInterpolator(OvershootInterpolator())
                    .duration = AudioController.VOLUME_UPDATE_DURATION
            }
        }

        launchTimer()
        initClickListeners()
        setCancelable(false)
        setUpDialogWindow()
    }

    private fun initAppComponent() {
        val appComponent =
            ((myContext as MainActivity).application as NotesApplication).appComponent
        appComponent.inject(this)
    }

    private fun initClickListeners() {
        binding.cancelButton.setOnClickListener {
            dismiss()
        }
        binding.saveAudioButton.setOnClickListener {
            audioController.stopRecording()
            result.invoke(audioController.getAudioPath(myContext as MainActivity))
            dismiss()
        }
    }

    private fun launchTimer() {
        timer
    }

    private fun setUpDialogWindow() {
        binding.root.setRoundedCornersView(56f)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            this.window?.attributes?.blurBehindRadius = 10
        }
    }

    override fun onDetachedFromWindow() {
        audioController.apply {
            stopRecording()
            stopPlaying()
        }
        super.onDetachedFromWindow()
    }
}
