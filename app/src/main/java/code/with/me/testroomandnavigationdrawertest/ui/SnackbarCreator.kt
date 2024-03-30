package code.with.me.testroomandnavigationdrawertest.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.LayoutRes
import androidx.core.content.getSystemService
import code.with.me.testroomandnavigationdrawertest.R
import com.google.android.material.snackbar.Snackbar

fun Snackbar.makeViewANoteSheetBottomBar(
    @LayoutRes layoutRes: Int,
): Snackbar {
    val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    val customView = inflater.inflate(layoutRes, null) as ViewGroup
    val snackbarLayout = view as Snackbar.SnackbarLayout
    val firstBtn = customView.findViewById<ImageButton>(R.id.button1)
    firstBtn.setOnClickListener {
        println("HELLO!")
    }
    val secondBtn = customView.findViewById<ImageButton>(R.id.button2)
    secondBtn.setOnClickListener {
        println("HELLO!")
    }
    snackbarLayout.removeAllViews()
    snackbarLayout.addView(customView, 0)
    return this
}