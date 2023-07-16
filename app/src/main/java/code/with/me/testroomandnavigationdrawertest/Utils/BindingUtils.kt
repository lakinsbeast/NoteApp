package code.with.me.testroomandnavigationdrawertest.Utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Build
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ProgressBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.forEach
import androidx.core.view.forEachIndexed
import androidx.core.view.get
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.shape.CornerFamily
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel


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
        menu[index].setCheckable(boolean)
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


fun getViewGroup(view: View): ViewGroup? {
    try {
        return (view as ViewGroup)
    } catch (e: Exception) {

    }
    return null
}

