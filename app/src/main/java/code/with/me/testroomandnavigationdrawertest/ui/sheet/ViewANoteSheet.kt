package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.AlertCreator
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.getDate
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.println
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.ViewNoteDetailSheetBinding
import code.with.me.testroomandnavigationdrawertest.file.FilesController
import code.with.me.testroomandnavigationdrawertest.ui.SnackbarCreator
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.AudioPlayerState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionAudioState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
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


class ViewANoteSheet : BaseSheet<ViewNoteDetailSheetBinding>(ViewNoteDetailSheetBinding::inflate) {

    /**
     * Нет обработки ошибок у MediaPlayer во viewModel
     **/

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
                        dateText.text = getDate(value.lastTimestampCreate)
                        titleText.text = value.titleNote
                        text.text = value.textNote

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
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        viewANoteViewModel = ViewModelProvider(this, factory)[ViewANoteViewModel::class.java]
        setFullScreenSheet()
        viewANoteViewModel.setActivityToAudioController(activity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterInflating()
        initAdapter()
        initViewModel()
        initClickListeners()
        setWaveformProgressCallback()
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
            playAudio.setOnClickListener {
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
        val idIntent = arguments?.getInt("noteId") ?: 0
        "id: $idIntent".println()
        viewANoteViewModel.getNoteById(idIntent)
        viewANoteViewModel.state.observe(viewLifecycleOwner) { state ->
            handleViewState(state)
        }
        viewANoteViewModel.waveFormProgress.observe(viewLifecycleOwner) { progress ->
            binding.waveForm.progress = progress
        }
        viewANoteViewModel.userActionAudioState.observe(viewLifecycleOwner) {
            if (it is UserActionAudioState.Error<*>) {
                Toast.makeText(activity(), it.error.toString(), Toast.LENGTH_SHORT).show()
            }
        }

        viewANoteViewModel.getAudioPlaybackStateLiveData().observe(viewLifecycleOwner) { state ->
            when (state) {
                is AudioPlayerState.Idle -> {
                    binding.playAudio.setImageResource(R.drawable.small_play_arrow_btn)
                }

                is AudioPlayerState.Playing -> {
                    binding.playAudio.setImageResource(R.drawable.small_pause_btn)
                }

                is AudioPlayerState.Paused -> {
                    binding.playAudio.setImageResource(R.drawable.small_play_arrow_btn)
                }

                AudioPlayerState.Completed -> {
                    binding.playAudio.setImageResource(R.drawable.small_play_arrow_btn)
                }

                is AudioPlayerState.Error<*> -> {
                    Toast.makeText(activity(), state.error.toString(), Toast.LENGTH_SHORT).show()
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
                    currentNote = state.data as Note
                    println("note: ${state.data}")
                }
                showProgressBar(false)
            }

            is NoteState.Error<*> -> {
                showProgressBar(false)
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
                showProgressBar(false)
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

                    Glide.with(this@ViewANoteSheet)
                        .load(Uri.parse(item.path)).into(image)
                }
            }
        }.apply {
            this@ViewANoteSheet.binding.photoList.adapter = this
            this@ViewANoteSheet.binding.photoList.apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }


}