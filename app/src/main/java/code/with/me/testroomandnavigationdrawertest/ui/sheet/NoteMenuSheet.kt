package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.utils.gone
import code.with.me.testroomandnavigationdrawertest.data.utils.setUpperRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.data.utils.visible
import code.with.me.testroomandnavigationdrawertest.data.enums.NoteItemsCallback
import code.with.me.testroomandnavigationdrawertest.databinding.NoteMenuSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteMenuSheetViewModel
import javax.inject.Inject
import javax.inject.Named

class NoteMenuSheet(private val idOfNote: Long, private val action: (NoteItemsCallback) -> Unit) :
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

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAndListenViewModel()
        binding.mainLayout.setUpperRoundedCornersView(
            (1.0f) * 64f,
            Color.WHITE,
            Color.BLACK,
            (1.0f) * 5f,
        )
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
            // TODO сделать так, чтоб в зависимости от того в избранном он или нет менялся текст и иконка
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
        setStateCollapsed()
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
