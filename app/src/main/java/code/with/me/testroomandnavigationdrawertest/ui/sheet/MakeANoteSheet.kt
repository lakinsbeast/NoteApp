package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.Manifest
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.getDisplayMetrics
import code.with.me.testroomandnavigationdrawertest.Utils.providerName
import code.with.me.testroomandnavigationdrawertest.Utils.setCheckable
import code.with.me.testroomandnavigationdrawertest.Utils.setRoundedCorners
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityAddNoteBinding
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionNote
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext


class MakeANoteSheet : BaseSheet<ActivityAddNoteBinding>(ActivityAddNoteBinding::inflate) {

    private var currentPermission: TypeOfPermission = TypeOfPermission.EMPTY
    private var cameraUri: Uri? = null
    private var cameraInString: String = ""
    private var audioInString: String = ""
    private var paintInString: String = ""
    private var imageInString: String = ""
    private var lastCreatedTime: Long = 0L
    private var lastOpenedTime: Long = 0L

    private var lastSaveID = 0L

    init {
        lastCreatedTime = System.currentTimeMillis()
        lastOpenedTime = System.currentTimeMillis()
    }

    private var RECORD_AUDIO: Int = 0

    private val randomNum = System.currentTimeMillis() / 1000

    private var fileAudioName: String? = null

    private var recorder: MediaRecorder? = null

    var scope = CoroutineScope(Job() + Dispatchers.IO)

    private var pickedColor: String = "#FFFFFFFF"

    private var listOfPhotos = mutableListOf<PhotoModel>()

    private var note: Note = Note(
        lastSaveID.toInt(),
        "",
        "",
        listOfPhotos.toList(),
        audioInString,
        pickedColor,
        -1,
        lastCreatedTime,
        lastOpenedTime,
        false,
        "-1"
    )

    @Inject
    @Named("noteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteViewModel: NoteViewModel

    lateinit var adapter: BaseAdapter<PhotoModel, PhotoItemBinding>
    private lateinit var photoItem: PhotoItemBinding


    enum class TypeOfPermission {
        CAMERA, IMAGE_GALLERY, AUDIO, WRITE_EXTERNAL, EMPTY,
    }

    private var imageSelected: (String) -> Unit = {}
    private var cameraSelected: (String) -> Unit = {}
    private var micSelected: (String) -> Unit = {}

    private fun openPaintSheet() {
        findNavController().navigate(MakeANoteSheetDirections.actionMakeANoteSheetToPaintSheet())
    }


    private var requestt =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            when (currentPermission) {
                TypeOfPermission.CAMERA -> {
                    openCamera()
                }

                TypeOfPermission.IMAGE_GALLERY -> {
                    chooseImage()
                }

                TypeOfPermission.AUDIO -> {
                    audioRec()
                }

                else -> {}
            }
        }

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { imageUri ->
            if (imageUri != null) {
                activity?.contentResolver?.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                imageInString = imageUri.toString()
                imageSelected.invoke(imageInString)
                listOfPhotos.add(PhotoModel(imageInString))
                adapter.submitList(listOfPhotos)
                adapter.notifyDataSetChanged()
                saveNote()
            }
        }

