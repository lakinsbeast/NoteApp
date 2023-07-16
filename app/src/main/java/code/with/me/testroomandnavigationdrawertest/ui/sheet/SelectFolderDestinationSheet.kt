package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.databinding.FolderMenuBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import javax.inject.Inject
import javax.inject.Named

class SelectFolderDestinationSheet :
    BaseSheet<FolderMenuBottomSheetBinding>(FolderMenuBottomSheetBinding::inflate) {

    private val args: SelectFolderDestinationSheetArgs by navArgs()

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    private lateinit var folderViewModel: FolderViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAppComponent()
        initViewModel()
        initClickListeners()
    }

    private fun initAppComponent() {
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@SelectFolderDestinationSheet)
        }
    }

    private fun initViewModel() {
        folderViewModel =
            ViewModelProvider(this, folderVmFactory).get(FolderViewModel::class.java)
    }

    private fun initClickListeners() {
        val dest = findNavController()
        binding.deleteBtn.setOnClickListener {
            // dest.navigate()
        }
        binding.renameBtn.setOnClickListener {
            dest.navigate(
                SelectFolderDestinationSheetDirections.actionSelectFolderDestinationSheetToRenameFolderSheet(
                    args.idFolder
                )
            )
        }
        binding.makeACopyBtn.setOnClickListener {

        }
        binding.shareBtn.setOnClickListener {

        }
    }
}
