package code.with.me.testroomandnavigationdrawertest.Activities

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
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentDetailBinding
import com.squareup.picasso.Picasso
import jp.wasabeef.picasso.transformations.RoundedCornersTransformation
import java.io.IOException
import java.util.concurrent.TimeUnit


@Suppress("DEPRECATION")
class DetailFragment : Fragment() {

    //первоначально присвоите переменной _binding значение null и
    //когда view снова уничтожается, оно должно быть установлено в значение null
    private var _binding: FragmentDetailBinding? = null

    private var title: String = ""
    private var titleEdited: String = ""
    private var text: String = ""
    private var textEdited: String = ""
    private var idS: Int = 0
    private var cameraImgPath: String = ""
    private var audioPath: String = ""
    private var paintPath: String = ""
    private var imagePath: String = ""
    private var pickedColorPath: String = ""
    private var isPlay: Boolean = true


    // пришлось добавить, чтоб не было ошибки outofboundexception, который фиксился только добавлением эти массивов
    private val idsList = ArrayList<Int>()
    private val titlesList = ArrayList<String>()
    private val textList = ArrayList<String>()
    private var cameraInRecycler = ArrayList<String>()
    private var audioInRecycler = ArrayList<String>()
    private var paintInRecycler = ArrayList<String>()
    private var imageInRecycler = ArrayList<String>()
    private var pickedColorInRecycler = ArrayList<String>()

    //

    private var mediaPlayer: MediaPlayer? = null

    private val binding get() = _binding!!

    private val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((this.activity?.application as NotesApplication).repo)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(R.transition.change_text_transform)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)

//        setHasOptionsMenu(true)
        //((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar)
//        (activity as AppCompatActivity?)!!.setSupportActionBar(binding.toolbar)
//        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayShowTitleEnabled(false)
//        (activity as AppCompatActivity?)!!.supportActionBar?.setDisplayHomeAsUpEnabled(true)


        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.playAudio.visibility = View.INVISIBLE
        binding.cameraImg.background = null
        binding.paintImage.background = null

        // menu
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.inflateMenu(R.menu.detailnotemenu)
        binding.toolbar.setOnMenuItemClickListener { item->
            when (item.itemId) {
                android.R.id.home -> {
                    startActivity(Intent(requireActivity().baseContext, MainActivity::class.java))
                }
                R.id.updateBtn -> {
                    titleEdited = binding.titleEditText.text.toString()
                    textEdited = binding.textEditText.text.toString()
                    val updNote = Note(idS,titleEdited, textEdited, cameraImgPath, audioPath, paintPath, imagePath, pickedColorPath)
                    noteViewModel.update(updNote)
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                }
                R.id.deleteBtn -> {
                    val alrtDlg = AlertDialog.Builder(requireActivity())
                    alrtDlg.setTitle("Удалить?")
                        .setPositiveButton("Да") { dialogInterface: DialogInterface, i: Int ->

                            val dltNote =
                                Note(idS, title, text, cameraImgPath, audioPath, paintPath, imagePath, pickedColorPath)
                            noteViewModel.delete(dltNote)
                            val fileImage = DocumentFile.fromSingleUri(requireActivity(), Uri.parse(cameraImgPath))
                            val filePaint = DocumentFile.fromSingleUri(requireActivity(), Uri.parse(paintPath))
                            if (fileImage != null && cameraImgPath.isNotEmpty()) {
                                requireActivity().contentResolver.delete(Uri.parse(cameraImgPath), null, null)
                            }
                            if (filePaint != null && paintPath.isNotEmpty()) {
                                requireActivity().contentResolver.delete(Uri.parse(paintPath), null, null)
                            }

                            startActivity(Intent(requireActivity(), MainActivity::class.java))

                        }.setNegativeButton("Нет") { DialogInterface: DialogInterface, i: Int ->
                            Toast.makeText(requireActivity(), "Вы выбрали  \"нет\" ", Toast.LENGTH_SHORT).show()
                        }.show()
                }
            }
            true
        }
        //

        val idIntent = this.requireArguments().getInt("num")

        noteViewModel.allNotes.observe(viewLifecycleOwner) {
            it.forEach { i ->
                if (!(i.id in idsList && i.titleNote in titlesList && i.textNote in textList
                            && i.imageById in cameraInRecycler && i.audioUrl in audioInRecycler && i.paintUrl in paintInRecycler
                            && i.imgFrmGlrUrl in imageInRecycler && i.colorCard in pickedColorInRecycler)) {
                    idsList.add(i.id)
                    titlesList.add(i.titleNote)
                    textList.add(i.textNote)
                    cameraInRecycler.add(i.imageById)
                    audioInRecycler.add(i.audioUrl)
                    paintInRecycler.add(i.paintUrl)
                    imageInRecycler.add(i.imgFrmGlrUrl)
                    pickedColorInRecycler.add(i.colorCard)
                }
            }
            idS = idsList[idIntent]
            cameraImgPath = cameraInRecycler[idIntent]
            imagePath = imageInRecycler[idIntent]
            if (audioInRecycler[idIntent].isNotEmpty()) {
                audioPath = audioInRecycler[idIntent]
                binding.playAudio.visibility = View.VISIBLE
            }
            if (paintInRecycler[idIntent].isNotEmpty()) {
                paintPath = paintInRecycler[idIntent]
                Picasso.get().load(paintPath).resize(350, 450).centerCrop().transform(
                    RoundedCornersTransformation(36,32)
                ).into(binding.paintImage)
            }
            binding.titleEditText.setText(titlesList[idIntent])
//            supportActionBar?.title = titlesList[idIntent]

            binding.textEditText.setText(textList[idIntent])
            if (cameraImgPath.isNotEmpty()) {
                Picasso.get().load(cameraImgPath).resize(350, 450).centerCrop().transform(
                    RoundedCornersTransformation(36,32)
                ).into(binding.cameraImg)
            }
            if (imagePath.isNotEmpty()) {
                Picasso.get().load(Uri.parse(imagePath)).resize(350, 450).centerCrop().transform(
                    RoundedCornersTransformation(36,32)
                ).into(binding.image)
            }
            pickedColorPath = pickedColorInRecycler[idIntent]
            if (pickedColorPath.isNotEmpty()) {

                binding.layoutdetail.setBackgroundColor(Color.parseColor(pickedColorPath))
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
            val intent = Intent(requireActivity(), ImageToFullScreenActivity::class.java)
            intent.putExtra("imageUrl", cameraImgPath)
            startActivity(intent)
        }
        binding.paintImage.setOnClickListener {
            val intent = Intent(requireActivity(), ImageToFullScreenActivity::class.java)
            intent.putExtra("imageUrl", paintPath)
            startActivity(intent)
        }
        binding.image.setOnClickListener {
            val intent = Intent(requireActivity(), ImageToFullScreenActivity::class.java)
            intent.putExtra("imageUrl", imagePath)
            startActivity(intent)
        }
    }
    private fun stopAudio() {
        if (mediaPlayer!!.isPlaying) {
            mediaPlayer!!.stop()
            mediaPlayer!!.reset()
            mediaPlayer!!.release()
            Toast.makeText(activity, "Голосовая заметка не проигрывается", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(activity, "Аудио не играет для паузы", Toast.LENGTH_SHORT).show()
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
        Toast.makeText(activity, "Проигрывается голосовая заметка", Toast.LENGTH_SHORT).show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}