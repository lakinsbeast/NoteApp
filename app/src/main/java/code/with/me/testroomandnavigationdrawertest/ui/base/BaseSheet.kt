package code.with.me.testroomandnavigationdrawertest.ui.base

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateMargins
import androidx.viewbinding.ViewBinding
import code.with.me.testroomandnavigationdrawertest.Utils.findActivity
import code.with.me.testroomandnavigationdrawertest.Utils.getDisplayMetrics
import code.with.me.testroomandnavigationdrawertest.Utils.gone
import code.with.me.testroomandnavigationdrawertest.Utils.setCenterGravity
import code.with.me.testroomandnavigationdrawertest.Utils.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseSheet<VB : ViewBinding>(val get: ((LayoutInflater, ViewGroup?, Boolean) -> VB)) :
    BottomSheetDialogFragment() {

    private var _binding: VB? = null

    var behavior: BottomSheetBehavior<FrameLayout>? = null
    private var fullScreen: Boolean = false
    private var peekHeight: Int = 1100
    private var canHide = true
    val binding get() = _binding!!

    var onSlide: ((Float) -> Unit) = {}
    var onStateChanged: ((Int, Int) -> Unit) = { oldState, newState -> }

    private lateinit var progressBar: ProgressBar

    private var state: Int = BottomSheetBehavior.STATE_EXPANDED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        peekHeight = getDisplayMetrics(activity()).heightPixels
        _binding = get.invoke(inflater, container, false)
        return binding.root
    }

    fun showProgressBar(show: Boolean) {
        if (show) {
            progressBar.visible()
        } else {
            progressBar.gone()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProgressBar()
        addViewsToSheet()

        behavior = BottomSheetBehavior.from(view.parent as FrameLayout)
        if (fullScreen) {
            setSheetToFullScreen(view)
        }
        behavior?.let { behavior ->
            behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            behavior.isHideable = canHide
            behavior.skipCollapsed = true
            behavior.isFitToContents = false
            behavior.halfExpandedRatio = 0.6f
            behavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {
                private var oldState: Int = BottomSheetBehavior.STATE_HALF_EXPANDED
                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    onStateChanged.invoke(newState, oldState)
                    if (newState != BottomSheetBehavior.STATE_DRAGGING && newState != BottomSheetBehavior.STATE_SETTLING) {
                        oldState = newState
                    }
                }

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                    onSlide.invoke(slideOffset)
                }
            })
        }
    }

    private fun setSheetToFullScreen(view: View) {
        val layoutParams = (view.parent as FrameLayout).layoutParams
        layoutParams.height = getDisplayMetrics(activity()).heightPixels
        (view.parent as FrameLayout).layoutParams = layoutParams
    }

    private fun addViewsToSheet() {
        try {
            (binding.root as ViewGroup).addView(progressBar)
            progressBar.gone()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun initProgressBar() {
        progressBar = ProgressBar(context).apply {
            setCenterGravity(binding.root)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    fun setStateExpanded() {
        state = BottomSheetBehavior.STATE_EXPANDED
        setStateToBehavior()
    }

    fun setStateHidden() {
        state = BottomSheetBehavior.STATE_HIDDEN
        setStateToBehavior()
    }

    private fun setStateToBehavior() {
        behavior?.state = state
    }

    fun setFullScreenSheet(boolean: Boolean = true) {
        fullScreen = boolean
    }

    fun activity() = findActivity(requireContext())

}