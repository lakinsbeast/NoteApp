package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.databinding.FolderMenuBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import javax.inject.Inject
import javax.inject.Named

class SelectFolderDestinationSheet :
    BaseSheet<FolderMenuBottomSheetBinding>(FolderMenuBottomSheetBinding::inflate) {

    private val args: RenameFolderSheetArgs by navArgs()

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    private lateinit var folderViewModel: FolderViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this)
        }

        folderViewModel = ViewModelProvider(this, folderVmFactory)[FolderViewModel::class.java]

        val dest = findNavController()
        binding.apply {
            deleteBtn.setOnClickListener {
//                dest.navigate()
            }
            renameBtn.setOnClickListener {
                dest.navigate(
                    SelectFolderDestinationSheetDirections.actionSelectFolderDestinationSheetToRenameFolderSheet(
                        args.idFolder
                    )
                )
            }
            makeACopyBtn.setOnClickListener {

            }
            shareBtn.setOnClickListener {

            }
        }
    }

}