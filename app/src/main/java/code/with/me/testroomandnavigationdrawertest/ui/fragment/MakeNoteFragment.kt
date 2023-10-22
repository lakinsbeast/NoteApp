package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.AlertCreator
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.PermissionController
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.Utils.mainScope
import code.with.me.testroomandnavigationdrawertest.data.Utils.println
import code.with.me.testroomandnavigationdrawertest.data.Utils.setCheckable
import code.with.me.testroomandnavigationdrawertest.data.Utils.setRoundedCorners
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityAddNoteBinding
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.file.FilesController
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AudioRecorderDialog
import code.with.me.testroomandnavigationdrawertest.ui.sheet.PaintSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MakeNoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionNote
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.Stack
import javax.inject.Inject
import javax.inject.Named


class MakeNoteFragment : BaseFragment<ActivityAddNoteBinding>(ActivityAddNoteBinding::inflate) {
    private var cameraUri: Uri? = null

    @Inject
    @Named("makeNoteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var makeNoteViewModel: MakeNoteViewModel

    lateinit var adapter: BaseAdapter<PhotoModel, PhotoItemBinding>
    private lateinit var photoItem: PhotoItemBinding

    @Inject
    lateinit var filesController: FilesController


    private fun openPaintSheet() {
        val paintSheet = PaintSheet()
        (activity as MainActivity).let { activity ->
            activity.sheetController.showSheet(activity, paintSheet)
        }
    }

    //попробовать сделать UUID вместо случайных названий фоток
    private var request =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            makeNoteViewModel.currentPermission.observe(viewLifecycleOwner) { currentPermission ->
                when (currentPermission) {
                    MakeNoteViewModel.Companion.TypeOfPermission.CAMERA -> {
                        activity?.let {
                            makeCameraFileAndOpenCamera(it)
                        }
                    }

                    MakeNoteViewModel.Companion.TypeOfPermission.IMAGE_GALLERY -> {
                        chooseImage()
                    }

                    MakeNoteViewModel.Companion.TypeOfPermission.AUDIO -> {
//                    audioRec()
                    }

                    else -> {
                        println("jkghsfhkjsfgd")
                    }
                }
            }
        }

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { imageUri ->
            if (imageUri != null) {
                activity?.contentResolver?.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                makeNoteViewModel.getImageFromGallery(imageUri)
                saveNote()
            }
        }

