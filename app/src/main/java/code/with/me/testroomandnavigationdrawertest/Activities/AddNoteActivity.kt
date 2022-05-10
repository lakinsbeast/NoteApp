package code.with.me.testroomandnavigationdrawertest.Activities

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityAddNoteBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.IOException

@Suppress("DEPRECATION")
class AddNoteActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private var image_uri: Uri? = null
    private var camera_uri: Uri? = null
    private var cameraInString: String = ""
    private var audioInString: String = ""
    private var paintInString: String = ""
    private var imageInString: String = ""

    private val IMAGE_CAPTURE_CODE  = 1001

    private var RECORD_AUDIO: Int = 0

    private val randomNum = System.currentTimeMillis()/1000

    private var fileAudioName: String? = null

    private var recorder: MediaRecorder? = null

    private lateinit var binding: ActivityAddNoteBinding

    val noteViewModel: NoteViewModel by viewModels {
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

        binding.cameraBtn.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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
            } else {
                // system os is < marshmallow
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
                arrayOf( Manifest.permission.RECORD_AUDIO ),
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
        this.contentResolver.takePersistableUriPermission(imageUri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        imageInString = imageUri.toString()
        Picasso.get().load(imageUri).resize(300, 450).centerCrop().transform(RoundedCornersTransformation(36,32)).into(binding.imageView)
        Log.d("testImage", imageUri.toString())
    }

    private fun openCamera() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, ".nomedia")
        values.put(MediaStore.Images.Media.DESCRIPTION, ".nomedia")
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/" + "NOTESBYEBLAN")
        }
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        //camera intent
        getImageFromCamera.launch(image_uri)
    }
    private val getImageFromCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) {
        MediaStore.Images.Media.getBitmap(this.contentResolver, image_uri)
        val test = image_uri.toString()
        cameraInString = test
        Picasso.get().load(image_uri).resize(300, 450).centerCrop().transform(RoundedCornersTransformation(36,32)).into(binding.cameraView)
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
                val test = Note(0, title, text, cameraInString, audioInString, paintInString, imageInString)
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