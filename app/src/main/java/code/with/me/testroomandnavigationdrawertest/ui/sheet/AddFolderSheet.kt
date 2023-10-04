package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.Utils.gone
import code.with.me.testroomandnavigationdrawertest.data.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.databinding.AddFolderBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import com.google.android.material.chip.Chip
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class AddFolderSheet : BaseSheet<AddFolderBottomSheetBinding>(AddFolderBottomSheetBinding::inflate),
    CoroutineScope {
    private val TAG = "AddFolderSheet"

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    private lateinit var folderViewModel: FolderViewModel

    private val selectedChips = mutableListOf<Int>()
    private val isFavorite = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppComponent()
        initViewModel()
        initChipGroup()
        initClickListeners()
    }

    private fun initAppComponent() {
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@AddFolderSheet)
        }
    }

    private fun initViewModel() {
        folderViewModel =
            ViewModelProvider(this, folderVmFactory).get(FolderViewModel::class.java)
    }

    private fun initChipGroup() {
        val listTags = arguments?.getParcelableArray("listFolderTags")

        if (!listTags.isNullOrEmpty()) {
            binding.chipGroup.removeAllViews()
            binding.chipGroup.visible()
            listTags?.forEach { parcel ->
                val folderTag = parcel as? FolderTag
                folderTag?.let {
                    val chip = Chip(
                        binding.chipGroup.context,
                        null,
                        com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter
                    )
                    chip.text = it.name
                    chip.isClickable = true
                    chip.isCheckable = false
                    chip.setChipBackgroundColorResource(R.color.white)
                    chip.chipStrokeWidth = 2f
                    chip.chipStrokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.black
                        )
                    )
                    chip.chipCornerRadius = 15f
                    chip.setOnClickListener { view ->
                        if (selectedChips.contains(it.id)) {
                            selectedChips.remove(it.id)
                            chip.setChipBackgroundColorResource(R.color.white)
                        } else {
                            selectedChips.add(it.id)
                            chip.setChipBackgroundColorResource(R.color.gray)
                        }
                    }
                    binding.chipGroup.addView(chip)
                }
            }
        } else {
            binding.chipGroup.gone()
        }
    }

    private fun initClickListeners() {
        binding.addFolderBtn.setOnClickListener {
            val text = binding.textEdit.editText?.text

            if (text.isNullOrEmpty()) {
                binding.textEdit.error = "Пустое поле названия, введите текст"
                return@setOnClickListener
            }

            launch {
                folderViewModel.insertFolder(
                    Folder(
                        text.toString(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        selectedChips.joinToString(","),
                        isFavorite
                    )
                )
            }
            findNavController().popBackStack()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isActive) {
            cancel()
        }
    }

    override val coroutineContext: CoroutineContext
        get() = Job() + Dispatchers.IO
}
