package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteTag
import code.with.me.testroomandnavigationdrawertest.databinding.AddNoteBottomSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteTagViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.coroutines.CoroutineContext

class AddNoteTagSheetMenu :
    BaseSheet<AddNoteBottomSheetBinding>(AddNoteBottomSheetBinding::inflate),
    CoroutineScope {
    private val TAG = "AddNoteTagSheetMenu"

    @Inject
    @Named("noteTagVMFactory")
    lateinit var tagVmFactory: ViewModelProvider.Factory
    private lateinit var noteTagViewModel: NoteTagViewModel

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAppComponent()
        initViewModel()
        initClickListeners()
    }

    private fun initAppComponent() {
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this@AddNoteTagSheetMenu)
        }
    }

    private fun initViewModel() {
        noteTagViewModel =
            ViewModelProvider(this, tagVmFactory).get(NoteTagViewModel::class.java)
    }

    private fun initClickListeners() {
        binding.addFolderBtn.setOnClickListener {
            val text = binding.textEdit.editText?.text

            if (text.isNullOrEmpty()) {
                binding.textEdit.error = "Пустое поле названия, введите текст"
                return@setOnClickListener
            }

            launch {
                noteTagViewModel.insertTag(
                    NoteTag(
                        text.toString(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                        System.currentTimeMillis(),
                    ),
                )
            }
            findNavController().popBackStack()
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
