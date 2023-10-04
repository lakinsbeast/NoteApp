package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import code.with.me.testroomandnavigationdrawertest.data.enums.NoteItemsCallback
import code.with.me.testroomandnavigationdrawertest.databinding.NoteMenuSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class NoteMenuSheet(private val action: (NoteItemsCallback) -> Unit) :
    BaseSheet<NoteMenuSheetBinding>(NoteMenuSheetBinding::inflate) {

    /**
     * набросок
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            shareLay.setOnClickListener {
                action.invoke(NoteItemsCallback.SHARE)
                dismiss()
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
}