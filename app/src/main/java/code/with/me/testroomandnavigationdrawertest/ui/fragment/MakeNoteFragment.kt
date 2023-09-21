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
import code.with.me.testroomandnavigationdrawertest.Utils.mainScope
import code.with.me.testroomandnavigationdrawertest.Utils.println
import code.with.me.testroomandnavigationdrawertest.Utils.setCheckable
import code.with.me.testroomandnavigationdrawertest.Utils.setRoundedCorners
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

    //    private var currentPermission: TypeOfPermission = TypeOfPermission.EMPTY
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
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { it ->
            "requestAudioPermission it: $it".println()
//            if (it) {
//
//            }
        }

    companion object {
        const val paintResultKey = "paintResultKey"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        makeNoteViewModel =
            ViewModelProvider(requireActivity(), factory)[MakeNoteViewModel::class.java]
        listenPaintSheetResult()
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
        initViewModel()
        initTextChangeListeners()
        setBottomMarginBottomNav(20)
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
                saveNote()
            }

//            textEdit.setOnTextChangeListener {
//                makeNoteViewModel.setText(it.toString())
//                saveNote()
//            }
//            textEdit.addTextChangedListener(object : TextWatcher {
//                override fun beforeTextChanged(
//                    s: CharSequence?,
//                    start: Int,
//                    count: Int,
//                    after: Int
//                ) {
//                }
//
//                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
//                    if (!s.isNullOrEmpty()) {
//                        //check is char deleted
//                        if (before == count) {
//                            if (!isFormattingText) {
//                                doOnDeleteText(s.toString())
//                            }
//                        } else {
//                            if (!isFormattingText) {
//                                doWithText(s.toString())
//                            }
//                        }
//                    }
//                }
//
//                override fun afterTextChanged(s: Editable?) {
//                    makeNoteViewModel.setText(s.toString())
//                    saveNote()
////                    doWithText(s.toString())
//                }
//
//            })
            textEdit.addTextChangedListener {
                makeNoteViewModel.setText(it.toString())
                saveNote()
//                doWithText(it.toString())
            }
        }
    }

//    var isStar = false
//    var startStack = Stack<String>()
//    var startStartCursor = 0
//    var endStartCursor = -1

//    var isFormattingText = false

//    fun doOnDeleteText(text: String) {
//        CoroutineScope(Dispatchers.IO).launch {
//            async {
//                if (text.isNotEmpty() && text.last() == '*' && startStack.isNotEmpty()) {
//                    startStack.pop()
//                    checkStartStack()
//                }
//            }
//            async {
//                acceptStyleText()
//            }
//        }
//    }
//
//    fun doWithText(text: String) {
//        //сохраняем позицию курсора, потому что setText сбрасывает его
//        CoroutineScope(Dispatchers.IO).launch {
//            async {
//                if (text.isNotEmpty() && text.last() == '*') {
//                    println("startStack.size: ${startStack.size}")
//                    startStack.push("*")
////                    when {
////                        !isStar -> {
////                            startStack.push("*")
////                        }
////
////                        isStar -> {
////                            startStack.pop()
////                        }
////                    }
//                    checkStartStack()
//                }
//            }.await()
//            async {
//                acceptStyleText()
//            }
//        }
//    }

//    private fun checkStartStack() {
//        when {
//            startStack.size >= 2 -> {
//                isStar = true
//                startStartCursor = binding.textEdit.selectionStart
//            }
//
//            startStack.size == 0 -> {
//                isStar = false
//                endStartCursor = binding.textEdit.selectionEnd
//            }
//        }
//    }

//    private suspend fun CoroutineScope.acceptStyleText() {
//        isFormattingText = true
//        val selStart = binding.textEdit.selectionStart
//        val selEnd = binding.textEdit.selectionEnd
//        println("selStart: $selStart")
//        println("selEnd: $selEnd")
//        println("startStartCursor: $startStartCursor")
//        val spannableString =
//            SpannableString(binding.textEdit.text.toString())
//        if (isStar) {
//            println("isStar true")
//            spannableString.setSpan(
//                StrikethroughSpan(),
//                startStartCursor,
//                endStartCursor,
//                0
//            )
//        } else {
//            println("isStar false")
//            spannableString.setSpan(
//                StyleSpan(Typeface.NORMAL),
//                selStart,
//                selEnd,
//                0
//            )
//        }
//        mainScope {
//            binding.textEdit.setText(spannableString)
//            binding.textEdit.setSelection(binding.textEdit.length())
//            isFormattingText = false
//        }
//    }

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
        }
        makeNoteViewModel.text.observe(viewLifecycleOwner) {
//            if (it != binding.textEdit.html) {
//                binding.textEdit.html = it
//            }
            if (it != binding.textEdit.text.toString()) {
                binding.textEdit.setText(it)
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