package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.os.Bundle
import android.view.View
import code.with.me.testroomandnavigationdrawertest.Utils.mainScope
import code.with.me.testroomandnavigationdrawertest.databinding.SeeTextSheetBinding
import code.with.me.testroomandnavigationdrawertest.markdown.BlockQuoteFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.FirstNewLineTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.FirstSpaceTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.HeadingFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.HeadingTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceBracketFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceSquareFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StarFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StarTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StrikethroughFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StrikethroughTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StringToMarkdownTextParser
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
                    val starChecker = StarTextCheckerImpl()
                    val strikethroughTextChecker = StrikethroughTextCheckerImpl()
                    val headingTextChecker = HeadingTextCheckerImpl()
                    val firstNewLineTextCheckerImpl = FirstNewLineTextCheckerImpl()
                    val firstSpaceTextCheckerImpl = FirstSpaceTextCheckerImpl()

                    binding.textView.text = StringToMarkdownTextParser(
                        StarFormatterImpl(starChecker),
                        StrikethroughFormatterImpl(strikethroughTextChecker),
                        HeadingFormatterImpl(headingTextChecker, firstNewLineTextCheckerImpl),
                        BlockQuoteFormatterImpl(firstNewLineTextCheckerImpl),
                        ReferenceSquareFormatterImpl(firstSpaceTextCheckerImpl),
                        ReferenceBracketFormatterImpl()
                    ).getParsedText(text)
                }
            }
        }
    }


}


