package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.ROUNDED_CORNERS_SHEET
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.ROUNDED_CORNERS_STROKE
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.data.utils.getDate
import code.with.me.testroomandnavigationdrawertest.data.utils.gone
import code.with.me.testroomandnavigationdrawertest.data.utils.println
import code.with.me.testroomandnavigationdrawertest.data.utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.data.utils.visible
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.ViewNoteDetailSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionAudioState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.ViewANoteViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import kotlinx.coroutines.launch
import java.io.File

// todo: не используется в проекте
class ViewANoteSheet : BaseSheet<ViewNoteDetailSheetBinding>(ViewNoteDetailSheetBinding::inflate) {
    /**
     * Нет обработки ошибок у MediaPlayer во viewModel
     **/

//    @Inject
//    @Named("viewANoteVMFactory")
//    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: ViewANoteViewModel by viewModels()
//    lazy {
//        ViewModelProvider(this, factory)[ViewANoteViewModel::class.java]
//    }

    lateinit var adapter: BaseAdapter<PhotoModel, PhotoItemBinding>
    private lateinit var photoItem: PhotoItemBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterInflating()
        initAdapter()
        initViewModel()
        initClickListeners()
        setWaveformProgressCallback()
        setRoundedCornersAndCallback()
    }

    private fun setRoundedCornersAndCallback() {
        binding.include2.root.alpha = 1.0f
        binding.mainLayout.setRoundedCornersView(
            ROUNDED_CORNERS_SHEET,
            Color.WHITE,
            Color.BLACK,
            ROUNDED_CORNERS_STROKE,
        )
        onSlide = {
            binding.include2.root.alpha = 1.0f - it
            // проверка нужна, что бы не уменьшался радиус угла после halfexpanded
            if (it >= 0) {
                binding.mainLayout.setRoundedCornersView(
                    (1.0f - it) * ROUNDED_CORNERS_SHEET,
                    Color.WHITE,
                    Color.BLACK,
                    (1.0f - it) * ROUNDED_CORNERS_STROKE,
                )
            }
        }
    }

    private fun setWaveformProgressCallback() {
        binding.waveForm.onProgressChanged =
            object : SeekBarOnProgressChanged {
                override fun onProgressChanged(
                    waveformSeekBar: WaveformSeekBar,
                    progress: Float,
                    fromUser: Boolean,
                ) {
                    if (fromUser) {
                        if (viewModel.isAudioPlaying()) {
                            sendViewAction(UserActionAudioState.PausePlaying)
                        }
                        if (!viewModel.checkAudioPlayer()) {
                            sendViewAction(UserActionAudioState.InitPlayer(viewModel.currentNote?.audioUrl.toString()))
                        }
                        viewModel.setCurrentPos(progress)
                        viewModel.startTimer(100)
                    }
                }
            }
    }

    private fun sendViewAction(userAction: UserActionAudioState) {
        viewModel.processUserActionsAudio(userAction)
    }

    /**
     * надо что-то сделать с после playAudio.setOnClickListener
     **/
    private fun initClickListeners() {
        binding.apply {
            playPauseBtn.setOnClickListener {
                if (viewModel.isAudioPlaying()) {
                    sendViewAction(UserActionAudioState.PausePlaying)
                } else {
                    sendViewAction(UserActionAudioState.StartPlaying(viewModel.currentNote?.audioUrl.toString()))
                }
            }
            waveForm.setOnTouchListener { v, event ->
                viewModel.doWaveFormOnTouch = true
                false
            }
            waveForm.setOnClickListener {
                viewModel.doWaveFormOnTouch = false
            }
        }
    }

    private fun initAdapterInflating() {
        photoItem =
            PhotoItemBinding.inflate(layoutInflater, binding.photoList.parent as ViewGroup, false)
    }

    private fun initViewModel() {
        viewModel.idIntent = arguments?.getLong("noteId") ?: 0L
        viewModel.getNoteById(viewModel.idIntent)
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.state.collect { state ->
                    handleViewState(state)
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.waveFormProgress.collect { progress ->
                    binding.waveForm.progress = progress
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.timerState.collect { progress ->
                    if (!viewModel.doWaveFormOnTouch) {
                        sendViewAction(UserActionAudioState.StartPlaying(viewModel.currentNote?.audioUrl.toString()))
                    }
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.userActionAudioState.collect {
                    if (it is UserActionAudioState.Error<*>) {
                        Toast.makeText(activity(), it.error.toString(), Toast.LENGTH_SHORT).show()
                    }

                    if (binding.playPauseBtn.isAnimating) {
                        binding.playPauseBtn.clearAnimation()
                    }

                    when (it) {
                        is UserActionAudioState.StartPlaying -> {
                            binding.playPauseBtn.setAnimation(R.raw.play_anim)
                        }

                        is UserActionAudioState.PausePlaying -> {
                            binding.playPauseBtn.setAnimation(R.raw.pause_anim)
                        }

                        is UserActionAudioState.Error<*> -> {
                            Toast.makeText(activity(), it.error.toString(), Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {}
                    }
                    binding.playPauseBtn.playAnimation()
                }
            }
        }
    }

    private fun handleViewState(state: NoteState) {
        when (state) {
            is NoteState.Loading -> {
                showProgressBar(true)
            }

            is NoteState.Result<*> -> {
                binding.apply {
                    setNote(state.data as Note)
                }
                showProgressBar(false)
            }

            is NoteState.Error<*> -> {
                showProgressBar(false)
                showError(state.error.toString())
                "ERROR in ${this.javaClass.simpleName} error: ${state.error}".println()
            }

            is NoteState.EmptyResult -> {
                showProgressBar(false)
            }
        }
    }

    private fun showError(text: String) {
        dialog?.window?.decorView?.let { window ->
            Snackbar.make(
                window, // binding.root is not work :(
                text,
                Snackbar.LENGTH_LONG,
            ).show()
        }
    }

    private fun setNote(value: Note?) {
        value?.let {
            binding.apply {
                dateText.text = context.getDate(value.lastTimestampCreate)
                titleText.text = value.titleNote
                text.text = value.textNote
                if (value.audioUrl.isNotEmpty()) {
                    audioLayout.visible()
//                    "File(value.audioUrl) exist? ${File(value.audioUrl).exists()}".println()
                    try {
                        waveForm.setSampleFrom(File(value.audioUrl))
                    } catch (error: Exception) {
                        showError(
                            error.localizedMessage?.toString()
                                ?: "Не удалось инициализировать волну аудио", // ???
                        )
                    }
                } else {
                    audioLayout.gone()
                }
                adapter.submitList(ArrayList(value.listOfImages))
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun initAdapter() {
        adapter =
            object : BaseAdapter<PhotoModel, PhotoItemBinding>(photoItem) {
                private var selected0 = -1

                init {
                    clickListener = {
                        selected0 = it.layoutPosition
                        val item = getItem(it.layoutPosition) as PhotoModel
//                    openDetailFragment(item.id)
                    }
                    onLongClickListener = {
                        selected0 = it.layoutPosition
                        val item = getItem(it.layoutPosition) as PhotoModel
                    }
                }

                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int,
                ): BaseViewHolder<PhotoItemBinding> {
                    val binding =
                        PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    val holder = BaseViewHolder(binding)
                    // Почему-то pinch не работает даже с библиотеками upd: похоже не работает с sheet
//                holder.itemView.setOnTouchListener(context?.let { ImageMatrixTouchHandler(it) })
                    holder.itemView.setOnLongClickListener {
                        onLongClickListener?.invoke(holder)
                        return@setOnLongClickListener true
                    }
                    return holder
                }

                override fun onBindViewHolder(
                    holder: BaseViewHolder<PhotoItemBinding>,
                    position: Int,
                ) {
                    super.onBindViewHolder(holder, position)

                    holder.binding.apply {
                        val item = getItem(position)
                        "item.path: ${item.path}".println()

                        Glide.with(this@ViewANoteSheet)
                            .load(Uri.parse(item.path)).into(image)
                    }
                }
            }.apply {
                this@ViewANoteSheet.binding.photoList.adapter = this
                this@ViewANoteSheet.binding.photoList.apply {
                    setHasFixedSize(false)
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                }
            }
    }
}
