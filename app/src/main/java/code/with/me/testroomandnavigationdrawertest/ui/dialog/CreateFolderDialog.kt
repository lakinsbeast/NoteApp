package code.with.me.testroomandnavigationdrawertest.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.WindowManager
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.Utils.launchAfterTimerIO
import code.with.me.testroomandnavigationdrawertest.data.Utils.launchAfterTimerMain
import code.with.me.testroomandnavigationdrawertest.data.Utils.println
import code.with.me.testroomandnavigationdrawertest.data.Utils.setCancelButton
import code.with.me.testroomandnavigationdrawertest.data.Utils.setChipSelectedDesign
import code.with.me.testroomandnavigationdrawertest.data.Utils.setChipUnSelectedDesign
import code.with.me.testroomandnavigationdrawertest.data.Utils.setConfirmButton
import code.with.me.testroomandnavigationdrawertest.data.Utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.databinding.CreateFolderDialogBinding
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModel
import com.google.android.material.chip.Chip
import javax.inject.Inject
import javax.inject.Named

class CreateFolderDialog(private val myContext: Context) :
    Dialog(myContext) {

    private lateinit var binding: CreateFolderDialogBinding
    private var selectedChip = -2
    private var selectedText = ""

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    private lateinit var folderViewModel: FolderViewModel

    private var textRunnable = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(text: CharSequence?, start: Int, before: Int, count: Int) {
            binding.apply {
                text.println()
                if (text != null) {
                    findTextGroupChip(text)
                }
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initAppComponent()
        super.onCreate(savedInstanceState)
        binding = CreateFolderDialogBinding.inflate(LayoutInflater.from(myContext))
        setContentView(binding.root)
        initViewModel()
        setCancelable(false)
        initClickListeners()
        initTextListeners()
        setUpDialogWindow()
        binding.cancelButton.setCancelButton()
        binding.confirmButton.setConfirmButton()
        generateChip()
        clearAllSelectedChip()

    }

    private fun setUpDialogWindow() {
        binding.root.setRoundedCornersView(56f)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            this.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            this.window?.attributes?.blurBehindRadius = 10
        }
    }

    private fun initAppComponent() {
        myContext.let {
            val appComponent =
                ((it as MainActivity).application as NotesApplication).appComponent
            appComponent.inject(this@CreateFolderDialog)
        }
    }

    private fun initViewModel() {
        folderViewModel =
            ViewModelProvider(
                (myContext as MainActivity).viewModelStore,
                folderVmFactory
            )[FolderViewModel::class.java]
    }

    private fun initTextListeners() {
        binding.apply {
            folderNameInput.addTextChangedListener(textRunnable)
        }
    }

    private fun findTextGroupChip(text: CharSequence) {
        for (chip in 0 until binding.chipGroup.childCount) {
            val selectedChip = (binding.chipGroup.getChildAt(chip) as Chip)
            if (TextUtils.equals(selectedChip.text, text)) {
                selectedChip.isChecked = true
                selectedChip.setChipSelectedDesign()
            } else {
                selectedChip.isChecked = false
                selectedChip.setChipUnSelectedDesign()
            }
        }
    }

    private fun clearAllSelectedChip() {
        for (chip in 0 until binding.chipGroup.childCount) {
            (binding.chipGroup.getChildAt(chip) as Chip).setChipUnSelectedDesign()
        }
    }

    private fun initClickListeners() {
        binding.apply {
            chipGroup.setOnCheckedStateChangeListener { group, checkedIds ->
                clearAllSelectedChip()
                val checkedChip = root.findViewById<Chip>(chipGroup.checkedChipId)
                if (chipGroup.checkedChipId != -1) {
                    selectedChip = chipGroup.checkedChipId
                }
                checkedChip?.let {
                    it.setChipSelectedDesign()
                    val chipText = it.text.toString()
                    selectedText = chipText
                    folderNameInput.removeTextChangedListener(textRunnable)
                    folderNameInput.text.clear()
                    folderNameInput.addTextChangedListener(textRunnable)
                    folderNameInput.append(chipText)
                }
            }
            cancelButton.setOnClickListener {
                dismiss()
            }
            confirmButton.setOnClickListener {
                if (folderNameInput.text.isEmpty()) {
                    folderNameInput.error = "Введите название!"
                    return@setOnClickListener
                }
                launchAfterTimerMain(0) {
                    folderViewModel.insertFolder(
                        Folder(
                            folderNameInput.text.toString(),
                            System.currentTimeMillis(),
                            System.currentTimeMillis(),
                            System.currentTimeMillis(),
                            "",
                            isFavoriteSwitch.isChecked
                        )
                    )
                }
                dismiss()
            }
        }
    }
    val folderNames = mutableListOf(
        "🏞️ Путешествия",
        "🍽️ Еда и рецепты",
        "🎥 Развлечения",
        "💡 Идеи и планы",
        "🚗 Транспорт",
        "🎓 Образование",
        "🎂 События и праздники",
        "💰 Финансы",
        "📅 Планирование",
        "🎨 Творчество",
        "📰 Новости",
        "🏋️ Фитнес и здоровье",
        "🏡 Дом и быт",
        "🌍 Планы на путешествия",
        "📚 Книги и чтение",
        "🎯 Цели и достижения",
        "🎵 Музыка",
        "💼 Работа и задачи",
        "🌆 Городская жизнь",
        "🌱 Хобби и увлечения"
    )

    private fun generateChip() {
        binding.chipGroup.removeAllViewsInLayout()
        repeat(4) {
            val chip = Chip(context)
            chip.isCheckable = true
            val index = folderNames.indices.random()
            chip.text = folderNames[index]
            folderNames.removeAt(index)
            binding.chipGroup.addView(chip)
        }
    }


}