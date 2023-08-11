package code.with.me.testroomandnavigationdrawertest.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import androidx.core.view.forEach
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.launchAfterTimerIO
import code.with.me.testroomandnavigationdrawertest.Utils.launchAfterTimerMain
import code.with.me.testroomandnavigationdrawertest.Utils.println
import code.with.me.testroomandnavigationdrawertest.Utils.setCancelButton
import code.with.me.testroomandnavigationdrawertest.Utils.setChipSelectedDesign
import code.with.me.testroomandnavigationdrawertest.Utils.setChipUnSelectedDesign
import code.with.me.testroomandnavigationdrawertest.Utils.setConfirmButton
import code.with.me.testroomandnavigationdrawertest.Utils.setRoundedCornersView
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
                    findedTextGroupChip(text)
                }
//                if (TextUtils.equals(selectedText, text)) {
//                    try {
//                        root.findViewById<Chip>(selectedChip).isChecked = true
//                    } catch (e: Exception) {
//                        e.localizedMessage.println()
//                    }
//                } else {
//                    binding.chipGroup.clearCheck()
//                }
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
        clearAllSelectedChip()
    }

    private fun setUpDialogWindow() {
        binding.root.setRoundedCornersView(56f)
        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
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

    private fun findTextInGroupChip() {
        for (chip in 0 until binding.chipGroup.childCount) {

        }
    }

    private fun findedTextGroupChip(text: CharSequence) {
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


}