package code.with.me.testroomandnavigationdrawertest.ui

import android.Manifest
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.graphics.Color
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.di.AppComponent
import code.with.me.testroomandnavigationdrawertest.data.di.DaggerAppComponent
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityAddNoteBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Named


class AddNoteActivity : AppCompatActivity() {

    private var stateOfAudioRecorder = false
    private val PERMISSION_CODE = 1000
    private var cameraUri: Uri? = null
    private var cameraInString: String = ""
    private var audioInString: String = ""
    private var paintInString: String = ""
    private var imageInString: String = ""

    private var RECORD_AUDIO: Int = 0

    private val randomNum = System.currentTimeMillis() / 1000

    private var fileAudioName: String? = null

    private var recorder: MediaRecorder? = null

    private lateinit var binding: ActivityAddNoteBinding

    private var isClickedToColorPicker: Boolean = false

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
    private var pickedColor: String = "#FFFFFFFF"

    @Inject
    @Named("noteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteViewModel: NoteViewModel

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        val appComponent = (application as NotesApplication).appComponent
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Новая заметка"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        binding.apply {
            imageButtonDraw.setOnClickListener {
                getPaint.launch(Intent(this@AddNoteActivity, PaintActivity::class.java))
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
            colorpicker.apply {
                minValue = 0
                maxValue = colornames.size - 1
                displayedValues = colornames
                descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
                wrapSelectorWheel = false
                setOnValueChangedListener { _, _, _ ->
                    colorpickercardView.setCardBackgroundColor(Color.parseColor("#" + color[colorpicker.value]))
                    textSize = 50F
                    layouttocolor.setBackgroundColor(Color.parseColor("#" + color[colorpicker.value]))
                    pickedColor = "#" + color[value]
                }
            }
            cameraBtn.setOnClickListener {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED
                ) {
                    //permission was not enabled
                    val permission =
                        arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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

    private fun stopAudio() {
        try {
            recorder?.apply {
                stop()
                release()
            }
            binding.imageButtonVoice.setImageResource(R.drawable.ic_baseline_keyboard_voice_24)
            Toast.makeText(this, "Запись голоса завершена", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Log.d("audioRecorder", e.message.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun audioRec() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO
            )
        } else {
            fileAudioName = externalCacheDir?.absolutePath.toString() + "/$randomNum.3gp"
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
                    Toast.makeText(this@AddNoteActivity, "Началась запись голоса", Toast.LENGTH_SHORT)
                        .show()
                } catch (e: IOException) {
                    Log.d("audioException", e.message.toString())
                    Toast.makeText(this@AddNoteActivity, e.message.toString(), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    private fun chooseImage() {
        getImageFromGallery.launch(arrayOf("image/*"))
    }

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { imageUri ->
            if (imageUri != null) {
                this.contentResolver.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            }
            imageInString = imageUri.toString()
            Picasso.get().load(imageUri).resize(300, 450).centerCrop()
                .transform(RoundedCornersTransformation(36, 32)).into(binding.imageView)
        }

    private fun openCamera() {
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
            this,
            "code.with.me.testroomandnavigationdrawertest.ui.AddNoteActivity.provider",
            imageFile
        )
        lifecycleScope.launch {
            getImageFromCamera.launch(cameraUri)
        }
    }

    private val getImageFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.d("image_uri", cameraUri!!.encodedPath.toString())
            if (it) {
                MediaStore.Images.Media.getBitmap(this.contentResolver, cameraUri)
                val test = cameraUri.toString()
                cameraInString = test
                Picasso.get().load(cameraUri).resize(300, 450).centerCrop()
                    .transform(RoundedCornersTransformation(36, 32)).into(binding.cameraView)

            }
        }
    private val getPaint =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val paintBitmap = it.data?.extras?.getString("pathBitmap")
            if (paintBitmap != null) {
                paintInString = paintBitmap
                Picasso.get().load(paintBitmap).resize(300, 450).centerCrop()
                    .transform(RoundedCornersTransformation(36, 32)).into(binding.paintImage)
            } else {
                Log.d("actforresult", "actforres не сработал")
            }
        }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbarmenu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        if (item.itemId == R.id.saveBtn) {
//            val data = Note(
//                0,
//                "binding.titleEdit.text.toString()",
//                binding.textEdit.text.toString(),
//                cameraInString,
//                audioInString,
//                paintInString,
//                imageInString,
//                pickedColor,
//                0,
//                System.currentTimeMillis(),
//                System.currentTimeMillis(),
//                0,
//                false
//            )
//            if (binding.titleEdit.text.isEmpty()) {
//                binding.titleEdit.error = "Необходимо ввести хотя бы заголовок"
//            } else {
//                try {
//                    noteViewModel.insert(data)
//                } catch (e: SQLiteException) {
//                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
//                } catch (e: Exception) {
//                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
//                } finally {
//                    startActivity(Intent(this, MainActivity::class.java))
//                    finish()
//                }
//            }
        }
        return super.onOptionsItemSelected(item)
    }


}