    private val getImageFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) {
            if (it) {
                activity?.let {
                    MediaStore.Images.Media.getBitmap(it.contentResolver, cameraUri)
                    makeNoteViewModel.getImageFromCamera(cameraUri.toString())
                    saveNote()
                }
            }
        }

    private val requestAudioPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { it -> }

    companion object {
        const val paintResultKey = "paintResultKey"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        makeNoteViewModel =
            ViewModelProvider(this, factory)[MakeNoteViewModel::class.java]
        listenPaintSheetResult()
        //TODO иногда не создает в папках заметки
        if (arguments?.getInt("idFolder") != null && arguments?.getInt("idFolder") != -1) {
            makeNoteViewModel.setFolderId(arguments?.getInt("idFolder")!!)
        } else {
            println("idFolder is null!")
        }

        if (arguments?.getInt("noteId") != null && arguments?.getInt("noteId") != -1) {
            makeNoteViewModel.noteId = arguments?.getInt("noteId")!!
        } else {
            makeNoteViewModel.insertNote()
        }
    }

    private fun listenPaintSheetResult() {
        setFragmentResultListener(paintResultKey) { requestKey: String, bundle: Bundle ->
            val paintBitmap = bundle.getString("pathBitmap")
            if (paintBitmap != null) {
                makeNoteViewModel.setPaintSheetResult(paintBitmap)
                saveNote()
            } else {
                throw java.lang.Exception("Не удалось получить данные paintActivity")
            }
        }
    }

    private fun saveNote() {
        makeNoteViewModel.saveNote()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initAdapter()
        initClickListeners()
        initOnEditorActionListener()
        initViewModel()
        initTextChangeListeners()
        setBottomMarginBottomNav(20)
    }

    private fun initOnEditorActionListener() {
        binding.apply {

            //not work
            textEdit.setOnEditorActionListener { v, actionId, event ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE -> {
                        println("NEW LINE!")
                        textEdit.append("\n")
                        true
                    }

                    EditorInfo.IME_ACTION_NEXT -> {
                        println("NEW LINE!")
                        textEdit.append("\n")
                        true
                    }

                    else -> {
                        false
                    }
                }
            }
        }
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
                if (!PermissionController.checkAudioPermission(activity())) {
                    AlertCreator.createAudioGivePermissionDialog(activity()) {
                        if (it) {
                            try {
                                requestAudioPermission.launch(Manifest.permission.RECORD_AUDIO)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                } else {
                    openAudioRecordDialog()
                }
            }

            is UserActionNote.GetDraw -> {
                openPaintSheet()
            }

            is UserActionNote.SavedNoteToDB -> {}
            else -> {}
        }
    }

    private fun openAudioRecordDialog() {
        AudioRecorderDialog(activity()) {
            makeNoteViewModel.audioInString = it
        }.show()
    }

    private fun sendViewAction(action: UserActionNote) {
        makeNoteViewModel.processUserActions(action)
    }

    private fun initTextChangeListeners() {
        binding.apply {
            titleEdit.addTextChangedListener {
//                makeNoteViewModel.titleText = it.toString()
                makeNoteViewModel.setTitleText(it.toString())
            }
            textEdit.addTextChangedListener {
                makeNoteViewModel.setText(it.toString())
            }
        }
    }

    private fun initViewModel() {
        makeNoteViewModel.userActionState.observe(viewLifecycleOwner) { state ->
            handleUserActionState(state)
        }
        makeNoteViewModel.updateAdapterObserver.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            adapter.notifyDataSetChanged()
        }
        makeNoteViewModel.titleText.observe(viewLifecycleOwner) {
            if (it != binding.titleEdit.text.toString()) {
                binding.titleEdit.setText(it)
            }
            makeNoteViewModel.title = it
        }
        makeNoteViewModel.text.observe(viewLifecycleOwner) {
            if (it != binding.textEdit.text.toString()) {
                binding.textEdit.setText(it)
            }
            makeNoteViewModel.textNote = it
        }
        makeNoteViewModel.noteObs.observe(viewLifecycleOwner) {
            binding.apply {
                titleEdit.setText(it.titleNote)
                textEdit.setText(it.textNote)
                adapter.submitList(it.listOfImages.toMutableList())
                adapter.notifyDataSetChanged()

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


    private fun setBottomMarginBottomNav(animatedValue: Int) {
        val layParams =
            binding.bottomNavigation.layoutParams as ViewGroup.MarginLayoutParams
        layParams.bottomMargin = animatedValue
        binding.bottomNavigation.layoutParams = layParams
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
                makeNoteViewModel.setPermission(MakeNoteViewModel.Companion.TypeOfPermission.CAMERA)
//                currentPermission = TypeOfPermission.CAMERA
                request.launch(
                    arrayOf(
                        Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            } else {
                makeCameraFileAndOpenCamera(it)
            }
        }
    }

    //пока не знаю как быть с этой функцией, думаю какую-то часть перенести во вьюмодельку, но нужно будет инжектить filesController
    private fun makeCameraFileAndOpenCamera(it: FragmentActivity): Job {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmSS", Locale.getDefault()).format(Date())

        val file = filesController.saveToInternalStorage(
            it,
            "image_${Calendar.getInstance().timeInMillis}_${timeStamp}.jpg"
        )
        file?.createNewFile()
        cameraUri = filesController.getUriForFile(
            it,
            file!!
        )
        return lifecycleScope.launch {
            getImageFromCamera.launch(
                cameraUri
            )
        }
    }

    private fun initAdapter() {
        adapter = object : BaseAdapter<PhotoModel, PhotoItemBinding>(photoItem) {
            private var selected0 = -1

//            init {
//                clickListener = {
//                    selected0 = it.layoutPosition
//                    val item = getItem(it.layoutPosition) as PhotoModel
////                    openDetailFragment(item.id)
//                }
//                onLongClickListener = {
//                    selected0 = it.layoutPosition
//                    val item = getItem(it.layoutPosition) as PhotoModel
//                }
//            }

            override fun onCreateViewHolder(
                parent: ViewGroup, viewType: Int
            ): BaseViewHolder<PhotoItemBinding> {
                val binding =
                    PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val holder = BaseViewHolder(binding)
//                holder.itemView.setOnClickListener {
//                    clickListener?.invoke(holder)
//                }
//                holder.itemView.setOnLongClickListener {
//                    onLongClickListener?.invoke(holder)
//                    return@setOnLongClickListener true
//                }
                return holder
            }


            override fun onBindViewHolder(
                holder: BaseViewHolder<PhotoItemBinding>, position: Int
            ) {
                super.onBindViewHolder(holder, position)

                holder.binding.apply {
                    val item = getItem(position)
                    Glide.with(this@MakeNoteFragment).load(item.path).into(image)
                }
            }
        }.apply {
            this@MakeNoteFragment.binding.photoList.adapter = this
            this@MakeNoteFragment.binding.photoList.apply {
                setHasFixedSize(false)
                layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
            }
        }
    }


}