package code.with.me.testroomandnavigationdrawertest.ui

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment

class FragmentController(val activity: MainActivity, private val fragmentLayout: Int) {

    fun getFragment(
        fragmentTag: String
    ): Fragment? {
//        return activity.supportFragmentManager.findFragmentByTag("MainScreenFragment")
        for (fragment in activity.supportFragmentManager.fragments) {
            if (fragment.tag == fragmentTag) {
                return fragment
            }
        }
        return null
    }


    //https://stackoverflow.com/a/62354505
    /**
     * setReorderingAllowed(true) - выполняет оптимизацию, но может случайно вызывать
     * onCreate у В, до onDestroy у А
     **/
    fun openFragment(
        fragment: Fragment,
        deleteLast: Boolean = false,
        fragmentLay: Int = fragmentLayout,
        addToBackStack: Boolean = true,
        clearBackStack: Boolean = false
    ) {
        if (clearBackStack) {
            for (i in 0 until activity.supportFragmentManager.backStackEntryCount - 1) {
                activity.supportFragmentManager.popBackStack()
            }
        }
        activity.supportFragmentManager.let { manager ->
            if (deleteLast) {
                try {
                    manager.popBackStack()
                } catch (_: Exception) {
                }
            }
            manager.beginTransaction()
                .add(fragmentLay, fragment, fragment.javaClass.simpleName)
                .addToBackStack(if (addToBackStack) fragment.javaClass.simpleName else null)
                .commit()
        }
    }

    fun replaceFragment(
        fragment: Fragment,
        deleteLast: Boolean = false,
        fragmentLay: Int = fragmentLayout,
        addToBackStack: Boolean = true,
        clearBackStack: Boolean = false
    ) {
        if (clearBackStack) {
            for (i in 0 until activity.supportFragmentManager.backStackEntryCount - 1) {
                activity.supportFragmentManager.popBackStack()
            }
        }
        activity.supportFragmentManager.let { manager ->
            if (deleteLast) {
                try {
                    manager.popBackStack()
                } catch (_: Exception) {
                }
            }
            manager.beginTransaction()
                .replace(fragmentLay, fragment, fragment.javaClass.simpleName)
                .addToBackStack(if (addToBackStack) fragment.javaClass.simpleName else null)
                .commit()
        }
    }

    fun closeFragment(
        fragmentTag: String
    ) {
        try {
            val fragment = getFragment(fragmentTag)
            if (fragment == null) {
                throw IllegalStateException()
            } else {
                activity.supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
        }

    }

    fun closeLastFragment() {

    }
}