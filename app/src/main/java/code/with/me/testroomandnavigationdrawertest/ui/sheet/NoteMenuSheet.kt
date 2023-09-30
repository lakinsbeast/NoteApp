package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import androidx.core.view.doOnLayout
import code.with.me.testroomandnavigationdrawertest.databinding.NoteMenuSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class NoteMenuSheet : BaseSheet<NoteMenuSheetBinding>(NoteMenuSheetBinding::inflate) {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.root.doOnLayout {
//            behavior?.setPeekHeight(binding.standardBottomSheet.measuredHeight/2, true)
//            height = binding.standardBottomSheet.measuredHeight
        }
        super.onViewCreated(view, savedInstanceState)
    }
}