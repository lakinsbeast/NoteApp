package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.databinding.RenameFolderBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/** todo не используется*/
class RenameFolderSheet :
    BaseSheet<RenameFolderBottomSheetBinding>(RenameFolderBottomSheetBinding::inflate),
    CoroutineScope {
    private val TAG = "RenameFolderSheet"

    private val args: RenameFolderSheetArgs by navArgs()

//    @Inject
//    @Named("folderVMFactory")
//    lateinit var folderVmFactory: ViewModelProvider.Factory
    private val folderViewModel: FolderViewModel by viewModels()
//    lazy {
//        ViewModelProvider(this, folderVmFactory).get(FolderViewModel::class.java)
//    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.renameFolderBtn.setOnClickListener {
            val text = binding.textEdit.editText?.text

            if (text.isNullOrEmpty()) {
                binding.textEdit.error = "Пустое поле названия, введите текст"
                return@setOnClickListener
            }

            launch {
                val folder =
                    Folder(
                        text.toString(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        "",
                        false,
                    ).apply {
                        this.id = args.idFolder
                    }
                folderViewModel.insertFolder(folder)
                withContext(Dispatchers.Main.immediate) {
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
