package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import androidx.core.view.isGone
import androidx.core.view.isNotEmpty
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.CHIP_CORNER_RADIUS
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.utils.getDisplayMetrics
import code.with.me.testroomandnavigationdrawertest.data.utils.gone
import code.with.me.testroomandnavigationdrawertest.data.utils.launchAfterTimerMain
import code.with.me.testroomandnavigationdrawertest.databinding.MainScreenFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.fragmentOptionsBuilder
import code.with.me.testroomandnavigationdrawertest.ui.controllers.supportFragmentManager
import code.with.me.testroomandnavigationdrawertest.ui.dialog.CreateFolderDialog
import code.with.me.testroomandnavigationdrawertest.ui.mainScreenFragment.MainScreenViewModel
import code.with.me.testroomandnavigationdrawertest.ui.sheet.ViewANoteSheet
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.google.android.material.R as MR

class MainScreenFragment :
    BaseFragment<MainScreenFragmentBinding>(MainScreenFragmentBinding::inflate) {
//    @Inject
//    @Named("mainScreenVMFactory")
//    lateinit var mainScreenVMFactory: ViewModelProvider.Factory
    private val mainScreenViewModel: MainScreenViewModel by viewModels()
//    lazy {
//        ViewModelProvider(
//            this,
//            mainScreenVMFactory,
//        )[MainScreenViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        listenViewModel()
        initClickListeners()
        openNotesListFragment()
    }

    private fun initClickListeners() {
        binding.apply {
            makeNoteBtn.setOnClickListener {
                openMakeNoteFragment()
            }
            makeFolderBtn.setOnClickListener {
                CreateFolderDialog().show(activity().supportFragmentManager(), "CreateFolderDialog")
            }
            settingsBtn.setOnClickListener {
                activity().fragmentController.openFragment(
                    activity(),
                    SettingsFragment(),
                    fragmentOptionsBuilder {
                        fragmentLayout = R.id.fragment_detail
                    },
                )
            }
            searchBtn.setOnClickListener {
                activity().fragmentController.openFragment(
                    activity(),
                    SearchFragment(),
                    fragmentOptionsBuilder {
                        fragmentLayout = R.id.fragment_detail
                    },
                )
            }
        }
    }

    private fun listenViewModel() {
        lifecycleScope.launch {
            /** достает названия всех папков из бд и показывает на главном экране
             * если пусто, то делает chipGroup невидимыми(gone) */
            mainScreenViewModel.getAllFolders().collect {
                binding.chipGroup.removeAllViewsInLayout()
                if (it.isNotEmpty()) {
                    it.forEach { folder ->
                        withContext(Dispatchers.Main.immediate) {
                            Chip(
                                binding.chipGroup.context,
                                null,
                                MR.style.Widget_MaterialComponents_Chip_Filter,
                            ).apply {
                                setUpChip(folder)
                                binding.chipGroup.addView(this)
                            }
                        }
                    }
                } else {
                    binding.folderLayout.gone()
                }
            }
        }
        /** если false, то скрывает chipGroup папок, вне зависимости, есть ли там какие-то chip*/
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                mainScreenViewModel.isUseFolderEnabled.collect {
                    withContext(Dispatchers.Main.immediate) {
                        binding.folderLayout.isGone = !it
                    }
                }
            }
        }
    }

    private fun Chip.setUpFirstChip() {
        text = getString(R.string.allNotes)
        isClickable = true
        isCheckable = false
        // если установить в стиле, то не работает
        setChipBackgroundColorResource(R.color.white)
        chipStrokeWidth = 2f
        chipStrokeColor =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black,
                ),
            )
        mainScreenViewModel.setSelectedChip(this.id)
        chipCornerRadius = CHIP_CORNER_RADIUS
        setOnClickListener {
            if (mainScreenViewModel.selectedChip.value == this.id) return@setOnClickListener
            showProgressBar(true)
            openNotesListFragment()
            mainScreenViewModel.setSelectedChipFolderId(-1)
            binding.chipGroup.forEachIndexed { index, _ ->
                (binding.chipGroup[index] as Chip).unsetSelected()
            }
            mainScreenViewModel.setSelectedChip(this.id)
            (binding.chipGroup[mainScreenViewModel.selectedChip.value - 1] as Chip).setSelected()
        }
        setSelected()
    }

    private fun Chip.setUpChip(folder: Folder) {
        text = folder.name
        isClickable = true
        isCheckable = false
        // если установить в стиле, то не работает
        setChipBackgroundColorResource(R.color.white)
        chipStrokeWidth = 2f
        chipStrokeColor =
            ColorStateList.valueOf(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.black,
                ),
            )
        chipCornerRadius = CHIP_CORNER_RADIUS
        val chipGroup = binding.chipGroup
        if (binding.chipGroup.isNotEmpty()) {
            id = chipGroup.getChildAt(chipGroup.childCount - 1).id + 1
        }
        setOnClickListener {
            if (mainScreenViewModel.selectedChip.value == this.id) {
                mainScreenViewModel.setSelectedChip(-1)
                mainScreenViewModel.setSelectedChipFolderId(1)
                openNotesListFragment()
                chipGroup.forEachIndexed { index, _ ->
                    (chipGroup[index] as Chip).unsetSelected()
                }
            } else {
                showProgressBar(true)
                openNotesListFragment(folder)
                mainScreenViewModel.setSelectedChipFolderId(folder.id)
                chipGroup.forEachIndexed { index, _ ->
                    (chipGroup[index] as Chip).unsetSelected()
                }
                mainScreenViewModel.setSelectedChip(this.id)
                chipGroup.findViewById<Chip>(mainScreenViewModel.selectedChip.value).setSelected()
            }
        }
    }

    private fun openNotesListFragment(it: Folder? = null) {
        val notesListFragment = NotesListFragment()
        notesListFragment.arguments =
            Bundle().apply {
                it?.let {
                    this.putLong("idFolder", it.id)
                } ?: run {
                    this.putLong("idFolder", -1)
                }
            }
        (activity as MainActivity).fragmentController.replaceFragment(
            activity as MainActivity,
            notesListFragment,
            fragmentOptionsBuilder {
                fragmentLayout = R.id.fragment_container
                clearBackStack = true
            },
        )
        showProgressBar(false)
    }

    /** щас бы вспомнить что к чему тут*/
    fun openMakeNoteFragment(
        id: Long = -1,
        view: View? = null,
    ) {
        view?.let {
            val expandedView =
                View(activity).apply {
                    setBackgroundColor(resources.getColor(R.color.white, null))
                }

            val height = getDisplayMetrics(activity()).heightPixels
            val width = getDisplayMetrics(activity()).widthPixels

            val startX = view.x
            val startY = view.y
            val startHeight = view.height

            expandedView.x = startX
            expandedView.y = startY
            expandedView.layoutParams = FrameLayout.LayoutParams(width, startHeight)

            val container =
                binding.root
            container.addView(expandedView)

            // Вычисляем конечные координаты и размеры (на весь экран)
            val finalX = 0f
            val finalY = 0f

            val anim = ValueAnimator.ofInt(0, 100)
            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.addUpdateListener { animation ->
                val fraction = animation.animatedValue as Int / 100f
                val newX = startX + fraction * (finalX - startX)
                val newY = startY + fraction * (finalY - startY)
                val newWidth = width + (width - width) * fraction
                val newHeight = startHeight + (height - startHeight) * fraction

                expandedView.x = newX
                expandedView.y = newY
                expandedView.layoutParams.width = newWidth.toInt()
                expandedView.layoutParams.height = newHeight.toInt()
                expandedView.requestLayout()
            }

            anim.addListener(
                object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        val fragment = MakeNoteFragment()
                        fragment.arguments =
                            Bundle().apply {
                                putLong("idFolder", mainScreenViewModel.selectedChipFolderId.value)
                                putLong("noteId", id)
                            }
                        activity().fragmentController.openFragment(
                            activity(),
                            fragment,
                            fragmentOptionsBuilder {
                                fragmentLayout = R.id.fragment_detail
                            },
                            R.anim.enter_from_right,
                            R.anim.exit_to_left,
                        )
                        launchAfterTimerMain(50) {
                            container.removeView(expandedView)
                        }
                    }
                },
            )

            anim.duration = 300
            anim.start()
        } ?: run {
            val fragment = MakeNoteFragment()
            fragment.arguments =
                Bundle().apply {
                    putLong("idFolder", mainScreenViewModel.selectedChipFolderId.value)
                    putLong("noteId", id)
                }
            activity().fragmentController.openFragment(
                activity(),
                fragment,
                fragmentOptionsBuilder {
                    fragmentLayout = R.id.fragment_detail
                },
                R.anim.enter_from_right,
                R.anim.exit_to_left,
            )
        }
    }

    private fun showProgressBar(show: Boolean) {
        binding.progressBar.isGone = !show
//        if (show) binding.progressBar.visible() else binding.progressBar.gone()
    }

    private fun Chip.setSelected() {
        chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.black, null))
        setTextColor(resources.getColor(R.color.white, null))
    }

    private fun Chip.unsetSelected() {
        chipBackgroundColor = ColorStateList.valueOf(resources.getColor(R.color.white, null))
        setTextColor(resources.getColor(R.color.black, null))
    }

    fun navigateToViewANoteSheet(id: Long) {
        try {
            val viewNoteSheet = ViewANoteSheet()
            viewNoteSheet.setFullScreenSheet()
            val bundle = Bundle()
            viewNoteSheet.arguments =
                bundle.apply {
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
