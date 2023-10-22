package code.with.me.testroomandnavigationdrawertest.ui.controllers

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import code.with.me.testroomandnavigationdrawertest.ui.FragmentBackStackManager
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

// под чем я был, когда писал это?
class FragmentController @Inject constructor(
    private val getFragmentImpl: IGetFragment,
    private val openFragmentImpl: IOpenFragment,
    private val replaceFragmentImpl: IReplaceFragment,
    private val closeFragmentImpl: ICloseFragment
) {
    fun openFragment(activity: MainActivity, fragment: Fragment, options: FragmentOptions) {
        openFragmentImpl.openFragment(activity, fragment, options)
    }

    fun replaceFragment(activity: MainActivity, fragment: Fragment, options: FragmentOptions) {
        replaceFragmentImpl.replaceFragment(activity, fragment, options)
    }

    fun closeFragment(
        activity: MainActivity, fragment: String
    ) {
        closeFragmentImpl.closeFragment(activity, fragment)
    }


}

// https://kotlinexpertise.com/java-builders-kotlin-dsls/
fun fragmentOptionsBuilder(options: FragmentOptions.() -> Unit): FragmentOptions {
    val fragmentOptions = FragmentOptions()
    fragmentOptions.options()
    return fragmentOptions
}


class FragmentOptions(
    var fragmentLayout: Int = -1,
    var addToBackStack: Boolean = true,
    var clearBackStack: Boolean = false,
    var deleteLast: Boolean = false
)


interface IGetFragment {
    fun getFragment(activity: MainActivity, fragmentTag: String): Fragment?
}

interface IOpenFragment {
    fun openFragment(activity: MainActivity, fragment: Fragment, options: FragmentOptions)
}

interface IReplaceFragment {
    fun replaceFragment(activity: MainActivity, fragment: Fragment, options: FragmentOptions)
}

interface ICloseFragment {
    fun closeFragment(activity: MainActivity, fragmentTag: String)
}

interface IClearBackStack {
    fun clearBackStackIfNeeded(clearBackStack: Boolean, fragmentManager: FragmentManager)
}

interface IDeleteLastFragmentIfNeeded {
    fun deleteLastFragmentIfNeeded(deleteLast: Boolean, fragmentManager: FragmentManager)
}

class GetFragmentImpl @Inject constructor() : IGetFragment {
    override fun getFragment(activity: MainActivity, fragmentTag: String): Fragment? {
        for (fragment in activity.supportFragmentManager.fragments) {
            if (fragment.tag == fragmentTag) {
                return fragment
            }
        }
        return null
    }
}

class OpenFragmentImpl @Inject constructor(
    private val fragmentBackStackManager: FragmentBackStackManager
) : IOpenFragment {
    override fun openFragment(
        activity: MainActivity,
        fragment: Fragment,
        options: FragmentOptions
    ) {
        fragmentBackStackManager.clearBackStackIfNeeded(
            options.clearBackStack,
            activity.supportFragmentManager
        )
        fragmentBackStackManager.deleteLastFragmentIfNeeded(
            options.deleteLast,
            activity.supportFragmentManager
        )

        activity.supportFragmentManager.beginTransaction()
            .add(options.fragmentLayout, fragment, fragment.javaClass.simpleName)
            .addToBackStack(if (options.addToBackStack) fragment.javaClass.simpleName else null)
            .commit()
    }

    fun showSheet(
        activity: MainActivity,
        sheet: BottomSheetDialogFragment
    ) {
        sheet.show(activity.supportFragmentManager, sheet.javaClass.simpleName)
    }
}

class ReplaceFragmentImpl @Inject constructor(
    private val fragmentBackStackManager: FragmentBackStackManager
) : IReplaceFragment {
    override fun replaceFragment(
        activity: MainActivity,
        fragment: Fragment,
        options: FragmentOptions
    ) {
        fragmentBackStackManager.clearBackStackIfNeeded(
            options.clearBackStack,
            activity.supportFragmentManager
        )
        fragmentBackStackManager.deleteLastFragmentIfNeeded(
            options.deleteLast,
            activity.supportFragmentManager
        )

        activity.supportFragmentManager.beginTransaction()
            .replace(options.fragmentLayout, fragment, fragment.javaClass.simpleName)
            .addToBackStack(if (options.addToBackStack) fragment.javaClass.simpleName else null)
            .commit()
    }
}

class CloseFragmentImpl @Inject constructor(
    private val getFragmentImpl: IGetFragment
) : ICloseFragment {
    override fun closeFragment(activity: MainActivity, fragmentTag: String) {
        try {
            val fragment = getFragmentImpl.getFragment(activity, fragmentTag)
            if (fragment == null) {
                throw IllegalStateException()
            } else {
                activity.supportFragmentManager.beginTransaction().remove(fragment).commit()
            }
        } catch (e: Exception) {
            Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}



