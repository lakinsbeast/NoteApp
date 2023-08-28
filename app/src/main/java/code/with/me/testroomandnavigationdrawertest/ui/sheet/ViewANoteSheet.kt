package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.getDate
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.launchAfterTimerIO
import code.with.me.testroomandnavigationdrawertest.Utils.launchAfterTimerMain
import code.with.me.testroomandnavigationdrawertest.Utils.println
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.databinding.ViewNoteDetailSheetBinding
import code.with.me.testroomandnavigationdrawertest.file.FilesController
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModel
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.masoudss.lib.SeekBarOnProgressChanged
import com.masoudss.lib.WaveformSeekBar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Named
import kotlin.math.roundToInt


class ViewANoteSheet : BaseSheet<ViewNoteDetailSheetBinding>(ViewNoteDetailSheetBinding::inflate) {

    private var currentPos: Int = -1

    @Inject
    @Named("noteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteViewModel: NoteViewModel

    @Inject
    lateinit var filesController: FilesController

    @Inject
    lateinit var audioController: AudioController

    lateinit var adapter: BaseAdapter<PhotoModel, PhotoItemBinding>
    private lateinit var photoItem: PhotoItemBinding

    private val timerJob = CoroutineScope(Dispatchers.IO.limitedParallelism(1)).launch {

    }

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
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
        setFullScreenSheet()
        audioController.activity = activity()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapterInflating()
        initAdapter()
        initViewModel()
        initClickListeners()
        binding.waveForm.onProgressChanged = object : SeekBarOnProgressChanged {
            override fun onProgressChanged(
                waveformSeekBar: WaveformSeekBar,
                progress: Float,
                fromUser: Boolean
            ) {
                if (fromUser) {
                    if (audioController.player?.isPlaying == true) {
                        audioController.pausePlaying()
                    }
                    audioController.player?.let {
                        currentPos =
                            ((it.duration.times(progress)).roundToInt()) / 100
                    }
                }
            }
        }
    }

    /**
     * надо что-то сделать с после playAudio.setOnClickListener
     **/
    private fun initClickListeners() {
        binding.apply {
            playAudio.setOnClickListener {
                "currentPos: $currentPos".println()
                startAudioPlaying()
            }
        }
    }

    private fun ViewNoteDetailSheetBinding.startAudioPlaying() {
        try {
            if (!audioController.isAudioPlaying()) {
                playAudio.setImageResource(R.drawable.small_pause_btn)
                if (currentPos != -1) {
                    audioController.player?.start()
                    audioController.player?.seekTo(currentPos)
                } else {
                    audioController.startPlaying(currentNote?.audioUrl.toString())
                }
                launchAfterTimerMain(0) {
                    run breaking@{
                        while (true) {
                            //в let нельзя использовать break, загуглил и нашел такой вариант через run breaking@
                            audioController.player?.let { player ->
                                currentPos =
                                    player.currentPosition
                                if (currentPos != -1) {
                                    waveForm.progress =
                                        ((currentPos * 100) / player.duration).toFloat()
                                }
                                if (player.currentPosition == player.duration) {
                                    currentPos = -1
                                }
                                if (!audioController.isAudioPlaying()) {
                                    playAudio.setImageResource(R.drawable.small_play_arrow_btn)
                                    "audioController.player?.currentPosition ${audioController.player?.currentPosition}".println()
                                    "audioController.player?.duration ${audioController.player?.duration}".println()
                                    audioController.pausePlaying()
                                    return@breaking
                                }
                            }
                            delay(0)
                        }
                    }

                }
            } else {
                playAudio.setImageResource(R.drawable.small_play_arrow_btn)
                audioController.pausePlaying()
            }
        } catch (e: Exception) {
            playAudio.setImageResource(R.drawable.small_play_arrow_btn)
        }
    }

    private fun initAdapterInflating() {
        photoItem =
            PhotoItemBinding.inflate(layoutInflater, binding.photoList.parent as ViewGroup, false)
    }

    private fun initViewModel() {
        val idIntent = arguments?.getInt("noteId") ?: 0
        "id: $idIntent".println()
        noteViewModel.getNoteById(idIntent)
        noteViewModel.state.observe(viewLifecycleOwner) { state ->
            handleViewState(state)
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