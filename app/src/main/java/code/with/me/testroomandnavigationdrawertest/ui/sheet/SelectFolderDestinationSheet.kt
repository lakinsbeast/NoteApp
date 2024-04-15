package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.appComponent
import code.with.me.testroomandnavigationdrawertest.databinding.FolderMenuBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModel
import javax.inject.Inject
import javax.inject.Named

/** todo не используется*/
class SelectFolderDestinationSheet :
    BaseSheet<FolderMenuBottomSheetBinding>(FolderMenuBottomSheetBinding::inflate) {
    private val args: SelectFolderDestinationSheetArgs by navArgs()

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    private val folderViewModel: FolderViewModel by lazy {
        ViewModelProvider(this, folderVmFactory).get(FolderViewModel::class.java)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAppComponent()
        initClickListeners()
    }

    private fun initAppComponent() {
        appComponent.inject(this)
    }

    private fun initClickListeners() {
        val dest = findNavController()
        binding.deleteBtn.setOnClickListener {
            // dest.navigate()
        }
        binding.renameBtn.setOnClickListener {
            dest.navigate(
                SelectFolderDestinationSheetDirections.actionSelectFolderDestinationSheetToRenameFolderSheet(
                    args.idFolder,
                ),
            )
        }
        binding.makeACopyBtn.setOnClickListener {
        }
        binding.shareBtn.setOnClickListener {
        }
    }
}
