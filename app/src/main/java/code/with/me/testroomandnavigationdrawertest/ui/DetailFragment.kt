package code.with.me.testroomandnavigationdrawertest.ui

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.data.NoteForDetailFragment
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentDetailBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.IOException

//TODO{ЗАРЕФАКТОРИТЬ ДАННЫЙ ФРАГМЕНТ}
@Suppress("DEPRECATION")
class DetailFragment : Fragment() {

    //первоначально присвоите переменной _binding значение null и
    //когда view снова уничтожается, оно должно быть установлено в значение null
    private var _binding: FragmentDetailBinding? = null

    private var title: String = ""
    private var titleEdited: String = ""
    private var text: String = ""
    private var textEdited: String = ""

//    private var idS: Int = 0
//    private var cameraImgPath: String = ""
//    private var audioPath: String = ""
//    private var paintPath: String = ""
//    private var imagePath: String = ""
//    private var pickedColorPath: String = ""

    private var isPlay: Boolean = true

    private var mediaPlayer: MediaPlayer? = null

    private val binding get() = _binding!!

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((this.activity?.application as NotesApplication).repo)
    }

    var note = NoteForDetailFragment()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(R.transition.change_text_transform)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            playAudio.visibility = View.INVISIBLE
            cameraImg.background = null
            paintImage.background = null
        }

        // menu
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
        }
        binding.toolbar.inflateMenu(R.menu.detailnotemenu)
        binding.toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                android.R.id.home -> {
                    requireActivity().supportFragmentManager.beginTransaction().remove(this)
                        .commit()
                    requireActivity().onBackPressed()
                }
                R.id.updateBtn -> {
                    binding.apply {
                        titleEdited = titleEditText.text.toString()
                        textEdited = textEditText.text.toString()
                    }
                    noteViewModel.update(
                        Note(
                            note.id,
                            titleEdited,
                            textEdited,
                            note.imgFrmGlrUrl,
                            note.audioUrl,
                            note.paintUrl,
                            note.imageById,
                            note.colorCard
                        )
                    )
                    requireActivity().supportFragmentManager.beginTransaction().remove(this)
                        .commit()
                    requireActivity().onBackPressed()
                }
                R.id.deleteBtn -> {
                    AlertDialog.Builder(requireActivity()).setTitle("Удалить?")
                        .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                            noteViewModel.delete(
                                Note(
                                    note.id,
                                    note.titleNote,
                                    note.textNote,
                                    note.imgFrmGlrUrl,
                                    note.audioUrl,
                                    note.paintUrl,
                                    note.imageById,
                                    note.colorCard
                                )
                            )
                            DocumentFile.fromSingleUri(
                                requireActivity(), Uri.parse(note.imgFrmGlrUrl)
                            ).apply {
                                if (this != null && note.imgFrmGlrUrl.isNotEmpty()) {
                                    requireActivity().contentResolver.delete(
                                        Uri.parse(note.imgFrmGlrUrl), null, null
                                    )
                                }
                            }
                            DocumentFile.fromSingleUri(requireActivity(), Uri.parse(note.paintUrl))
                                .apply {
                                    if (this != null && note.paintUrl.isNotEmpty()) {
                                        requireActivity().contentResolver.delete(
                                            Uri.parse(
                                                note.paintUrl
                                            ), null, null
                                        )
                                    }
                                }
                            requireActivity().supportFragmentManager.beginTransaction().remove(this)
                                .commit()
                            requireActivity().onBackPressed()
                        }.setNegativeButton("Нет") { _: DialogInterface, _: Int ->
                            Toast.makeText(
                                requireActivity(), "Вы выбрали  \"нет\" ", Toast.LENGTH_SHORT
                            ).show()
                        }.show()
                }
            }
            true
        }


        val idIntent = this.requireArguments().getInt("num")
        Log.d("id", idIntent.toString())
        lifecycleScope.launch {
            noteViewModel.getAll().collect {
                if (it.isNotEmpty()) {
                    note = NoteForDetailFragment(
                        it[idIntent].id,
                        it[idIntent].titleNote,
                        it[idIntent].textNote,
                        it[idIntent].imageById,
                        it[idIntent].audioUrl,
                        it[idIntent].paintUrl,
                        it[idIntent].imgFrmGlrUrl,
                        it[idIntent].colorCard
                    )
                    if (note.audioUrl.isNotEmpty()) {
                        binding.playAudio.visibility = View.VISIBLE
                    }
                    if (note.paintUrl.isNotEmpty()) {
                        Picasso.get().load(note.paintUrl).resize(350, 450).centerCrop().transform(
                            RoundedCornersTransformation(36, 32)
                        ).into(binding.paintImage)
                    }
                    binding.titleEditText.setText(note.titleNote)
                    binding.textEditText.setText(note.textNote)
                    if (note.imgFrmGlrUrl.isNotEmpty()) {
                        Picasso.get().load(note.imgFrmGlrUrl).resize(350, 450).centerCrop()
                            .transform(
                                RoundedCornersTransformation(36, 32)
                            ).into(binding.cameraImg)
                    }
                    if (note.imageById.isNotEmpty()) {
                        Picasso.get().load(Uri.parse(note.imageById)).resize(350, 450)
                            .centerCrop().transform(
                                RoundedCornersTransformation(36, 32)
                            ).into(binding.image)
                    }
                    if (note.colorCard.isNotEmpty()) {
                        binding.layoutdetail.setBackgroundColor(Color.parseColor(note.colorCard))
                    }
                }
            }
        }
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
        binding.cameraImg.setOnClickListener {
            Intent(requireActivity(), ImageToFullScreenActivity::class.java).apply {
                putExtra("imageUrl", note.imgFrmGlrUrl)
                startActivity(this)
            }
        }
        binding.paintImage.setOnClickListener {
            Intent(requireActivity(), ImageToFullScreenActivity::class.java).apply {
                putExtra("imageUrl", note.paintUrl)
                startActivity(this)
            }
        }
        binding.image.setOnClickListener {
            Intent(requireActivity(), ImageToFullScreenActivity::class.java).apply {
                putExtra("imageUrl", note.imageById)
                startActivity(this)
            }
        }
    }

    private fun stopAudio() {
        with(mediaPlayer!!) {
            if (this.isPlaying) {
                stop()
                reset()
                release()
                Toast.makeText(activity, "Голосовая заметка не проигрывается", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(activity, "Аудио не играет для паузы", Toast.LENGTH_SHORT).show()
            }
        }
//
//        if (mediaPlayer!!.isPlaying) {
//            mediaPlayer!!.stop()
//            mediaPlayer!!.reset()
//            mediaPlayer!!.release()
//            Toast.makeText(activity, "Голосовая заметка не проигрывается", Toast.LENGTH_SHORT)
//                .show()
//        } else {
//            Toast.makeText(activity, "Аудио не играет для паузы", Toast.LENGTH_SHORT).show()
//        }
    }

    private fun playAudio() {
        mediaPlayer = MediaPlayer().apply {
            setAudioStreamType(AudioManager.STREAM_MUSIC)
            try {
                setDataSource(note.audioUrl)
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
//        mediaPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)
//        try {
//            mediaPlayer!!.setDataSource(audioPath)
//            mediaPlayer!!.prepare()
//            mediaPlayer!!.start()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
        Toast.makeText(activity, "Проигрывается голосовая заметка", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}