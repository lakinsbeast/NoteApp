package code.with.me.testroomandnavigationdrawertest.ui.controllers

import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class SheetController @Inject constructor() {

    fun showSheet(
        activity: MainActivity,
        sheet: BottomSheetDialogFragment
    ) {
        sheet.show(activity.supportFragmentManager, sheet.javaClass.simpleName)
    }

    fun closeSheet(
        activity: MainActivity,
        sheet: BottomSheetDialogFragment
    ) {
        sheet.dismiss()
    }
}