    private val getImageFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                activity?.let {
                    MediaStore.Images.Media.getBitmap(it.contentResolver, cameraUri)
                    val test = cameraUri.toString()
                    cameraInString = test
                    cameraSelected.invoke(cameraInString)
                    listOfPhotos.add(PhotoModel(cameraInString))
                    adapter.submitList(listOfPhotos)
                    adapter.notifyDataSetChanged()
                    saveNote()
                }
            }
        }

    companion object {
        const val paintResultKey = "paintResultKey"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        noteViewModel = ViewModelProvider(requireActivity(), factory)[NoteViewModel::class.java]
        setFullScreenSheet()
        listenPaintSheetResult()
    }

    private fun listenPaintSheetResult() {
        setFragmentResultListener(paintResultKey) { requestKey: String, bundle: Bundle ->
            val paintBitmap = bundle.getString("pathBitmap")
            println("pathBitmap: ${paintBitmap}")
            println("requestKey: ${requestKey}")
            if (paintBitmap != null) {
                paintInString = paintBitmap
                listOfPhotos.add(PhotoModel(paintInString))
                adapter.submitList(listOfPhotos)
                adapter.notifyDataSetChanged()
                saveNote()
            } else {
                throw java.lang.Exception("Не удалось получить данные paintActivity")
            }
        }
    }

    private fun saveNote() {
        noteViewModel.saveNote(
            lastSaveID.toInt(),
            binding.titleEdit.text.toString(),
            binding.textEdit.text.toString(),
            listOfPhotos.toList(),
            audioInString,
            pickedColor,
            arguments?.getInt("idFolder")!!,
            lastCreatedTime,
            lastOpenedTime,
            false,
            "-1"
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initAdapter()
        initClickListeners()
        initSheetCallbacks()
        initViewModel()
        initTextChangeListeners()
        setBottomMarginBottomNav((getDisplayMetrics(activity()).heightPixels / 2) - 200)
    }

    private fun handleUserActionState(state: UserActionNote) {
        when (state) {
            is UserActionNote.GetImage -> {
                chooseImage()
            }

            is UserActionNote.GetCamera -> {
                openCamera()
            }

            is UserActionNote.GetMicrophone -> {
                println("GetCamera!")
            }

            is UserActionNote.GetDraw -> {
                openPaintSheet()
            }

            is UserActionNote.SavedNoteToDB -> {
                println("${javaClass.simpleName} is UserActionNote.SavedNoteToDB")
            }
        }

    }

    private fun sendViewAction(action: UserActionNote) {
        noteViewModel.processUserActions(action)
    }

    private fun initTextChangeListeners() {
        binding.apply {
            titleEdit.addTextChangedListener {
                saveNote()
            }

            textEdit.addTextChangedListener {
                saveNote()
            }
        }
    }


    private fun initViewModel() {
        scope.launch {
            async {
                lastSaveID = noteViewModel.getLastCustomer()
                lastSaveID += 1
            }.await()
            launch {
                noteViewModel.insert(note)
            }
        }
        noteViewModel.userActionState.observe(viewLifecycleOwner) { state ->
            handleUserActionState(state)
        }
    }

    private fun initSheetCallbacks() {
        onSlide = {
//            println("onSlide: $it")
        }
        onStateChanged = { newState, oldState ->
            if (newState != oldState) {
                when (newState) {
                    6 -> {
                        setBottomNavHalfScreen(newState)
                    }

                    3 -> {
                        setBottomNavFullScreen(newState)
                    }
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.imageBtn -> {
                    sendViewAction(UserActionNote.GetImage)
                    true
                }

                R.id.camera_btn -> {
                    sendViewAction(UserActionNote.GetCamera)
                    true
                }

                R.id.imageButtonVoice -> {
                    sendViewAction(UserActionNote.GetMicrophone)
                    true
                }

                R.id.imageButtonDraw -> {
                    sendViewAction(UserActionNote.GetDraw)
                    true
                }

                else -> {
                    false
                }
            }
        }
    }

    private fun initUI() {
        photoItem =
            PhotoItemBinding.inflate(layoutInflater, binding.photoList.parent as ViewGroup, false)
        binding.bottomNavigation.setRoundedCorners(32f)
        binding.bottomNavigation.setCheckable()
    }

    private fun setBottomNavFullScreen(state: Int) {
        println("state: $state")
        val animator =
            ValueAnimator.ofInt((getDisplayMetrics(activity()).heightPixels / 2) - 200, 40)
        animator.duration = 100
        animator.addUpdateListener {
            val animatedValue = animator.animatedValue as Int
            setBottomMarginBottomNav(animatedValue)
        }
        animator.start()
        behavior?.state = state
    }

    private fun setBottomNavHalfScreen(state: Int) {
        val animator =
            ValueAnimator.ofInt(40, (getDisplayMetrics(activity()).heightPixels / 2) - 200)
        animator.duration = 100
        animator.addUpdateListener {
            val animatedValue = animator.animatedValue as Int
            setBottomMarginBottomNav(animatedValue)
        }
        animator.start()
        behavior?.state = state
    }

    private fun setBottomMarginBottomNav(animatedValue: Int) {
        val layParams =
            binding.bottomNavigation.layoutParams as ViewGroup.MarginLayoutParams
        layParams.bottomMargin = animatedValue
        binding.bottomNavigation.layoutParams = layParams
    }


    private fun stopAudio() {
        try {
            activity?.let {
                recorder?.apply {
                    stop()
                    release()
                }
//                binding.imageButtonVoice.setImageResource(R.drawable.ic_baseline_keyboard_voice_24)
                saveNote()
                Toast.makeText(it, "Запись голоса завершена", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("audioRecorder", e.message.toString())
        }
    }


    private fun audioRec() {
        activity?.let {
            if (ActivityCompat.checkSelfPermission(
                    it, Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    it, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO
                )
            } else {
                fileAudioName = it.externalCacheDir?.absolutePath.toString() + "/$randomNum.3gp"
                audioInString = fileAudioName.toString()
                micSelected.invoke(audioInString)
                Log.d("filename", fileAudioName!!)

                recorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(fileAudioName)

                    try {
                        prepare()
                        start()
//                        binding.imageButtonVoice.setImageResource(R.drawable.mic_off48px)
                        Toast.makeText(it, "Началась запись голоса", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        Log.d("audioException", e.message.toString())
                        Toast.makeText(it, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun chooseImage() {
        getImageFromGallery.launch(arrayOf("image/*"))
    }

    private fun openCamera() {
        activity?.let {
            if (it.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || it.checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_DENIED
            ) {
                currentPermission = TypeOfPermission.CAMERA
                requestt.launch(
                    arrayOf(
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                currentPermission = TypeOfPermission.WRITE_EXTERNAL
            }
            val timeStamp = SimpleDateFormat("yyyyMMddHHmmSS").format(Date())
            val storageDir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                "NotesPhotos"
            ).apply { mkdir() }
            val imageFile = File(
                storageDir,
                "image".plus(Calendar.getInstance().timeInMillis).plus(timeStamp).plus(".jpg")
            ).apply {
                createNewFile()
                if (parentFile?.exists()!!) {
                    parentFile?.mkdirs()
                }
                if (exists()) {
                    mkdirs()
                }
            }
            cameraUri = FileProvider.getUriForFile(
                it,
                providerName,
                imageFile
            )
            lifecycleScope.launch {
                getImageFromCamera.launch(cameraUri)
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
                holder.itemView.setOnClickListener {
                    clickListener?.invoke(holder)
                }
                holder.itemView.setOnLongClickListener {
                    onLongClickListener?.invoke(holder)
                    return@setOnLongClickListener true
                }
                return holder
            }


            override fun onBindViewHolder(
                holder: BaseViewHolder<PhotoItemBinding>, position: Int
            ) {
                super.onBindViewHolder(holder, position)

                holder.binding.apply {
                    val item = getItem(position)
                    Glide.with(this@MakeANoteSheet).load(item.path).into(image)
                }
            }
        }.apply {
            this@MakeANoteSheet.binding.photoList.adapter = this
            this@MakeANoteSheet.binding.photoList.apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }

}