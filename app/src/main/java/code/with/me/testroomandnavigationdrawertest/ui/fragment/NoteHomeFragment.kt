package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import code.with.me.testroomandnavigationdrawertest.AlertCreator
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.utils.gone
import code.with.me.testroomandnavigationdrawertest.data.utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteTag
import code.with.me.testroomandnavigationdrawertest.databinding.FolderHomeFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteTagViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates

class NoteHomeFragment :
    BaseFragment<FolderHomeFragmentBinding>(FolderHomeFragmentBinding::inflate) {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var fragmentList = mutableListOf<Fragment>()
    private var listOfFolderTags = mutableListOf<NoteTag>()

    private var idFolder by Delegates.notNull<Long>()

    @Inject
    @Named("noteTagVMFactory")
    lateinit var noteVmFactory: ViewModelProvider.Factory
    private lateinit var noteTagViewModel: NoteTagViewModel

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAppComponent()
        initViewModel()
        initViewPager()
        initTabLayout()
        initClickListeners()
    }

    private fun initAppComponent() {
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@NoteHomeFragment)
        }
    }

    private fun initViewModel() {
        noteTagViewModel =
            ViewModelProvider(this, noteVmFactory).get(NoteTagViewModel::class.java)

        lifecycleScope.launch {
            noteTagViewModel.getAllTags().collect { tags ->
                listOfFolderTags.clear()
                listOfFolderTags.addAll(tags)
                if (tags.isNotEmpty()) {
                    binding.chipGroup.removeAllViews()
                    binding.chipGroup.visible()
                    tags.forEach { tag ->
                        val chip = Chip(binding.chipGroup.context)
                        chip.text = tag.name
                        chip.isClickable = true
                        chip.isCheckable = false
                        binding.chipGroup.addView(chip)
                    }
                } else {
                    binding.chipGroup.gone()
                }
            }
        }
    }

    private fun initViewPager() {
        idFolder = arguments?.getLong("idFolder") ?: 0L
        viewPager = binding.viewPager

        fragmentList.apply {
            clear()
            add(
                NotesListFragment().apply {
                    arguments =
                        Bundle().apply {
                            putLong("idFolder", this@NoteHomeFragment.idFolder)
                        }
                },
            )
        }

        viewPager.adapter =
            object : FragmentStateAdapter(this) {
                override fun getItemCount(): Int = fragmentList.size

                override fun createFragment(position: Int): Fragment {
                    return fragmentList[position]
                }
            }
    }

    private fun initTabLayout() {
        tabLayout = binding.tabLayout

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Все заметки"
                1 -> tab.text = "Последнее просмотренное"
                2 -> tab.text = "Последнее изменено"
                3 -> tab.text = "Избранное"
            }
        }.attach()
    }

    private fun initClickListeners() {
        binding.fab.setOnClickListener {
            AlertCreator.createAddNoteMenu(
                requireContext(),
                {
                    findNavController().navigate(NoteHomeFragmentDirections.actionNoteHomeFragmentToAddNoteTagSheetMenu())
                },
                {
                    findNavController().navigate(
                        NoteHomeFragmentDirections.actionNoteHomeFragmentToMakeANoteSheet(
                            arguments?.getLong("idFolder") ?: 0L,
                        ),
                    )
                },
            )
        }
    }

    fun changeUiOnRvUpdate(
        binding: FolderHomeFragmentBinding,
        notes: List<Note>,
    ) {
        binding.chipGroupScrollable.gone()
    }

    fun navigateToNotesListFragment(id: Long) {
        val action =
            NoteHomeFragmentDirections.actionNoteHomeFragmentToViewANoteSheet(id)
        findNavController().navigate(action)
    }
}
