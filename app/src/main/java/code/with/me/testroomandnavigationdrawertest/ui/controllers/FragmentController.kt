package code.with.me.testroomandnavigationdrawertest.ui.controllers

import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.ui.FragmentBackStackManager
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import javax.inject.Inject

class FragmentController @Inject constructor(
    private val getFragmentImpl: IGetFragment,
    private val openFragmentImpl: IOpenFragment,
    private val replaceFragmentImpl: IReplaceFragment,
    private val closeFragmentImpl: ICloseFragment,
) : IGetFragment by getFragmentImpl, IOpenFragment by openFragmentImpl,
    IReplaceFragment by replaceFragmentImpl, ICloseFragment by closeFragmentImpl

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
    var deleteLast: Boolean = false,
)

interface IGetFragment {
    fun getFragment(
        activity: MainActivity,
        fragmentTag: String,
    ): Fragment?
}

interface IOpenFragment {
    fun openFragment(
        activity: MainActivity,
        fragment: Fragment,
        options: FragmentOptions,
        animEnter: Int = R.anim.enter_from_right,
        animExit: Int = R.anim.exit_to_left
    )
}

interface IReplaceFragment {
    fun replaceFragment(
        activity: MainActivity,
        fragment: Fragment,
        options: FragmentOptions,
    )
}

interface ICloseFragment {
    fun closeFragment(
        activity: MainActivity,
        fragmentTag: String,
    )
}

interface IClearBackStack {
    fun clearBackStackIfNeeded(
        clearBackStack: Boolean,
        fragmentManager: FragmentManager,
    )
}

interface IDeleteLastFragmentIfNeeded {
    fun deleteLastFragmentIfNeeded(
        deleteLast: Boolean,
        fragmentManager: FragmentManager,
    )
}

class GetFragmentImpl
@Inject
constructor() : IGetFragment {
    override fun getFragment(
        activity: MainActivity,
        fragmentTag: String,
    ): Fragment? {
        for (fragment in activity.supportFragmentManager.fragments) {
            if (fragment.tag == fragmentTag) {
                return fragment
            }
        }
        return null
    }
}

class OpenFragmentImpl
@Inject
constructor(
    private val fragmentBackStackManager: FragmentBackStackManager,
) : IOpenFragment {
    override fun openFragment(
        activity: MainActivity,
        fragment: Fragment,
        options: FragmentOptions,
        animEnter: Int,
        animExit: Int,
    ) {
        fragmentBackStackManager.clearBackStackIfNeeded(
            options.clearBackStack,
            activity.supportFragmentManager,
        )
        fragmentBackStackManager.deleteLastFragmentIfNeeded(
            options.deleteLast,
            activity.supportFragmentManager,
        )

        activity.supportFragmentManager.beginTransaction()
            .setCustomAnimations(animEnter, animExit, animEnter, animExit)
            .add(options.fragmentLayout, fragment, fragment.javaClass.simpleName)
            .addToBackStack(if (options.addToBackStack) fragment.javaClass.simpleName else null)
            .commit()
    }

    fun showSheet(
        activity: MainActivity,
        sheet: BottomSheetDialogFragment,
    ) {
        sheet.show(activity.supportFragmentManager, sheet.javaClass.simpleName)
    }
}

class ReplaceFragmentImpl
@Inject
constructor(
    private val fragmentBackStackManager: FragmentBackStackManager,
) : IReplaceFragment {
    override fun replaceFragment(
        activity: MainActivity,
        fragment: Fragment,
        options: FragmentOptions,
    ) {
        fragmentBackStackManager.clearBackStackIfNeeded(
            options.clearBackStack,
            activity.supportFragmentManager,
        )
        fragmentBackStackManager.deleteLastFragmentIfNeeded(
            options.deleteLast,
            activity.supportFragmentManager,
        )

        activity.supportFragmentManager.beginTransaction()
            .replace(options.fragmentLayout, fragment, fragment.javaClass.simpleName)
            .addToBackStack(if (options.addToBackStack) fragment.javaClass.simpleName else null)
            .commit()
    }
}

class CloseFragmentImpl
@Inject
constructor(
    private val getFragmentImpl: IGetFragment,
) : ICloseFragment {
    override fun closeFragment(
        activity: MainActivity,
        fragmentTag: String,
    ) {
        try {
            val fragment = getFragmentImpl.getFragment(activity, fragmentTag)
            fragment?.let {
                activity.supportFragmentManager.beginTransaction().remove(fragment).commit()

            } ?: run {
                throw IllegalStateException()
            }

        } catch (e: Exception) {
            Toast.makeText(activity, e.localizedMessage, Toast.LENGTH_LONG).show()
        }
    }
}
