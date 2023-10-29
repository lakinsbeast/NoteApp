package code.with.me.testroomandnavigationdrawertest.ui.dialog

import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.Utils.getDate
import code.with.me.testroomandnavigationdrawertest.data.Utils.getDisplayMetrics
import code.with.me.testroomandnavigationdrawertest.data.Utils.gone
import code.with.me.testroomandnavigationdrawertest.data.Utils.launchAfterTimerIO
import code.with.me.testroomandnavigationdrawertest.data.Utils.mainScope
import code.with.me.testroomandnavigationdrawertest.data.Utils.println
import code.with.me.testroomandnavigationdrawertest.data.Utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.data.Utils.setTouchListenerForAllViews
import code.with.me.testroomandnavigationdrawertest.data.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.ViewNoteDetailDialogPreviewBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseDialog
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionAudioState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.ViewANoteViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Named


class PreviewNoteDialog() :
    BaseDialog<ViewNoteDetailDialogPreviewBinding>(ViewNoteDetailDialogPreviewBinding::inflate) {

    //TODO сделать максимальный и минимальный размер диалога


    @Inject
    @Named("viewANoteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var viewANoteViewModel: ViewANoteViewModel

    lateinit var adapter: BaseAdapter<PhotoModel, PhotoItemBinding>
    private lateinit var photoItem: PhotoItemBinding

    private var timerJob: Job? = null
    private var doWaveFormOnTouch = false

    private var currentNote: Note? = null
        set(value) {
            if (value != field) {
                field = value
                if (value != null) {
                    binding.apply {
                        context?.let {
                            dateText.text = getDate(it, value.lastTimestampCreate)
                        }
                        titleText.text = value.titleNote
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
//                                val parsedText = markdownParser.getParsedText(value.textNote)
                                mainScope {
                                    text.text = value.textNote
                                }
                            } catch (e: Exception) {
                                mainScope {
                                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show()
                                    text.text = value.textNote
                                    e.printStackTrace()
                                }
                            }
                        }

                        if (value.audioUrl.isNotEmpty()) {
                            audioLayout.visible()
                            "File(value.audioUrl) exist? ${File(value.audioUrl).exists()}".println()
                            try {
                                waveForm.setSampleFrom(File(value.audioUrl))
                            } catch (_: Exception) {

                            }
                        } else {
                            audioLayout.gone()
                        }

                        adapter.submitList(ArrayList(value.listOfImages))
                        adapter.notifyDataSetChanged()
                    }
                    viewANoteViewModel.getNextAvailableId(value.id)
                    viewANoteViewModel.getPreviousAvailableId(value.id)
                }
            }
        }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setWindowAnimations(R.style.DialogAnimationEnterExit)

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        viewANoteViewModel = ViewModelProvider(this, factory)[ViewANoteViewModel::class.java]
        viewANoteViewModel.setActivityToAudioController(activity())
        idIntent = arguments?.getLong("noteId") ?: 0
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterInflating()
        initAdapter()
        initViewModel()
        initClickListeners()
        setWaveformProgressCallback()
        isBehindNeedBlurred = true
        binding.root.setRoundedCornersView(56f, Color.WHITE)
        setSizeParams()

        binding.text.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            kotlin.io.println()
        }
        binding.root.setTouchListenerForAllViews { v, event ->
            if (event != null) {
                gestureD.onTouchEvent(event)
            }
            true
        }
        //это нужно добавить потому что не будет скроллиться scrollview
        binding.scrollView.setOnTouchListener { v, event ->
            gestureD.onTouchEvent(event)
            false // разрешает ScrollView прокручивать контент
        }


    }

    var idIntent = 0L
        set(value) {
            field = value
        }

    private var lastId = 0
    private var firstId = 0

    private var lastAvailableId = 0L
    private var nextAvailableId = 0L

    //Избавиться от магических чисел
    private var gestureD = GestureDetector(context, object : SimpleOnGestureListener() {
        //false - разрешить скролл
        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent,
            distanceX: Float,
            distanceY: Float
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
            velocityY: Float
        ): Boolean {
            e1?.rawX?.let { rawX ->
                when (rawX) {
                    in 800f..1500f -> {
                        idIntent = nextAvailableId
                        println("idIntent: $idIntent")
                        println("nextAvailableId: $nextAvailableId")
                        println("lastAvailableId: $lastAvailableId")
                        println("firstId: $firstId")
                        println("lastId: $lastId")
//                        idIntent += 1
                        if (idIntent in firstId..<lastId) {
                            viewANoteViewModel.getNoteById(idIntent)
                        } else {
                            idIntent = currentNote?.id ?: 0L
                            makeMeShake(binding.root, 50, 15)
                        }
                        return true
                    }

                    in 0f..300f -> {
                        idIntent = lastAvailableId
                        if (idIntent in firstId..<lastId) {
                            viewANoteViewModel.getNoteById(idIntent)
                        } else {
                            idIntent = currentNote?.id ?: 0
                            makeMeShake(binding.root, 50, 15)
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
    })

    //test
    fun makeMeShake(view: View, duration: Int, offset: Int): View? {
        val anim: Animation = TranslateAnimation(-offset.toFloat(), offset.toFloat(), 0f, 0f)
        anim.duration = duration.toLong()
        anim.repeatMode = Animation.REVERSE
        anim.repeatCount = 5
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
        binding.waveForm.onProgressChanged = object : SeekBarOnProgressChanged {
            override fun onProgressChanged(
                waveformSeekBar: WaveformSeekBar,
                progress: Float,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    if (viewANoteViewModel.isAudioPlaying()) {
                        sendViewAction(UserActionAudioState.PausePlaying)
                    }
                    if (viewANoteViewModel.checkAudioPlayer() == null) {
                        sendViewAction(UserActionAudioState.InitPlayer(currentNote?.audioUrl.toString()))
                    }
                    viewANoteViewModel.setCurrentPos(progress)
                    startTimer(100) {
                        if (!doWaveFormOnTouch) {
                            sendViewAction(UserActionAudioState.StartPlaying(currentNote?.audioUrl.toString()))
                        }
                    }
                }
            }
        }
    }

    fun startTimer(delayLong: Long, doOnDelayPassed: () -> Unit) {
        if (timerJob != null) {
            timerJob?.cancel()
            timerJob = null
        }
        timerJob = CoroutineScope(Dispatchers.IO.limitedParallelism(1)).launch {
            delay(delayLong)
            doOnDelayPassed.invoke()
        }
    }

    private fun sendViewAction(userAction: UserActionAudioState) {
        viewANoteViewModel.processUserActionsAudio(userAction)
    }

    /**
     * надо что-то сделать с после playAudio.setOnClickListener
     **/
    private fun initClickListeners() {
        binding.apply {
            playPauseBtn.setOnClickListener {
                if (viewANoteViewModel.isAudioPlaying()) {
                    sendViewAction(UserActionAudioState.PausePlaying)
                } else {
                    sendViewAction(UserActionAudioState.StartPlaying(currentNote?.audioUrl.toString()))
                }
            }
            waveForm.setOnTouchListener { v, event ->
                doWaveFormOnTouch = true
                false
            }
            waveForm.setOnClickListener {
                doWaveFormOnTouch = false
            }
        }
    }

    private fun initAdapterInflating() {
        photoItem =
            PhotoItemBinding.inflate(layoutInflater, binding.photoList.parent as ViewGroup, false)
    }

    private fun initViewModel() {
//        val idIntent = arguments?.getInt("noteId") ?: 0
//        "id: $idIntent".println()
        viewANoteViewModel.getNoteById(idIntent)
        viewANoteViewModel.state.observe(viewLifecycleOwner) { state ->
            handleViewState(state)
        }
        viewANoteViewModel.getFirstCustomer()
        viewANoteViewModel.getLastCustomer()
        viewANoteViewModel.waveFormProgress.observe(viewLifecycleOwner) { progress ->
            binding.waveForm.progress = progress
        }
        viewANoteViewModel.userActionAudioState.observe(viewLifecycleOwner) {
            println("userActionAudioState: $it")
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
                    Toast.makeText(activity(), it.error.toString(), Toast.LENGTH_SHORT).show()
                }

                else -> {}
            }
            binding.playPauseBtn.playAnimation()
        }
        viewANoteViewModel.lastIdOfNotes.observe(viewLifecycleOwner) {
            lastId = it
        }
        viewANoteViewModel.firstIdOfNotes.observe(viewLifecycleOwner) {
            firstId = it
        }
        viewANoteViewModel.previousIdNote.observe(viewLifecycleOwner) {
            lastAvailableId = it
        }
        viewANoteViewModel.nextIdNote.observe(viewLifecycleOwner) {
            nextAvailableId = it
        }

    }

    private fun handleViewState(state: NoteState) {
        when (state) {
            is NoteState.Loading -> {
//                showProgressBar(true)
            }

            is NoteState.Result<*> -> {
                binding.apply {
                    println("note: ${state.data}")
                    currentNote = state.data as Note
                }
//                showProgressBar(false)
            }

            is NoteState.Error<*> -> {
//                showProgressBar(false)
                dialog?.window?.decorView?.let { window ->
                    Snackbar.make(
                        window, //binding.root is not work :(
                        state.error.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
                "ERROR in ${this.javaClass.simpleName} error: ${state.error}".println()
            }

            is NoteState.EmptyResult -> {
//                showProgressBar(false)
            }

        }
    }

    private fun initAdapter() {
        adapter = object : BaseAdapter<PhotoModel, PhotoItemBinding>(photoItem) {
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
                parent: ViewGroup, viewType: Int
            ): BaseViewHolder<PhotoItemBinding> {
                val binding =
                    PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
                //Почему-то pinch не работает даже с библиотеками upd: похоже не работает с sheet
//                holder.itemView.setOnTouchListener(context?.let { ImageMatrixTouchHandler(it) })
                holder.itemView.setOnLongClickListener {
                    onLongClickListener?.invoke(holder)
                    return@setOnLongClickListener true
                }
                return holder
            }


            override fun onBindViewHolder(
                holder: BaseViewHolder<PhotoItemBinding>,
                position: Int
            ) {
                super.onBindViewHolder(holder, position)

                holder.binding.apply {
                    val item = getItem(position)
                    "item.path: ${item.path}".println()

                    Glide.with(this@PreviewNoteDialog)
                        .load(Uri.parse(item.path)).into(image)
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