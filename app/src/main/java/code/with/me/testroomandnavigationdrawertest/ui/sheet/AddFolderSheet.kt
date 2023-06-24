package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.databinding.AddFolderBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class AddFolderSheet: BaseSheet<AddFolderBottomSheetBinding>(AddFolderBottomSheetBinding::inflate), CoroutineScope {
    private val TAG = "AddFolderSheet"

    @Inject
    @Named("folderVMFactory")
    lateinit var folderVmFactory: ViewModelProvider.Factory
    private lateinit var folderViewModel: FolderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this)
        }

        folderViewModel = ViewModelProvider(this, folderVmFactory)[FolderViewModel::class.java]

        binding.apply {
            val text = textEdit.editText?.text

            addFolderBtn.setOnClickListener {
                text?.let {
                    if (text.isEmpty()) {
                        textEdit.error = "Пустое поле названия, введите текст"
                        return@setOnClickListener
                    }
                    launch {
                        folderViewModel.insertFolder(Folder(text.toString()))
                    }
                    findNavController().popBackStack()
                }

            }
        }
    }

    fun showSheet(fragmentManager: FragmentManager) {
        super.showSheet(fragmentManager, TAG)
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