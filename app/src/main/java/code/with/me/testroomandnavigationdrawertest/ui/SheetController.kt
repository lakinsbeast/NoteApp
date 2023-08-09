package code.with.me.testroomandnavigationdrawertest.ui

import androidx.viewbinding.ViewBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SheetController(val activity: MainActivity) {

    fun showSheet(
        sheet: BottomSheetDialogFragment
    ) {
        sheet.show(activity.supportFragmentManager, sheet.javaClass.simpleName)
    }
}