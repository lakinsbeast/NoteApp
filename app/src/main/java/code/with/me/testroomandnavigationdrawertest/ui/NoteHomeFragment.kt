package code.with.me.testroomandnavigationdrawertest.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import code.with.me.testroomandnavigationdrawertest.AlertCreator
import code.with.me.testroomandnavigationdrawertest.FolderListFragment
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.NotesListFragment
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteTag
import code.with.me.testroomandnavigationdrawertest.databinding.FolderHomeFragmentBinding
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentNotesListBinding
import code.with.me.testroomandnavigationdrawertest.databinding.HomeFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
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

    private var idFolder by Delegates.notNull<Int>()


    @Inject
    @Named("noteTagVMFactory")
    lateinit var noteVmFactory: ViewModelProvider.Factory
    private lateinit var noteTagViewModel: NoteTagViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@NoteHomeFragment)
        }
        noteTagViewModel = ViewModelProvider(this, noteVmFactory)[NoteTagViewModel::class.java]

        idFolder = arguments?.getInt("idFolder") ?: 0
        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        lifecycleScope.launch {
            noteTagViewModel.getAllTags().collect() {
                listOfFolderTags.clear()
                listOfFolderTags = it.toMutableList()
                if (it.isNotEmpty()) {
                    binding.chipGroup.removeAllViews()
                    binding.chipGroup.visible()
                    it.forEach {
                        val chip = Chip(binding.chipGroup.context)
                        chip.text = it.name
                        chip.isClickable = true
                        chip.isCheckable = false
                        binding.chipGroup.addView(chip)
                    }
                } else {
                    binding.chipGroup.gone()
                }
            }
        }

        fragmentList.apply {
            clear()
            add(NotesListFragment().apply {
                arguments = Bundle().apply {
                    putInt("idFolder", this@NoteHomeFragment.idFolder)
                }
            })
        }

        binding.apply {
            fab.setOnClickListener {
                AlertCreator.createAddNoteMenu(requireContext(),
                    {
                        findNavController().navigate(NoteHomeFragmentDirections.actionNoteHomeFragmentToAddNoteTagSheetMenu())
                    }, {
                        findNavController().navigate(
                            NoteHomeFragmentDirections.actionNoteHomeFragmentToMakeANoteSheet(
                                arguments?.getInt("idFolder") ?: 0
                            )
                        )
                    })
            }
        }

        object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragmentList.size

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }
        }.apply {
            viewPager.adapter = this
        }

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Все заметки"
                1 -> tab.text = "Последнее просмотренное"
                2 -> tab.text = "Последнее изменненое"
                3 -> tab.text = "Избранное"
            }
        }.attach()

    }

    fun changeUiOnRvUpdate(binding: FolderHomeFragmentBinding, notes: List<Note>) {
        binding.apply {
            chipGroupScrollable.gone()
        }
    }

    fun navigateToNotesListFragment(id: Int) {
        val action = NoteHomeFragmentDirections.actionNoteHomeFragmentToViewANoteSheet(id)
        findNavController().navigate(action)
    }

}