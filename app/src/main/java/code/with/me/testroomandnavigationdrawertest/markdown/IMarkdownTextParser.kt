package code.with.me.testroomandnavigationdrawertest.markdown

import android.text.SpannableString

interface IMarkdownTextParser {
    suspend fun parseText(text: String): SpannableString

    suspend fun setTextSpannable(text: String): SpannableString
}