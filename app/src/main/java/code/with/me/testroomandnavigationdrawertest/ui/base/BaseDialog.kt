package code.with.me.testroomandnavigationdrawertest.ui.base

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import code.with.me.testroomandnavigationdrawertest.data.const.const.Companion.ROUNDED_CORNERS
import code.with.me.testroomandnavigationdrawertest.data.const.const.Companion.STANDARD_BLUR_RADIUS
import code.with.me.testroomandnavigationdrawertest.data.utils.findActivity
import code.with.me.testroomandnavigationdrawertest.data.utils.setRoundedCornersView

// лучше использовать DialogFragment, вместо Dialog
@SuppressLint("ResourceType")
abstract class BaseDialog<VB : ViewBinding>(
    val get: ((LayoutInflater, ViewGroup?, Boolean) -> VB),
) : DialogFragment() {
    private var _binding: VB? = null
    val binding get() = _binding!!

    /** отвечает за затемнение позади диалога*/
    var dimAmount = 1f
        set(value) {
            dialog?.window?.setDimAmount(value)
            field = value
        }
    var isDialogCancelable = true
        set(value) {
            isCancelable = value
            field = value
        }

    /** нужно ли использовать размытие позади*/
    var isBehindNeedBlurred = true
        set(value) {
            if (value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    dialog?.window?.attributes?.blurBehindRadius = STANDARD_BLUR_RADIUS
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dialog?.window?.addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
                    dialog?.window?.attributes?.blurBehindRadius = 0
                }
            }
            // если не добавить эту строку, то не применится блюр, пока я хз почему так
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            field = value
        }

    // не работает
    var isBackgroundNeedBlurred = true
        set(value) {
            println("value: $value")
            if (value) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dialog?.window?.setBackgroundBlurRadius(20)
                    /** полезно для бюра картинок всяких или cardView каких-нибудь, например, для защищенных заметок*/
//                    binding.root.setRenderEffect(
//                        RenderEffect.createBlurEffect(
//                            3f,
//                            3f,
//                            Shader.TileMode.DECAL
//                        )
//                    )
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    dialog?.window?.setBackgroundBlurRadius(0)
                }
            }
            // если не добавить эту строку, то не применится блюр, пока я хз почему так
            dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            field = value
        }

    fun activity() = findActivity(requireContext())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = get(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    /** делает углы диалога закругленными*/
    fun setCornerRounded(view: View) {
        view.setRoundedCornersView(ROUNDED_CORNERS)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
