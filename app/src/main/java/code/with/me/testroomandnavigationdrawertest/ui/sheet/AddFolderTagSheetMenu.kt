package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.databinding.AddFolderBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.FolderTagViewModel
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

class AddFolderTagSheetMenu :
    BaseSheet<AddFolderBottomSheetBinding>(AddFolderBottomSheetBinding::inflate),
    CoroutineScope {
    private val TAG = "AddFolderSheet"

    @Inject
    @Named("folderTagVMFactory")
    lateinit var tagVmFactory: ViewModelProvider.Factory
    private lateinit var folderTagViewModel: FolderTagViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this)
        }

        folderTagViewModel = ViewModelProvider(this, tagVmFactory)[FolderTagViewModel::class.java]

        binding.apply {
            val text = textEdit.editText?.text

            addFolderBtn.setOnClickListener {
                text?.let {
                    if (text.isEmpty()) {
                        textEdit.error = "Пустое поле названия, введите текст"
                        return@setOnClickListener
                    }
                    launch {
                        folderTagViewModel.insertTag(
                            FolderTag(
                                text.toString(),
                                System.currentTimeMillis(),
                                System.currentTimeMillis(),
                                System.currentTimeMillis()
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