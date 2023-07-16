package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.databinding.AddFolderBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import com.google.android.material.R
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
import kotlin.math.roundToInt


class AddFolderSheet : BaseSheet<AddFolderBottomSheetBinding>(AddFolderBottomSheetBinding::inflate),
    CoroutineScope {
    private val TAG = "AddFolderSheet"

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    private lateinit var folderViewModel: FolderViewModel

    private val selectedChips = mutableListOf<Int>()
    private val isFavorite = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this)
        }
//        val wm = requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
//        val d = wm.defaultDisplay;
//        val dm = DisplayMetrics()
////        val heigth = d.getMetrics(dm)
//
//        println("height: $dm.heightPixels")
//
//        binding.standardBottomSheet.updateLayoutParams {
//            height = dm.heightPixels
//        }


        folderViewModel = ViewModelProvider(this, folderVmFactory)[FolderViewModel::class.java]

        val listTags = arguments?.getParcelableArray("listFolderTags")

        println("addfoldersheet")
        if (!listTags.isNullOrEmpty()) {
            binding.chipGroup.removeAllViews()
            binding.chipGroup.visible()
            listTags?.forEach { parcel ->
                println("addfoldersheet2")
                println(parcel.toString())

                val folderTag = parcel as? FolderTag
                folderTag?.let {
                    println("chip: ${it.name}")
                    val chip = Chip(
                        binding.chipGroup.context,
                        null,
                        R.style.Widget_MaterialComponents_Chip_Filter
                    )
                    chip.text = it.name
                    chip.isClickable = true
                    chip.isCheckable = false
                    //если установить в стиле, то не работает
                    chip.setChipBackgroundColorResource(code.with.me.testroomandnavigationdrawertest.R.color.white)
                    chip.chipStrokeWidth = 2f
                    chip.chipStrokeColor = ColorStateList.valueOf(
                        ContextCompat.getColor(
                            requireContext(),
                            code.with.me.testroomandnavigationdrawertest.R.color.black
                        )
                    )
                    chip.chipCornerRadius = 15f
                    chip.setOnClickListener { view ->
                        if (selectedChips.contains(it.id)) {
                            selectedChips.remove(it.id)
                            chip.setChipBackgroundColorResource(code.with.me.testroomandnavigationdrawertest.R.color.white)
                        } else {
                            selectedChips.add(it.id)
                            chip.setChipBackgroundColorResource(code.with.me.testroomandnavigationdrawertest.R.color.gray)
                        }
                    }
                    binding.chipGroup.addView(chip)
                }
            }
        } else {
            binding.chipGroup.gone()
        }

        binding.apply {
            val text = textEdit.editText?.text

            addFolderBtn.setOnClickListener {
                text?.let {
                    if (text.isEmpty()) {
                        textEdit.error = "Пустое поле названия, введите текст"
                        return@setOnClickListener
                    }
                    launch {
                        folderViewModel.insertFolder(
                            Folder(
                                text.toString(),
                                System.currentTimeMillis(),
                                System.currentTimeMillis(),
                                System.currentTimeMillis(),
                                selectedChips.joinToString(
                                    ","
                                ),
                                isFavorite
                            )
                        )
                    }
                    findNavController().popBackStack()
                }

            }
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