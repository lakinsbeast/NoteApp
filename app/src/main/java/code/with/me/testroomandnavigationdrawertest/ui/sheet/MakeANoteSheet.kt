package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.Manifest
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
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.getDisplayMetrics
import code.with.me.testroomandnavigationdrawertest.Utils.setCheckable
import code.with.me.testroomandnavigationdrawertest.Utils.setRoundedCorners
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityAddNoteBinding
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteState
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.PaintActivity
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionNote
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
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


class MakeANoteSheet : BaseSheet<ActivityAddNoteBinding>(ActivityAddNoteBinding::inflate),
    CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Job()

    var currentPermission: TypeOfPermission = TypeOfPermission.EMPTY
    private var stateOfAudioRecorder = false
    private val PERMISSION_CODE = 1000
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

    private inline fun onGetImageSelected(crossinline get: (String) -> Unit) {
        chooseImage()
        imageSelected = {
            get.invoke(it)
        }
    }

    private inline fun onGetCameraSelected(crossinline get: (String) -> Unit) {
        openCamera()
        cameraSelected = {
            get.invoke(it)
        }
    }

    private inline fun onGetMicSelected(crossinline get: (String) -> Unit) {
        micSelected = {
            get.invoke(it)
        }
    }

    private inline fun onGetDrawSelected(get: () -> Unit) {
        getPaint.launch(Intent(activity, PaintActivity::class.java))
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
                    saveNote()
                }
            }
        }
    private val getPaint =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val paintBitmap = it.data?.extras?.getString("pathBitmap")
            if (paintBitmap != null) {
                paintInString = paintBitmap
                listOfPhotos.add(PhotoModel(paintInString))
                adapter.submitList(listOfPhotos)
                saveNote()
            } else {
                throw java.lang.Exception("Не удалось получить данные paintActivity")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
        setFullScreenSheet()
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
    }

    private fun handleUserActionState(state: UserActionNote) {
        binding.apply {
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

                }

                is UserActionNote.SaveNoteToDB<*> -> {

                }
            }
        }
    }

    private fun handleViewState(state: NoteState) {
        binding.apply {
            when (state) {
                is NoteState.Loading -> {
                    println("LOADING!")
                }

                is NoteState.Result<*> -> {
                    println("LOADING!")
                }

                is NoteState.EmptyResult -> {

                }

                is NoteState.Error<*> -> {

                }
            }
        }
    }

    private fun sendViewAction(action: UserActionNote) {
        noteViewModel.processUserActions(action)
    }

//    private fun sendStateAction(action: NoteState) {
//        noteViewModel.processNoteStates(action)
//    }


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
        noteViewModel.state.observe(this) { state ->
            handleViewState(state)
        }
        noteViewModel.userActionState.observe(this) { state ->
            handleUserActionState(state)
        }
    }

    private fun initSheetCallbacks() {
        onSlide = {
//            println("onSLide: $it")
        }
        onStateChanged = {
            when (it) {
                4 -> {
                    setBottomNavHalfScreen(it)
                }

                3 -> {
                    setBottomNavFullScreen(it)
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.imageBtn -> {
                    println("first!")
                    sendViewAction(UserActionNote.GetImage)
//                    onGetImageSelected {
//                        println("second!")
//                    }
                    true
                }

                R.id.camera_btn -> {
                    sendViewAction(UserActionNote.GetCamera)
//                    onGetCameraSelected {
//
//                    }
                    true
                }

                R.id.imageButtonVoice -> {
                    sendViewAction(UserActionNote.GetMicrophone)
//                    onGetMicSelected {
//
//                    }
                    true
                }

                R.id.imageButtonDraw -> {
                    sendViewAction(UserActionNote.GetDraw)
//                    onGetDrawSelected {
//
//                    }
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
        val layParams =
            binding.bottomNavigation.layoutParams as ViewGroup.MarginLayoutParams
        layParams.bottomMargin = 40
        binding.bottomNavigation.layoutParams = layParams
        behavior?.state = state
    }

    private fun setBottomNavHalfScreen(state: Int) {
        val layParams =
            binding.bottomNavigation.layoutParams as ViewGroup.MarginLayoutParams
        layParams.bottomMargin = (getDisplayMetrics(activity()).heightPixels / 2) + 40
        binding.bottomNavigation.layoutParams = layParams
        behavior?.state = state
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

    fun chooseImage() {
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
                "code.with.me.testroomandnavigationdrawertest.ui.AddNoteActivity.provider",
                imageFile
            )
            lifecycleScope.launch {
                getImageFromCamera.launch(cameraUri)
            }
        }
    }

    fun initAdapter() {
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
                    this.image.setImageURI(Uri.parse(item.path))
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