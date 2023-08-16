package code.with.me.testroomandnavigationdrawertest.ui

import androidx.viewbinding.ViewBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class SheetController @Inject constructor() {

    fun showSheet(
        activity: MainActivity,
        sheet: BottomSheetDialogFragment
    ) {
        sheet.show(activity.supportFragmentManager, sheet.javaClass.simpleName)
    }
}