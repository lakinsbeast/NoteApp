package code.with.me.testroomandnavigationdrawertest.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.graphics.Bitmap
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityAddNoteBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Timer
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext


class AddNoteFragment() : BaseFragment<ActivityAddNoteBinding>(ActivityAddNoteBinding::inflate),
    CoroutineScope {

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

    private var isClickedToColorPicker: Boolean = false

    var timer = Timer()
    var scope = CoroutineScope(Job() + Dispatchers.IO)
    val DELAY: Long = 1000

    private var pickedColor: String = "#FFFFFFFF"

    private var note: Note = Note(
        lastSaveID.toInt(),
        "",
        "",
        cameraInString,
        audioInString,
        paintInString,
        imageInString,
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

    enum class TypeOfPermission {
        CAMERA, IMAGE_GALLERY, AUDIO, WRITE_EXTERNAL, EMPTY,
    }

    private var color: Array<String> = arrayOf(
        "FFFFFFFF",
        "EF9A9A",
        "F8BBD0",
        "E1BEE7",
        "FC85AE",
        "C5CAE9",
        "42A5F5",
        "26C6DA",
        "4DB6AC",
        "81C784",
        "8BC34A",
        "DCEDC8",
        "F7FE49",
        "FFF176",
        "FFE082",
        "FFCC80",
        "FF8A65",
        "D7CCC8",
        "BDBDBD",
        "B0BEC5"
    )
    private var colornames: Array<String> = arrayOf(
        "White",
        "Red",
        "Pink",
        "Purple",
        "Deep Purple",
        "Indigo",
        "Blue",
        "Light Blue",
        "Cyan",
        "Teal",
        "Green",
        "Light Green",
        "Lime",
        "Yellow",
        "Amber",
        "Orange",
        "Deep Orange",
        "Brown",
        "Grey",
        "Blue Grey"
    )

    var requestt = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
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
                activity?.let {
                    it.contentResolver.takePersistableUriPermission(
                        imageUri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                imageInString = imageUri.toString()
                Picasso.get().load(imageUri).resize(300, 450).centerCrop()
                    .transform(RoundedCornersTransformation(36, 32)).into(binding.imageView)
                saveNote()
            }
        }
    private val getImageFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.d("image_uri", cameraUri!!.encodedPath.toString())
            if (it) {
                activity?.let {
                    MediaStore.Images.Media.getBitmap(it.contentResolver, cameraUri)
                    val test = cameraUri.toString()
                    cameraInString = test
                    Picasso.get().load(cameraUri).resize(300, 450).centerCrop()
                        .transform(RoundedCornersTransformation(36, 32)).into(binding.cameraView)
                    saveNote()
                }
            }
        }
    private val getPaint =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val paintBitmap = it.data?.extras?.getString("pathBitmap")
            if (paintBitmap != null) {
                paintInString = paintBitmap
                Picasso.get().load(paintBitmap).resize(300, 450).centerCrop()
                    .transform(RoundedCornersTransformation(36, 32)).into(binding.paintImage)
                saveNote()
            } else {
                Log.d("actforresult", "actforres не сработал")
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]
    }

    private fun saveNote() {
        if (binding.titleEdit.text.toString().isNotEmpty()
            || binding.textEdit.text.toString().isNotEmpty()
            || cameraInString.isNotEmpty()
            || paintInString.isNotEmpty()
            || imageInString.isNotEmpty()
        )
            scope.cancel()
        scope = CoroutineScope(Job() + Dispatchers.IO)
        scope.launch {
            while (true) {
                delay(150)
                note = Note(
                    lastSaveID.toInt(),
                    binding.titleEdit.text.toString(),
                    binding.textEdit.text.toString(),
                    cameraInString,
                    audioInString,
                    paintInString,
                    imageInString,
                    pickedColor,
                    arguments?.getInt("idFolder")!!,
                    lastCreatedTime,
                    lastOpenedTime,
                    false,
                    "-1"
                )
                noteViewModel.update(note)
                break
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        scope.launch {
            async {
                lastSaveID = noteViewModel.getLastCustomer()
                lastSaveID += 1
            }.await()
            launch {
                noteViewModel.insert(note)
            }
        }
        activity?.let { activity ->
            binding.apply {
                imageButtonDraw.setOnClickListener {
                    getPaint.launch(Intent(activity, PaintActivity::class.java))
                }
                titleEdit.addTextChangedListener {
                    saveNote()
                }

                textEdit.addTextChangedListener {
                    saveNote()
                }
                imageButtonVoice.setOnClickListener {
                    stateOfAudioRecorder = if (!stateOfAudioRecorder) {
                        audioRec()
                        true
                    } else {
                        stopAudio()
                        false
                    }
                }
                cardcolorpicker.setOnClickListener {
                    if (!isClickedToColorPicker) {
                        colorpickerlayout.visibility = View.VISIBLE
                        isClickedToColorPicker = true
                    } else {
                        colorpickerlayout.visibility = View.INVISIBLE
                        isClickedToColorPicker = false
                    }
                }
                cameraBtn.setOnClickListener { camera ->
                    if (activity.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || activity.checkSelfPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_DENIED
                    ) {
                        //permission was not enabled
                        val permission = arrayOf(
                            Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                        )
                        //show popup to request permission
                        requestPermissions(permission, PERMISSION_CODE)
                    } else {
                        //permission already granter
                        openCamera()
                    }
                }
                imageBtn.setOnClickListener {
                    chooseImage()
                }
            }
        }
    }


    private fun stopAudio() {
        try {
            activity?.let {
                recorder?.apply {
                    stop()
                    release()
                }
                binding.imageButtonVoice.setImageResource(R.drawable.ic_baseline_keyboard_voice_24)
                saveNote()
                Toast.makeText(it, "Запись голоса завершена", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("audioRecorder", e.message.toString())
        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
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
                Log.d("filename", fileAudioName!!)

                recorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(fileAudioName)

                    try {
                        prepare()
                        start()
                        binding.imageButtonVoice.setImageResource(R.drawable.mic_off48px)
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
                "code.with.me.testroomandnavigationdrawertest.ui.AddNoteActivity.provider",
                imageFile
            )
            lifecycleScope.launch {
                getImageFromCamera.launch(cameraUri)
            }
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job()
}
