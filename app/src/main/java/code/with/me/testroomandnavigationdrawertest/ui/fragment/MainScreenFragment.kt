package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.AnimationDrawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.isNotEmpty
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.Utils.gone
import code.with.me.testroomandnavigationdrawertest.data.Utils.launchAfterTimerMain
import code.with.me.testroomandnavigationdrawertest.data.Utils.println
import code.with.me.testroomandnavigationdrawertest.data.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.databinding.MainScreenFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentOptions
import code.with.me.testroomandnavigationdrawertest.ui.controllers.fragmentOptionsBuilder
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

    private var selectedChip = -1
    private var selectedChipFolderId = 1L

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
//        when (savedInstanceState?.getString("fragmentTag")) {
//            "MakeNoteFragment" -> {
//                openMakeNoteFragment()
//            }
//        }
    }


    override fun onSaveInstanceState(outState: Bundle) {
        kotlin.io.println("onSaveInstanceState fragment tag ${fragmentManager?.fragments?.last()?.javaClass?.simpleName}")
        outState.putString(
            "fragmentTag",
            fragmentManager?.fragments?.last()?.javaClass?.simpleName
        )
        super.onSaveInstanceState(outState)
    }


    private fun initClickListeners() {
        binding.apply {
            makeNoteBtn.setOnClickListener {
                openMakeNoteFragment()
            }
            makeFolderBtn.setOnClickListener {
                CreateFolderDialog().show(activity().supportFragmentManager, "CreateFolderDialog")
            }
            settingsBtn.setOnClickListener {
//                Toast.makeText(context, "Settings Fragment", Toast.LENGTH_LONG)
//                    .show()
                activity().fragmentController.openFragment(
                    activity(),
                    SettingsFragment(),
                    fragmentOptionsBuilder {
                        fragmentLayout = R.id.fragment_detail
                    })
            }
            searchBtn.setOnClickListener {
                activity().fragmentController.openFragment(
                    activity(),
                    SearchFragment(),
                    fragmentOptionsBuilder {
                        fragmentLayout = R.id.fragment_detail
                    })
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
            mainScreenViewModel.isUseFolderEnabled.observe(viewLifecycleOwner) {
                if (it) {
                    binding.folderLayout.visible()
                } else {
                    binding.folderLayout.gone()
                }
            }
            mainScreenViewModel.getAllFolders().collect() {
                binding.chipGroup.removeAllViewsInLayout()
                val chip = Chip(
                    binding.chipGroup.context,
                    null,
                    MR.style.Widget_MaterialComponents_Chip_Filter
                )
                chip.setUpFirstChip()
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
                    binding.folderLayout.visible()
                } else {
                    binding.folderLayout.gone()
                }
            }
        }
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
        val chipGroup = binding.chipGroup
        if (binding.chipGroup.isNotEmpty()) {
            id = chipGroup.getChildAt(chipGroup.childCount - 1).id + 1
        }
        setOnClickListener { view ->
            if (selectedChip == this.id) {
                selectedChip = -1
                selectedChipFolderId = 1
                openNotesListFragment()
                chipGroup.forEachIndexed { index, view ->
                    (chipGroup[index] as Chip).unsetSelected()
                }
            } else {
                showProgressBar(true)
                openNotesListFragment(it)
                selectedChipFolderId = it.id
                chipGroup.forEachIndexed { index, view ->
                    (chipGroup[index] as Chip).unsetSelected()
                }
                selectedChip = this.id
                println("selectedChip: $selectedChip")
                println("selectedChipFolderId: $selectedChipFolderId")
                println("this.id: ${this.id}")
                chipGroup.findViewById<Chip>(selectedChip).setSelected()
            }
        }
    }

    private fun openNotesListFragment(it: Folder? = null) {
        val bundle = Bundle()
        if (it == null) {
            bundle.putLong("idFolder", -1)
        } else {
            bundle.putLong("idFolder", it.id)
        }
        val notesListFragment = NotesListFragment()
        notesListFragment.arguments = bundle
        (activity as MainActivity).fragmentController.replaceFragment(
            activity as MainActivity,
            notesListFragment,
            fragmentOptionsBuilder {
                fragmentLayout = R.id.fragment_container
                clearBackStack = true
            }
        )
        showProgressBar(false)
    }

    fun openMakeNoteFragment(id: Long = -1) {
        val fragment = MakeNoteFragment()
        val bundle = Bundle()
        bundle.putLong("idFolder", selectedChipFolderId)
        bundle.putLong("noteId", id)
        fragment.arguments = bundle
        (activity as MainActivity).fragmentController.openFragment(
            activity as MainActivity,
            fragment, fragmentOptionsBuilder {
                fragmentLayout = R.id.fragment_detail
            }
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

    fun navigateToViewANoteSheet(id: Long) {
        try {
            val viewNoteSheet = ViewANoteSheet()
            viewNoteSheet.setFullScreenSheet()
            val bundle = Bundle()
            viewNoteSheet.arguments = bundle.apply {
                putLong("noteId", id)
            }
            (activity as MainActivity).let { activity ->
                activity.sheetController.showSheet(activity, viewNoteSheet)
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }

}