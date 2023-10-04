package code.with.me.testroomandnavigationdrawertest.data.Utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.os.SystemClock
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import code.with.me.testroomandnavigationdrawertest.R

import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.google.android.material.switchmaterial.SwitchMaterial


fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun findActivity(context: Context): MainActivity {
    return context as MainActivity
}


fun View.setRoundedCornersView(radius: Float, color: Int = Color.BLACK, strokeColor: Int? = null) {
    val backgroundDrawable = GradientDrawable().apply {
        cornerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
    }

    background = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        shapeDrawable.fillColor = ColorStateList.valueOf(color)
        if (strokeColor != null) {
            shapeDrawable.setStroke(5f, ColorStateList.valueOf(strokeColor))
        }

        val layerDrawable = LayerDrawable(arrayOf(shapeDrawable))
        layerDrawable
    } else {
        backgroundDrawable
    }
}

fun BottomNavigationView.setRoundedCorners(radius: Float) {
    val backgroundDrawable = GradientDrawable().apply {
        cornerRadii = floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
        setColor(Color.BLACK)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        val shapeAppearanceModel = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, radius)
            .build()

        val shapeDrawable = MaterialShapeDrawable(shapeAppearanceModel)
        shapeDrawable.fillColor = ColorStateList.valueOf(Color.BLACK)

        val layerDrawable = LayerDrawable(arrayOf(shapeDrawable))
        background = layerDrawable
    } else {
        background = backgroundDrawable
    }
}

fun BottomNavigationView.setCheckable(boolean: Boolean = false) {
    menu.forEachIndexed { index, item ->
        menu[index].isCheckable = boolean
    }
}

fun ProgressBar.setCenterGravity(view: View) {
    when (getViewGroup(view)) {
        is FrameLayout -> {
            val layParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            layParams.gravity = Gravity.CENTER
            this.layoutParams = layParams
        }

        is ConstraintLayout -> {
            val layParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.WRAP_CONTENT,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layParams.circleConstraint = ConstraintLayout.LayoutParams.PARENT_ID
            this.layoutParams = layParams
        }
    }
}

fun View.safeClickListener(delay: Long = 500L, action: () -> Unit) {
    this.setOnClickListener(object : View.OnClickListener {
        private var lastClickTime: Long = 0

        override fun onClick(v: View) {
            if (SystemClock.elapsedRealtime() - lastClickTime < delay) return
            else action()

            lastClickTime = SystemClock.elapsedRealtime()
        }
    })
}

fun getViewGroup(view: View): ViewGroup? {
    try {
        return (view as ViewGroup)
    } catch (e: Exception) {

    }
    return null
}

fun Button.setCancelButton() {
    //У gradient drawable неправильно работает setStroke с кнопками, он не позволяет установить цвет обводки
    this.setRoundedCornersView(32f, Color.BLACK, Color.WHITE)
    setTextColor(resources.getColor(R.color.white, null))
}

fun Button.setConfirmButton() {
    this.setRoundedCornersView(32f, Color.WHITE)
    setTextColor(resources.getColor(R.color.black, null))
}


fun Chip.setChipSelectedDesign() {
    setChipBackgroundColorResource(R.color.white)
    val transparentCheckedIcon = ContextCompat.getDrawable(context, R.drawable.check_mark)
    transparentCheckedIcon?.setTint(Color.BLACK)
    checkedIcon = ContextCompat.getDrawable(context, R.drawable.check_mark)
    setTextColor(Color.BLACK)
}

@SuppressLint("ResourceType")
fun Chip.setChipUnSelectedDesign() {
    setChipBackgroundColorResource(R.color.black)
    setTextColor(Color.WHITE)
}