package code.with.me.testroomandnavigationdrawertest.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.viewbinding.ViewBinding
import code.with.me.testroomandnavigationdrawertest.data.utils.findActivity
import code.with.me.testroomandnavigationdrawertest.data.utils.getDisplayMetrics
import code.with.me.testroomandnavigationdrawertest.data.utils.gone
import code.with.me.testroomandnavigationdrawertest.data.utils.setCenterGravity
import code.with.me.testroomandnavigationdrawertest.data.utils.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseSheet<VB : ViewBinding>(val get: ((LayoutInflater, ViewGroup?, Boolean) -> VB)) :
    BottomSheetDialogFragment() {
    private var _binding: VB? = null

    var behavior: BottomSheetBehavior<FrameLayout>? = null
    private var fullScreen: Boolean = false
    var isDraggable = true
    var isSheetCancelable = true
    var doSheetBackShadowed = true
    var height: Int = -1
        set(value) {
            view?.let {
                setSheetHeightTo(it, value)
                behavior?.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    private var peekHeight: Int = 1100
    var canHide = true
    var isBackNeedBeBlurred = true
    private var halfExpandedRatio = 0.6f

    // если установить false, то будет работать halfExpanded state, а если true, то нет, но не будет различных багов
    var isFitToContents = true
        set(value) {
            view?.let {
                behavior?.isFitToContents = value
            }
            field = value
        }
    val binding get() = _binding!!

    var onSlide: ((Float) -> Unit) = {}
    var onStateChanged: ((Int, Int) -> Unit) = { oldState, newState -> }

    private lateinit var progressBar: ProgressBar

    private var state: Int = BottomSheetBehavior.STATE_EXPANDED

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
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

    private fun setUpDialogWindow() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
            dialog?.window?.attributes?.blurBehindRadius = 10
            dialog?.window?.setBackgroundBlurRadius(10)
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        if (isBackNeedBeBlurred) {
            setUpDialogWindow()
        }
        initProgressBar()
        addViewsToSheet()

        // убирает закрытие sheet, после нажатия за пределами sheet
        isCancelable = isSheetCancelable
        // убирает затемнение позади sheet
        if (!doSheetBackShadowed) {
            dialog?.window?.setDimAmount(0.02f)
        }
        // убирает темный бэкграунд позади sheet, например, если поставить corner radius 64f, то будет видно затенение по краям
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        behavior = BottomSheetBehavior.from(view.parent as FrameLayout)
        if (fullScreen) {
            setSheetToFullScreen(view)
        }
        behavior?.let { behavior ->
            /**
             * Если не сделать эту проверку, то из-за стейта sheet будет выходит верх своего размера и получится странный вид
             **/
            if (fullScreen) {
                behavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            } else {
                behavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }
            behavior.isHideable = canHide
            behavior.isDraggable = isDraggable
//            behavior.skipCollapsed = true
            behavior.isFitToContents = true
            behavior.halfExpandedRatio = halfExpandedRatio
            behavior.addBottomSheetCallback(
                object :
                    BottomSheetBehavior.BottomSheetCallback() {
                    private var oldState: Int = BottomSheetBehavior.STATE_HALF_EXPANDED

                    override fun onStateChanged(
                        bottomSheet: View,
                        newState: Int,
                    ) {
                        onStateChanged.invoke(newState, oldState)
                        if (newState != BottomSheetBehavior.STATE_DRAGGING && newState != BottomSheetBehavior.STATE_SETTLING) {
                            oldState = newState
                        }
                    }

                    override fun onSlide(
                        bottomSheet: View,
                        slideOffset: Float,
                    ) {
                        onSlide.invoke(slideOffset)
                    }
                },
            )
        }
    }

    private fun setSheetToFullScreen(view: View) {
        val layoutParams = (view.parent as FrameLayout).layoutParams
        layoutParams.height = getDisplayMetrics(activity()).heightPixels
        (view.parent as FrameLayout).layoutParams = layoutParams
    }

    fun setSheetHeightTo(
        view: View,
        height: Int,
    ) {
        val layoutParams = (view.parent as FrameLayout).layoutParams
        layoutParams.height = height
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
        progressBar =
            ProgressBar(context).apply {
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

    fun setStateHalfExpanded() {
        state = BottomSheetBehavior.STATE_HALF_EXPANDED
        setStateToBehavior()
    }

    fun setStateHidden() {
        state = BottomSheetBehavior.STATE_HIDDEN
        setStateToBehavior()
    }

    fun setStateCollapsed() {
        state = BottomSheetBehavior.STATE_COLLAPSED
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
