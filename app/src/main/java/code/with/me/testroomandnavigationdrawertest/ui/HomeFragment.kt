package code.with.me.testroomandnavigationdrawertest.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import code.with.me.testroomandnavigationdrawertest.FolderListListFragment
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.databinding.HomeFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import com.google.android.material.chip.Chip
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class HomeFragment : BaseFragment<HomeFragmentBinding>(HomeFragmentBinding::inflate) {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private var fragmentList = mutableListOf<Fragment>()
    private var listOfFolderTags = mutableListOf<FolderTag>()

    @Inject
    @Named("tagVMFactory")
    lateinit var tagVmFactory: ViewModelProvider.Factory
    private lateinit var tagViewModel: TagViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@HomeFragment)
        }
        tagViewModel = ViewModelProvider(this, tagVmFactory)[TagViewModel::class.java]


        viewPager = binding.viewPager
        tabLayout = binding.tabLayout

        lifecycleScope.launch {
            tagViewModel.getAllTags().collect() {
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
            add(FolderListListFragment())
            add(LastViewedFoldersListFragment())
            add(LastEditedFolderListFragment())
            add(FavoriteFoldersListFragment())
        }

        binding.apply {
            addFolderBtn.setOnClickListener {
                findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToAddFolderSheet())
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

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                val destinationId = when (position) {
                    0 -> R.id.folderListFragment
                    1 -> R.id.lastViewedFoldersFragment
                    2 -> R.id.lastEditedFolderFragment
                    3 -> R.id.favoriteFoldersFragment
                    else -> {
                        R.id.folderListFragment
                    }
                }
            }
        })

        TabLayoutMediator(tabLayout, viewPager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "Все папки"
                1 -> tab.text = "Последнее просмотренное"
                2 -> tab.text = "Последнее изменненое"
                3 -> tab.text = "Избранное"
            }
        }.attach()
    }


    fun navigateToNotesListFragment(folderId: Int) {
        val action = HomeFragmentDirections.actionHomeFragmentToNotesListFragment2(folderId)
        findNavController().navigate(action)
    }


}