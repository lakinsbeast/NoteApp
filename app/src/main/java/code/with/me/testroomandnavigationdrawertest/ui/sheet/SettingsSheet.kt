package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.PaintDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewOutlineProvider
import code.with.me.testroomandnavigationdrawertest.data.Utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.databinding.SettingsSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel

class SettingsSheet(val slide: (Float) -> Unit) :
    BaseSheet<SettingsSheetBinding>(SettingsSheetBinding::inflate) {


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        onSlide = {
            slide.invoke(it)
            binding.include2.root.alpha = 1.0f - it
            binding.mainLayout.setRoundedCornersView((1.0f - it) * 64f, Color.WHITE)
        }
        setStateCollapsed()
    }

}