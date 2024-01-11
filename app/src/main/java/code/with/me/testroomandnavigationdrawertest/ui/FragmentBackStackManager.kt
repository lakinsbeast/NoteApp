package code.with.me.testroomandnavigationdrawertest.ui

import androidx.fragment.app.FragmentManager
import code.with.me.testroomandnavigationdrawertest.ui.controllers.IClearBackStack
import code.with.me.testroomandnavigationdrawertest.ui.controllers.IDeleteLastFragmentIfNeeded

// ?? помоему дичь
class FragmentBackStackManager :
    IClearBackStack,
    IDeleteLastFragmentIfNeeded {
    override fun clearBackStackIfNeeded(
        clearBackStack: Boolean,
        fragmentManager: FragmentManager,
    ) {
        if (clearBackStack) {
            for (i in 0 until fragmentManager.backStackEntryCount - 1) {
                fragmentManager.popBackStack()
            }
        }
    }

    override fun deleteLastFragmentIfNeeded(
        deleteLast: Boolean,
        fragmentManager: FragmentManager,
    ) {
        if (deleteLast) {
            try {
                fragmentManager.popBackStack()
            } catch (_: Exception) {
            }
        }
    }
}
