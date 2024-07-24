package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.AlertCreator
import code.with.me.testroomandnavigationdrawertest.PermissionController
import code.with.me.testroomandnavigationdrawertest.PermissionController.checkCameraAndWriteExternalStoragePermission
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.AUDIO_PATH
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.PAINT_KEY_RESULT
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.data.utils.setCheckable
import code.with.me.testroomandnavigationdrawertest.data.utils.setRoundedCorners
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityAddNoteBinding
import code.with.me.testroomandnavigationdrawertest.databinding.PhotoItemBinding
import code.with.me.testroomandnavigationdrawertest.file.FilesController
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseAdapter
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.supportFragmentManager
import code.with.me.testroomandnavigationdrawertest.ui.dialog.AudioRecorderDialog
import code.with.me.testroomandnavigationdrawertest.ui.sheet.PaintSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MakeNoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.UserActionNote
import com.bumptech.glide.Glide
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class MakeNoteFragment : BaseFragment<ActivityAddNoteBinding>(ActivityAddNoteBinding::inflate) {
    private lateinit var cameraUri: Uri

    //    @Inject
//    @Named("makeNoteVMFactory")
//    lateinit var factory: ViewModelProvider.Factory
    private val makeNoteViewModel: MakeNoteViewModel by viewModels()
// lazy {
//        ViewModelProvider(this, factory)[MakeNoteViewModel::class.java]
//    }

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

    // попробовать сделать UUID вместо случайных названий фоток
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
                        println("jkghsfhkjsfgd") // ??
                    }
                }
            }
        }

    private val getImageFromGallery =
        registerForActivityResult(ActivityResultContracts.OpenDocument()) { imageUri ->
            imageUri?.let {
                activity?.contentResolver?.takePersistableUriPermission(
                    imageUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION,
                )
                makeNoteViewModel.getImageFromGallery(imageUri)
                saveNote()
            }
        }

    private val getImageFromCamera =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { callback ->
            if (callback) {
                activity?.let {
                    MediaStore.Images.Media.getBitmap(it.contentResolver, cameraUri)
                    makeNoteViewModel.getImageFromCamera(cameraUri.toString())
                    saveNote()
                }
            }
        }

    private val requestAudioPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listenPaintSheetResult()
        // TODO иногда не создает в папках заметки
        arguments?.getLong("idFolder")?.let {
            if (arguments?.getLong("idFolder") != -1L) {
                makeNoteViewModel.setFolderId(arguments?.getLong("idFolder")!!)
            }
        }
        arguments?.getLong("noteId")?.let {
            if (arguments?.getLong("noteId") != -1L) {
                makeNoteViewModel.noteId = arguments?.getLong("noteId")!!
            }
        }
    }

    private fun listenPaintSheetResult() {
        setFragmentResultListener(PAINT_KEY_RESULT) { _: String, bundle: Bundle ->
            val paintBitmap = bundle.getString("pathBitmap")
            paintBitmap?.let {
                makeNoteViewModel.setPaintSheetResult(paintBitmap)
                saveNote()
            } ?: run {
                throw java.lang.Exception("Не удалось получить данные paintActivity")
            }
        }

        setFragmentResultListener(AUDIO_PATH) { _: String, bundle: Bundle ->
            bundle.getString(AUDIO_PATH)?.let {
                makeNoteViewModel.audioInString = it
            } ?: run {
                throw java.lang.Exception("Не удалось получить путь к аудио")
            }
        }
    }

    private fun saveNote() {
        makeNoteViewModel.saveNote()
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        initAdapter()
        initViewModel()
        setBottomMarginBottomNav(20)

        // установил задержку, потому что при открытии фрагмента с анимацией есть фризы
        Handler(Looper.getMainLooper()).postDelayed({
            initClickListeners()
//            initOnEditorActionListener()
            initTextChangeListeners()
        }, 150)
    }

    private fun initOnEditorActionListener() {
        binding.apply {
            // not work
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
        }
    }

    private fun openAudioRecordDialog() {
        AudioRecorderDialog().apply {
            this.show(activity().supportFragmentManager(), "AudioRecorderDialog")
        }
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
        if (makeNoteViewModel.noteId != -1L) {
            binding.textEdit.hint = ""
            binding.titleEdit.hint = ""
        }
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
            if (!it.checkCameraAndWriteExternalStoragePermission()) {
                makeNoteViewModel.setPermission(MakeNoteViewModel.Companion.TypeOfPermission.CAMERA)
                request.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    ),
                )
            } else {
                makeCameraFileAndOpenCamera(it)
            }
//            if (it.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED || it.checkSelfPermission(
//                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                ) == PackageManager.PERMISSION_DENIED
//            ) {
//                makeNoteViewModel.setPermission(MakeNoteViewModel.Companion.TypeOfPermission.CAMERA)
// //                currentPermission = TypeOfPermission.CAMERA
//                request.launch(
//                    arrayOf(
//                        Manifest.permission.CAMERA,
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    ),
//                )
//            } else {
//                makeCameraFileAndOpenCamera(it)
//            }
        }
    }

    // пока не знаю как быть с этой функцией, думаю какую-то часть перенести во вьюмодельку, но нужно будет инжектить filesController
    private fun makeCameraFileAndOpenCamera(fragmentActivity: FragmentActivity): Job {
        val timeStamp = SimpleDateFormat("yyyyMMddHHmmSS", Locale.getDefault()).format(Date())

        val file =
            filesController.saveToInternalStorage(
                fragmentActivity,
                "image_${Calendar.getInstance().timeInMillis}_$timeStamp.jpg",
            )
        file?.createNewFile()
        file?.let {
            cameraUri =
                filesController.getUriForFile(
                    fragmentActivity,
                    file,
                )
        } ?: run {
            return@run
        }

        return lifecycleScope.launch {
            getImageFromCamera.launch(
                cameraUri,
            )
        }
    }

    private fun initAdapter() {
        adapter =
            object : BaseAdapter<PhotoModel, PhotoItemBinding>(photoItem) {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int,
                ): BaseViewHolder<PhotoItemBinding> {
                    val binding =
                        PhotoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    val holder = BaseViewHolder(binding)
                    return holder
                }

                override fun onBindViewHolder(
                    holder: BaseViewHolder<PhotoItemBinding>,
                    position: Int,
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
                    layoutManager =
                        LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                }
            }
    }
}
