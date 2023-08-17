package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.launchAfterTimerMain
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.databinding.MainScreenFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentOptions
import code.with.me.testroomandnavigationdrawertest.ui.dialog.CreateFolderDialog
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddFolderSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.ViewANoteSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MainScreenViewModel
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import com.google.android.material.R as MR

class MainScreenFragment :
    BaseFragment<MainScreenFragmentBinding>(MainScreenFragmentBinding::inflate) {


    private var selectedChip = 1
    private var selectedChipFolderId = 1

    @Inject
    @Named("mainScreenVMFactory")
    lateinit var mainScreenVMFactory: ViewModelProvider.Factory
    private lateinit var mainScreenViewModel: MainScreenViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppComponent()
        initViewModel()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
        initClickListeners()
        openNotesListFragment()
        createFirstChip()
    }

    private fun initClickListeners() {
        binding.apply {
            makeNoteBtn.setOnClickListener {
//                openMakeNoteSheet()
                openMakeNoteFragment()
            }
            makeFolderBtn.setOnClickListener {
//                openMakeFolderSheet()
                CreateFolderDialog(requireContext()).show()
            }
        }
    }

    private fun initAppComponent() {
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@MainScreenFragment)
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            mainScreenViewModel.getAllFolders().collect() {
                println("getAllFolders size: ${it.size}")


                //TODO FIX THIS SHIT
                binding.chipGroup.forEachIndexed { index, view ->
                    if (index != 0) {
                        binding.chipGroup.removeView((binding.chipGroup[index] as Chip))
                    }
                }
                if (it.isNotEmpty()) {
                    it.forEach { folder ->
                        val chip = Chip(
                            binding.chipGroup.context,
                            null,
                            MR.style.Widget_MaterialComponents_Chip_Filter
                        )
                        chip.setUpChip(folder)
                        binding.chipGroup.addView(chip)
                    }
                }
            }
        }
    }

    private fun createFirstChip() {
        val chip = Chip(
            binding.chipGroup.context,
            null,
            MR.style.Widget_MaterialComponents_Chip_Filter
        )
        chip.setUpFirstChip()
        binding.chipGroup.addView(chip)
    }

    private fun Chip.setUpFirstChip() {
        text = getString(R.string.allNotes)
        isClickable = true
        isCheckable = false
        //если установить в стиле, то не работает
        setChipBackgroundColorResource(R.color.white)
        chipStrokeWidth = 2f
        chipStrokeColor = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )

        selectedChip = this.id
        chipCornerRadius = 15f
        setOnClickListener { view ->
            if (selectedChip == this.id) return@setOnClickListener
            showProgressBar(true)
            openNotesListFragment()
            selectedChipFolderId = -1
            binding.chipGroup.forEachIndexed { index, view ->
                (binding.chipGroup[index] as Chip).unsetSelected()
            }
            selectedChip = this.id
            (binding.chipGroup[selectedChip - 1] as Chip).setSelected()
        }
        setSelected()
    }

    private fun Chip.setUpChip(it: Folder) {
        text = it.name
        isClickable = true
        isCheckable = false
        //если установить в стиле, то не работает
        setChipBackgroundColorResource(R.color.white)
        chipStrokeWidth = 2f
        chipStrokeColor = ColorStateList.valueOf(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
        chipCornerRadius = 15f
        setOnClickListener { view ->
            if (selectedChip == this.id) return@setOnClickListener
            showProgressBar(true)
            openNotesListFragment(it)
            selectedChipFolderId = it.id
            binding.chipGroup.forEachIndexed { index, view ->
                (binding.chipGroup[index] as Chip).unsetSelected()
            }
            selectedChip = this.id
            println("selectedChip: $selectedChip")
            (binding.chipGroup[selectedChip - 1] as Chip).setSelected()
        }
    }

    private fun openNotesListFragment(it: Folder? = null) {
        val bundle = Bundle()
        if (it == null) {
            bundle.putInt("idFolder", -1)
        } else {
            bundle.putInt("idFolder", it.id)
        }
        val notesListFragment = NotesListFragment()
        notesListFragment.arguments = bundle
        (activity as MainActivity).fragmentController.replaceFragment(
            activity as MainActivity,
            notesListFragment,
            FragmentOptions(R.id.fragment_container, clearBackStack = true)
        )
        showProgressBar(false)
    }

    private fun openMakeNoteFragment() {
        val fragment = MakeNoteFragment()
        val bundle = Bundle()
        bundle.putInt("idFolder", selectedChipFolderId)
        fragment.arguments = bundle
        (activity as MainActivity).fragmentController.openFragment(
            activity as MainActivity,
            fragment, FragmentOptions(R.id.fragment_detail)
        )
    }

    private fun openMakeFolderSheet() {
        val sheet = AddFolderSheet()
        (activity as MainActivity).let { activity ->
            activity.sheetController.showSheet(activity, sheet)
        }
    }

    private fun showProgressBar(show: Boolean) {
        if (show) binding.progressBar.visible() else binding.progressBar.gone()
    }

    private fun Chip.setSelected() {
        chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.black, null))
        setTextColor(resources.getColor(R.color.white, null))
    }

    private fun Chip.unsetSelected() {
        chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.white, null))
        setTextColor(resources.getColor(R.color.black, null))
    }

    private fun initViewModel() {
        mainScreenViewModel =
            ViewModelProvider(
                this,
                mainScreenVMFactory
            )[MainScreenViewModel::class.java]
    }

    fun navigateToViewANoteSheet(id: Int) {
        try {
            val viewNoteSheet = ViewANoteSheet()
            val bundle = Bundle()
            viewNoteSheet.arguments = bundle.apply {
                putInt("noteId", id)
            }
            (activity as MainActivity).let { activity ->
                activity.sheetController.showSheet(activity, viewNoteSheet)
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

}