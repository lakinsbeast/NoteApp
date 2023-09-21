package code.with.me.testroomandnavigationdrawertest.markdown

import android.text.SpannableString

interface IMarkdownTextParser {
    suspend fun getParsedText(text: String): SpannableString

    suspend fun parseText(text: String): SpannableString
}