package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.databinding.RenameFolderBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class RenameFolderSheet :
    BaseSheet<RenameFolderBottomSheetBinding>(RenameFolderBottomSheetBinding::inflate),
    CoroutineScope {
    private val TAG = "RenameFolderSheet"

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

        binding.apply {
            val text = textEdit.editText?.text

            renameFolderBtn.setOnClickListener {
                text?.let {
                    if (text.isEmpty()) {
                        textEdit.error = "Пустое поле названия, введите текст"
                        return@setOnClickListener
                    }
                    launch {
                        async {
                            folderViewModel.insertFolder(
                                Folder(
                                    text.toString(),
                                    System.currentTimeMillis(),
                                    System.currentTimeMillis(),
                                    System.currentTimeMillis(),
                                    "",
                                    false
                                ).apply {
                                    this.id = args.idFolder
                                })
                        }.await()
                        withContext(Dispatchers.Main) {
                            findNavController().popBackStack()
                        }
                    }
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