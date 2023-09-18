package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.QuoteSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.view.View
import code.with.me.testroomandnavigationdrawertest.Utils.mainScope
import code.with.me.testroomandnavigationdrawertest.databinding.SeeTextSheetBinding
import code.with.me.testroomandnavigationdrawertest.markdown.Heading
import code.with.me.testroomandnavigationdrawertest.markdown.Star
import code.with.me.testroomandnavigationdrawertest.markdown.StringToMarkdownTextParser
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Stack
import kotlin.time.Duration.Companion.nanoseconds
import kotlin.time.measureTime

class SeeTextSheet(newText: String) :
    BaseSheet<SeeTextSheetBinding>(SeeTextSheetBinding::inflate) {

    var text = ""

    init {
        text = newText
    }

    var index1 = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (text.isNotEmpty()) {
            val before = System.currentTimeMillis()
            println("before: $before")
            CoroutineScope(Dispatchers.Default).launch {
                mainScope {
                    binding.textView.text = StringToMarkdownTextParser().parseText(text)
                }
            }
        }
    }


}


