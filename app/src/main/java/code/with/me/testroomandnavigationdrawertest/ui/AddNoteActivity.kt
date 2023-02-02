package code.with.me.testroomandnavigationdrawertest.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityAddNoteBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class AddNoteActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private var camera_uri: Uri? = null
    private var cameraInString: String = ""
    private var audioInString: String = ""
    private var paintInString: String = ""
    private var imageInString: String = ""

    private var RECORD_AUDIO: Int = 0

    private val randomNum = System.currentTimeMillis()/1000

    private var fileAudioName: String? = null

    private var recorder: MediaRecorder? = null

    private lateinit var binding: ActivityAddNoteBinding

    private var isClickedToColorPicker: Boolean = false

    private var color: Array<String> = arrayOf("FFFFFFFF","EF9A9A","F8BBD0","E1BEE7", "FC85AE", "C5CAE9", "42A5F5", "26C6DA", "4DB6AC",
    "81C784", "8BC34A", "DCEDC8","F7FE49", "FFF176", "FFE082","FFCC80", "FF8A65", "D7CCC8", "BDBDBD", "B0BEC5")
    private var colornames: Array<String> = arrayOf("White","Red", "Pink", "Purple", "Deep Purple", "Indigo", "Blue", "Light Blue", "Cyan",
    "Teal", "Green", "Light Green", "Lime", "Yellow", "Amber", "Orange", "Deep Orange", "Brown", "Grey", "Blue Grey")
    private var pickedColor: String = "#FFFFFFFF"

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NotesApplication).repo)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Новая заметка"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.imageButtonDraw.setOnClickListener {
            getPaint.launch(Intent(this, PaintActivity::class.java))
        }
        binding.imageButtonVoice.setOnClickListener {
            audioRec()
        }
        binding.cardcolorpicker.setOnClickListener {
            if (!isClickedToColorPicker) {
                binding.colorpickerlayout.visibility = View.VISIBLE
                isClickedToColorPicker = true
            } else {
                binding.colorpickerlayout.visibility = View.INVISIBLE
                isClickedToColorPicker = false
            }
        }
        binding.colorpicker.minValue = 0
        binding.colorpicker.maxValue = colornames.size - 1
        binding.colorpicker.displayedValues = colornames
        binding.colorpicker.descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
        binding.colorpicker.wrapSelectorWheel = false
        binding.colorpicker.setOnValueChangedListener { _, _, _ ->
            binding.colorpickercardView.setCardBackgroundColor(Color.parseColor("#"+color[binding.colorpicker.value]))
            binding.layouttocolor.setBackgroundColor(Color.parseColor("#"+color[binding.colorpicker.value]))
            binding.colorpicker.textSize = 50F
            pickedColor = "#"+color[binding.colorpicker.value]
        }

        binding.cameraBtn.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                //permission was not enabled
                val permission = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                //show popup to request permission
                requestPermissions(permission, PERMISSION_CODE)
            } else {
                //permission already granter
                openCamera()
            }
        }
        binding.imageBtn.setOnClickListener {
            chooseImage()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun audioRec() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf( Manifest.permission.RECORD_AUDIO),
                RECORD_AUDIO
            )
        } else {

            fileAudioName = externalCacheDir?.absolutePath.toString() + "/$randomNum.3gp"
            audioInString = fileAudioName.toString()
            Log.d("filename", fileAudioName!!)
            @Suppress("DEPRECATION")
            recorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                setOutputFile(fileAudioName)

                try {
                    prepare()
                } catch (e: IOException) {
                    Log.d("audioException", e.message.toString())
                }
                Toast.makeText(this@AddNoteActivity, "record start", Toast.LENGTH_SHORT).show()
                start()
            }
        }
    }

    private fun chooseImage() {
        getImageFromGallery.launch(arrayOf("image/*"))
    }

    private val getImageFromGallery = registerForActivityResult(ActivityResultContracts.OpenDocument()) { imageUri ->
        if (imageUri != null) {
            this.contentResolver.takePersistableUriPermission(imageUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
        imageInString = imageUri.toString()
        Picasso.get().load(imageUri).resize(300, 450).centerCrop().transform(RoundedCornersTransformation(36,32)).into(binding.imageView)
        Log.d("testImage", imageUri.toString())
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun openCamera() {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmSS").format(Date())
        val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "NotesPhotos")
        storageDir.mkdir()
        val imageFile = File(storageDir, "image".plus(Calendar.getInstance().timeInMillis).plus(timeStamp).plus(".jpg"))
        imageFile.createNewFile()
        if (!imageFile.parentFile?.exists()!!) {
            imageFile.parentFile?.mkdirs()
        }
        if (!imageFile.exists()) {
            imageFile.mkdirs()
        }
        camera_uri = FileProvider.getUriForFile(this, "code.with.me.testroomandnavigationdrawertest.ui.AddNoteActivity.provider", imageFile)
        GlobalScope.launch(Dispatchers.IO) {
            getImageFromCamera.launch(camera_uri)
        }



//        val values = ContentValues()
//        values.put(MediaStore.Images.Media.TITLE, ".nomedia")
//        values.put(MediaStore.Images.Media.DESCRIPTION, ".nomedia")
//        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
////            values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + ".nomedia")
//        }
//        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//
//        //camera intent
//        GlobalScope.launch(Dispatchers.IO) {
//            getImageFromCamera.launch(image_uri)
//        }
    }
    private val getImageFromCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {
    Log.d("image_uri", camera_uri!!.encodedPath.toString())
        if (it) {
            MediaStore.Images.Media.getBitmap(this.contentResolver, camera_uri)
            val test = camera_uri.toString()
            cameraInString = test
            Picasso.get().load(camera_uri).resize(300, 450).centerCrop()
                .transform(RoundedCornersTransformation(36, 32)).into(binding.cameraView)

        }
    }
    private val getPaint = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val paintBitmap = it.data?.extras?.getString("pathBitmap")
        if (paintBitmap != null) {
            paintInString = paintBitmap
            Picasso.get().load(paintBitmap).resize(300, 450).centerCrop().transform(RoundedCornersTransformation(36,32)).into(binding.paintImage)
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
        when(requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //perm from popup was granted
                    openCamera()
                } else {
                    // perm from popup was denied
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
            if (binding.titleEdit.text.isEmpty()) {
                binding.titleEdit.error = "Необходимо ввести хотя бы заголовок"
            } else {
                val title = binding.titleEdit.text.toString()
                val text = binding.textEdit.text.toString()
                val test = Note(0, title, text, cameraInString, audioInString, paintInString, imageInString, pickedColor)
                noteViewModel.insert(test)
                binding.titleEdit.text = null
                binding.textEdit.text = null
                binding.cameraView.setImageURI(null)
                startActivity(Intent(this, MainActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


}