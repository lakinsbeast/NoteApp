package code.with.me.testroomandnavigationdrawertest.Activities

import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityDetailBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.IOException


@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding:  ActivityDetailBinding
    private var title: String = ""
    private var titleEdited: String = ""
    private var text: String = ""
    private var textEdited: String = ""
    private var id: Int = 0
    private var imagePath: String = ""
    private var audioPath: String = ""
    private var paintPath: String = ""
    private var isPlay: Boolean = true


    // пришлось добавить, чтоб не было ошибки outofboundexception, который фиксился только добавлением эти массивов
    private val idsList = ArrayList<Int>()
    private val titlesList = ArrayList<String>()
    private val textList = ArrayList<String>()
    private var imageInRecycler = ArrayList<String>()
    private var audioInRecycler = ArrayList<String>()
    private var paintInRecycler = ArrayList<String>()
    //

    private var mediaPlayer: MediaPlayer? = null

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NotesApplication).repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.playAudio.visibility = View.INVISIBLE
        binding.image.background = null
        binding.paintImage.background = null
        val idIntent = intent.getIntExtra("id", 0)

        noteViewModel.allNotes.observe(this) {
//            title = it[idIntent].titleNote
//            text = it[idIntent].textNote
//            id = it[idIntent].id
//            imagePath = it[idIntent].imageById
            it.forEach { i ->
                if (!(i.id in idsList && i.titleNote in titlesList && i.textNote in textList
                            && i.imageById in imageInRecycler && i.audioUrl in audioInRecycler && i.paintUrl in paintInRecycler)) {
                    idsList.add(i.id)
                    titlesList.add(i.titleNote)
                    textList.add(i.textNote)
                    imageInRecycler.add(i.imageById)
                    audioInRecycler.add(i.audioUrl)
                    paintInRecycler.add(i.paintUrl)
                }
            }
            id = idsList[idIntent]
            imagePath = imageInRecycler[idIntent]
            if (audioInRecycler[idIntent].isNotEmpty()) {
                audioPath = audioInRecycler[idIntent]
                binding.playAudio.visibility = View.VISIBLE
            }
            if (paintInRecycler[idIntent].isNotEmpty()) {
                paintPath = paintInRecycler[idIntent]
                Picasso.get().load(paintPath).resize(350, 450).centerCrop().transform(RoundedCornersTransformation(36,32)).into(binding.paintImage)
            }
            binding.titleEditText.setText(titlesList[idIntent])
            supportActionBar?.title = titlesList[idIntent]
            binding.textEditText.setText(textList[idIntent])
            if (imagePath.isNotEmpty()) {
                Picasso.get().load(imagePath).resize(350, 450).centerCrop().transform(RoundedCornersTransformation(36,32)).into(binding.image)
            }
        }
//        binding.updBtn.setOnClickListener {
//            titleEdited = binding.titleEditText.text.toString()
//            textEdited = binding.textEditText.text.toString()
//            val updNote = Note(id,titleEdited, textEdited, imagePath, audioPath, paintPath)
//            noteViewModel.update(updNote)
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//        }
//        binding.dltBtn.setOnClickListener {
//            val dltNote = Note(id,title, text, imagePath, audioPath, paintPath)
//            noteViewModel.delete(dltNote)
//            val intent = Intent(this, MainActivity::class.java)
//            val fileImage = DocumentFile.fromSingleUri(this, Uri.parse(imagePath))
//            val filePaint = DocumentFile.fromSingleUri(this, Uri.parse(paintPath))
//            if (fileImage != null) {
//                contentResolver.delete(Uri.parse(imagePath), null, null)
//            }
//            if (filePaint != null) {
//                contentResolver.delete(Uri.parse(paintPath), null, null)
//            }
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
//            finish()
//        }
        binding.playAudio.setOnClickListener {
            if (isPlay) {
                playAudio()
                isPlay = false
                binding.playAudio.setImageResource(R.drawable.pause_btn)
                Log.d("isPlay", isPlay.toString())
            } else {
                stopAudio()
                binding.playAudio.setImageResource(R.drawable.play_arrow_btn)
                isPlay = true
            }
        }
        binding.image.setOnClickListener {
            val intent = Intent(this, ImageToFullScreenActivity::class.java)
            intent.putExtra("imageUrl", imagePath)
            startActivity(intent)
        }
        binding.paintImage.setOnClickListener {
            val intent = Intent(this, ImageToFullScreenActivity::class.java)
            intent.putExtra("imageUrl", paintPath)
            startActivity(intent)
        }


    }

    private fun stopAudio() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            Toast.makeText(this, "Голосовая заметка не проигрывается", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Аудио не играет для паузы", Toast.LENGTH_SHORT).show()
        }
    }

    private fun playAudio() {
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            mediaPlayer!!.setDataSource(audioPath)
            mediaPlayer!!.prepare()
            mediaPlayer!!.start()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        Toast.makeText(this, "Проигрывается голосовая заметка", Toast.LENGTH_SHORT).show()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detailnotemenu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
        if (item.itemId == R.id.updateBtn) {
            titleEdited = binding.titleEditText.text.toString()
            textEdited = binding.textEditText.text.toString()
            val updNote = Note(id,titleEdited, textEdited, imagePath, audioPath, paintPath)
            noteViewModel.update(updNote)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        if (item.itemId == R.id.deleteBtn) {
            val alrtDlg = AlertDialog.Builder(this)
            alrtDlg.setTitle("Удалить?")
                .setPositiveButton("Да") { dialogInterface: DialogInterface, i: Int ->
                    val dltNote = Note(id, title, text, imagePath, audioPath, paintPath)
                    noteViewModel.delete(dltNote)
                    val intent = Intent(this, MainActivity::class.java)
                    val fileImage = DocumentFile.fromSingleUri(this, Uri.parse(imagePath))
                    val filePaint = DocumentFile.fromSingleUri(this, Uri.parse(paintPath))
                    if (fileImage != null) {
                        contentResolver.delete(Uri.parse(imagePath), null, null)
                    }
                    if (filePaint != null) {
                        contentResolver.delete(Uri.parse(paintPath), null, null)
                    }
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                }.setNegativeButton("Нет") { DialogInterface: DialogInterface, i: Int ->
                    Toast.makeText(this, "Вы выбрали  \"нет\" ", Toast.LENGTH_SHORT).show()
                }.show()
//            val dltNote = Note(id,title, text, imagePath, audioPath, paintPath)
//            noteViewModel.delete(dltNote)
//            val intent = Intent(this, MainActivity::class.java)
//            val fileImage = DocumentFile.fromSingleUri(this, Uri.parse(imagePath))
//            val filePaint = DocumentFile.fromSingleUri(this, Uri.parse(paintPath))
//            if (fileImage != null) {
//                contentResolver.delete(Uri.parse(imagePath), null, null)
//            }
//            if (filePaint != null) {
//                contentResolver.delete(Uri.parse(paintPath), null, null)
//            }
//            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            startActivity(intent)
//            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}