package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.Utils.gone
import code.with.me.testroomandnavigationdrawertest.data.Utils.visible
import code.with.me.testroomandnavigationdrawertest.data.enums.NoteItemsCallback
import code.with.me.testroomandnavigationdrawertest.databinding.NoteMenuSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MakeNoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteMenuSheetViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Named

class NoteMenuSheet(private val idOfNote: Int, private val action: (NoteItemsCallback) -> Unit) :
    BaseSheet<NoteMenuSheetBinding>(NoteMenuSheetBinding::inflate) {

    @Inject
    @Named("noteMenuSheetVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteMenuSheetViewModel: NoteMenuSheetViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppComponent()
    }
    private fun initAppComponent() {
        activity?.let {
            val appComponent = (it.application as NotesApplication).appComponent
            appComponent.inject(this)
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAndListenViewModel()
        binding.apply {

            progressBar.playAnimation()
            shareLay.setOnClickListener {
                showCustomProgressBar(true)
                noteMenuSheetViewModel.shareText(idOfNote)
            }
            moveLay.setOnClickListener {
                action.invoke(NoteItemsCallback.MOVE)
                dismiss()
            }
            favoriteLay.setOnClickListener {
                action.invoke(NoteItemsCallback.FAVORITE)
                dismiss()
            }
            lockLay.setOnClickListener {
                action.invoke(NoteItemsCallback.LOCK)
                dismiss()
            }
            deleteLay.setOnClickListener {
                action.invoke(NoteItemsCallback.DELETE)
                dismiss()
            }
            closeBtn.setOnClickListener {
                dismiss()
            }
        }
    }

    fun showCustomProgressBar(show: Boolean) {
        if (show) {
            binding.progressBarLay.visible()
        } else {
            binding.progressBarLay.gone()
        }
    }

    private fun initAndListenViewModel() {
        noteMenuSheetViewModel =
            ViewModelProvider(this, factory)[NoteMenuSheetViewModel::class.java]
        noteMenuSheetViewModel.shareTextLiveData.observe(viewLifecycleOwner) {
            println("it: $it")
            if (it.isNotBlank()) {
                val intent = Intent()
                intent.action = Intent.ACTION_SEND
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_TEXT, it)
                startActivity(Intent.createChooser(intent, "Поделиться текстом с"))
            }
            showCustomProgressBar(false)
        }
    }
}