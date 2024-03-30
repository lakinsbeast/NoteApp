package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import code.with.me.testroomandnavigationdrawertest.AlertCreator
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.utils.gone
import code.with.me.testroomandnavigationdrawertest.data.utils.launchAfterTimerMain
import code.with.me.testroomandnavigationdrawertest.data.utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.databinding.HomeFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderTagViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class FolderHomeFragment : BaseFragment<HomeFragmentBinding>(HomeFragmentBinding::inflate) {
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var fragmentList = mutableListOf<Fragment>()
    private var listOfFolderTags = mutableListOf<FolderTag>()
    private var checkedChips = mutableListOf<Long>()

    @Inject
    @Named("folderTagVMFactory")
    lateinit var tagVmFactory: ViewModelProvider.Factory
    private lateinit var folderTagViewModel: FolderTagViewModel

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAppComponent()
        initViewModel()
        initFragmentList()
        initViews()
        initClickListeners()
        initFragmentAdapterState()
        initTabLayoutMediator()
    }

    private fun initTabLayoutMediator() {
        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "All folders"
                1 -> tab.text = "Last viewed"
                2 -> tab.text = "Last edited"
                3 -> tab.text = "Favorite"
            }
        }.attach()
    }

    private fun initFragmentAdapterState() {
        object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragmentList.size

            override fun createFragment(position: Int): Fragment {
                return fragmentList[position]
            }
        }.apply {
            viewPager.adapter = this
        }
    }

    private fun initClickListeners() {
        binding.apply {
            addFolderBtn.setOnClickListener {
                AlertCreator.createAddFolderMenu(
                    requireContext(),
                    {
                        findNavController().navigate(FolderHomeFragmentDirections.actionHomeFragmentToAddFolderTagSheetMenu())
                    },
                    {
                        findNavController().navigate(
                            FolderHomeFragmentDirections.actionHomeFragmentToAddFolderSheet(
                                listOfFolderTags.toTypedArray(),
                            ),
                        )
                    },
                )
            }
        }
    }

    private fun initViews() {
        this@FolderHomeFragment.viewPager = binding.viewPager
        this@FolderHomeFragment.tabLayout = binding.tabLayout
    }

    private fun initFragmentList() {
        fragmentList.apply {
            clear()
            add(FolderListFragment())
            add(LastViewedFoldersListFragment())
            add(LastEditedFolderListFragment())
            add(FavoriteFoldersListFragment())
        }
    }

    private fun initViewModel() {
        folderTagViewModel = ViewModelProvider(this, tagVmFactory)[FolderTagViewModel::class.java]
        lifecycleScope.launch {
            folderTagViewModel.getAllTags().collect {
                listOfFolderTags.clear()
                listOfFolderTags = it.toMutableList()
                if (it.isNotEmpty()) {
                    binding.chipGroup.removeAllViews()
                    binding.chipGroup.visible()
                    it.forEach {
                        val chip =
                            Chip(
                                binding.chipGroup.context,
                                null,
                                com.google.android.material.R.style.Widget_MaterialComponents_Chip_Filter,
                            )
                        chip.text = it.name
                        chip.isClickable = true
                        chip.isCheckable = false
                        // если установить в стиле, то не работает
                        chip.setChipBackgroundColorResource(R.color.white)
                        chip.chipStrokeWidth = 2f
                        chip.chipStrokeColor =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.black,
                                ),
                            )
                        chip.chipCornerRadius = 15f
                        chip.setOnClickListener { view ->
                            if (checkedChips.contains(it.id)) {
                                checkedChips.remove(it.id)
                                chip.setChipBackgroundColorResource(R.color.white)
                            } else {
                                checkedChips.add(it.id)
                                chip.setChipBackgroundColorResource(R.color.gray)
                            }
                            launchAfterTimerMain(500) {
                                val folderListFragment = fragmentList[0] as? FolderListFragment
                                folderListFragment?.updateRecyclerViewData(
                                    checkedChips.joinToString(
                                        ",",
                                    ),
                                )
                            }
                        }
                        binding.chipGroup.addView(chip)
                    }
                } else {
                    binding.chipGroup.gone()
                }
            }
        }
    }

    private fun initAppComponent() {
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@FolderHomeFragment)
        }
    }

    fun navigateToNotesListFragment(folderId: Long) {
        val action = FolderHomeFragmentDirections.actionHomeFragmentToNoteHomeFragment(folderId)
        findNavController().navigate(
            action,
        )
    }

    fun navigateToSelectFolderDestintationSheet(idFolder: Long) {
        val action =
            FolderHomeFragmentDirections.actionHomeFragmentToSelectFolderDestinationSheet(
                idFolder,
            )
        findNavController().navigate(action)
    }
}
