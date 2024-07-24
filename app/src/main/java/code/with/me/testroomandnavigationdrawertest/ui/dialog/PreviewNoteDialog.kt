@file:Suppress("ktlint:standard:import-ordering")

package code.with.me.testroomandnavigationdrawertest.ui.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.R

import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.ROUNDED_CORNERS
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.SHAKE_DURATION
import code.with.me.testroomandnavigationdrawertest.data.utils.getDate
import code.with.me.testroomandnavigationdrawertest.data.utils.getDisplayMetrics
import code.with.me.testroomandnavigationdrawertest.data.utils.gone
import code.with.me.testroomandnavigationdrawertest.data.utils.println
import code.with.me.testroomandnavigationdrawertest.data.utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.data.utils.setTouchListenerForAllViews
import code.with.me.testroomandnavigationdrawertest.data.utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.ViewNoteDetailDialogPreviewBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseDialog
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.PreviewNoteDialogViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionAudioState
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.io.File

class PreviewNoteDialog() :
    BaseDialog<ViewNoteDetailDialogPreviewBinding>(ViewNoteDetailDialogPreviewBinding::inflate) {
    // TODO сделать максимальный и минимальный размер диалога

//    @Inject
//    @Named("previewVMFactory")
//    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: PreviewNoteDialogViewModel by viewModels()
//    lazy {
//        ViewModelProvider(this, factory)[PreviewNoteDialogViewModel::class.java]
//    }

    private lateinit var adapter: BaseAdapter<PhotoModel, PhotoItemBinding>
    private lateinit var photoItem: PhotoItemBinding

    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimationEnterExit)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.idIntent = arguments?.getLong("noteId") ?: 0
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterInflating()
        initAdapter()
        initViewModel()
        initListeners()
        setWaveformProgressCallback()
        isBehindNeedBlurred = true
        binding.root.setRoundedCornersView(ROUNDED_CORNERS, Color.WHITE)
        setSizeParams()
    }

    private var firstLastId = Pair(0, 0)
    private var firstLastAvailableId = Pair(0L, 0L)

    private var gestureD =
        GestureDetector(
            context,
            object : SimpleOnGestureListener() {
                // false - разрешить скролл
                override fun onScroll(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    distanceX: Float,
                    distanceY: Float,
                ): Boolean {
                    e1?.rawX?.let { rawX ->
                        when (rawX) {
                            in 300f..800f -> {
                                return false
                            }

                            else -> {
                                return true
                            }
                        }
                    }

                    return false
                }

                override fun onFling(
                    e1: MotionEvent?,
                    e2: MotionEvent,
                    velocityX: Float,
                    velocityY: Float,
                ): Boolean {
                    e1?.rawX?.let { rawX ->
                        when (rawX) {
                            in 800f..1500f -> {
                                viewModel.idIntent = firstLastAvailableId.first
                                if (viewModel.idIntent in firstLastId.first..firstLastId.second) {
                                    viewModel.getNoteById(viewModel.idIntent)
                                } else {
                                    viewModel.idIntent = viewModel.currentNote?.id ?: 0L
                                    makeMeShake(binding.root, SHAKE_DURATION, 5)
                                }
                                return true
                            }

                            in 0f..300f -> {
                                viewModel.idIntent = firstLastAvailableId.second
                                if (viewModel.idIntent in firstLastId.first..firstLastId.second) {
                                    viewModel.getNoteById(viewModel.idIntent)
                                } else {
                                    viewModel.idIntent = viewModel.currentNote?.id ?: 0
                                    makeMeShake(binding.root, SHAKE_DURATION, 5)
                                }
                                return true
                            }

                            else -> {
                                return false
                            }
                        }
                    }
                    return false
                }
            },
        )

    fun makeMeShake(
        view: View,
        duration: Int,
        offset: Int,
    ): View {
        val anim: Animation =
            TranslateAnimation(
                -offset.toFloat(),
                offset.toFloat(),
                -offset.toFloat(),
                offset.toFloat(),
            )
        anim.duration = duration.toLong()
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = 3
        view.startAnimation(anim)
        return view
    }

    private fun setSizeParams() {
        binding.root.minimumHeight = getDisplayMetrics(activity()).heightPixels / 3
        val viewParam = binding.root.layoutParams
        viewParam.height = getDisplayMetrics(activity()).heightPixels / 2
        viewParam.width = getDisplayMetrics(activity()).widthPixels
        binding.root.layoutParams = viewParam
        binding.root.minimumWidth = getDisplayMetrics(activity()).widthPixels
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
    @SuppressLint("ClickableViewAccessibility")
    private fun initListeners() {
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
//        binding.text.setOnScrollChangeListener { _, _, _, _, _ ->
//            kotlin.io.println()
//        }
        binding.root.setTouchListenerForAllViews { _, event ->
            event?.let {
                gestureD.onTouchEvent(event)
            }
            true
        }
        // это нужно добавить потому что не будет скроллиться scrollview
        binding.scrollView.setOnTouchListener { _, event ->
            gestureD.onTouchEvent(event)
            false // разрешает ScrollView прокручивать контент
        }
    }

    private fun initAdapterInflating() {
        photoItem =
            PhotoItemBinding.inflate(layoutInflater, binding.photoList.parent as ViewGroup, false)
    }

    private fun initViewModel() {
        viewModel.getNoteById(viewModel.idIntent)
        viewModel.getFirstAndLastCustomer()
        listenViewModels()
    }

    private fun listenViewModels() {
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

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.firstAndLastId.filter { it != Pair(0, 0) }.collect {
                    firstLastId = it
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.firstAndLastAvailableId.filter { it != Pair(0L, 0L) }.collect {
                    firstLastAvailableId = it
                }
            }
        }
    }

    private fun handleViewState(state: NoteState) {
        when (state) {
            is NoteState.Loading -> {
            }

            is NoteState.Result<*> -> {
                binding.apply {
                    setNote(state.data as Note)
                }
            }

            is NoteState.Error<*> -> {
                showError(state.error.toString())
                "ERROR in ${this.javaClass.simpleName} error: ${state.error}".println()
            }

            is NoteState.EmptyResult -> {
                showError("Не удалось загрузить данные")
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
            viewModel.getFirstAndLastAvailableId(value.id)
        }
    }

    private fun initAdapter() {
        adapter =
            object : BaseAdapter<PhotoModel, PhotoItemBinding>(photoItem) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int,
                ): BaseViewHolder<PhotoItemBinding> {
                    val binding =
                        PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    val holder = BaseViewHolder(binding)
                    // Почему-то pinch to zoom не работает даже с библиотеками upd: похоже не работает с sheet
                    holder.itemView.setOnLongClickListener {
                        onLongClickListener.invoke(holder)
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
                        Glide.with(this@PreviewNoteDialog).load(Uri.parse(item.path)).into(image)
                    }
                }
            }.apply {
                this@PreviewNoteDialog.binding.photoList.adapter = this
                this@PreviewNoteDialog.binding.photoList.apply {
                    setHasFixedSize(false)
                    layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                }
            }
    }
}
