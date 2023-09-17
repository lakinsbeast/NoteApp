package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.QuoteSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.view.View
import code.with.me.testroomandnavigationdrawertest.Utils.mainScope
import code.with.me.testroomandnavigationdrawertest.databinding.SeeTextSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Stack

class SeeTextSheet(newText: String) :
    BaseSheet<SeeTextSheetBinding>(SeeTextSheetBinding::inflate) {

    var text = ""

    init {
        text = newText
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (text.isNotEmpty()) {
            var index = 0
            CoroutineScope(Dispatchers.Default).launch {
                while (index < text.length) {
                    when {
                        skipIteration -> {
                            index++
                            skipIteration = false
                            continue
                        }

                        skipTwoIteration -> {
                            index += 2
                            skipTwoIteration = false
                            continue
                        }

                        skipThreeIteration -> {
                            index += 3
                            skipThreeIteration = false
                            continue
                        }

                        skipFourthIteration -> {
                            index += 4
                            skipFourthIteration = false
                            continue
                        }

                        skipFifthIteration -> {
                            index += 5
                            skipFifthIteration = false
                            continue
                        }

                        skipSixthIteration -> {
                            index += 6
                            skipSixthIteration = false
                            continue
                        }
                    }
//                    if (skipIteration) {
//                        index++
//                        skipIteration = false
//                        continue
//                    }
//                    if (skipTwoIteration) {
//                        index += 2
//                        skipTwoIteration = false
//                        continue
//                    }
                    doWithTextNew(text, index)
                    index++
                }
                async {
                    setSpannableStringToTextView()
                }
            }
        }
    }

    private suspend fun CoroutineScope.setSpannableStringToTextView() {
        val spannableString =
            SpannableString(text)
        if (starHash.isNotEmpty()) {
            starHash.forEach { (key, value) ->
                spannableString.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    key,
                    value,
                    0
                )
            }
        }
        if (twoStarHash.isNotEmpty()) {
            twoStarHash.forEach { (key, value) ->
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    key,
                    value,
                    0
                )
            }
        }
        if (threeStarHash.isNotEmpty()) {
            threeStarHash.forEach { (key, value) ->
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD_ITALIC),
                    key,
                    value,
                    0
                )
            }
        }
        if (strikethroughHash.isNotEmpty()) {
            strikethroughHash.forEach { (key, value) ->
                spannableString.setSpan(
                    StrikethroughSpan(),
                    key,
                    value,
                    0
                )
            }
        }
        if (firstHeadingHash.isNotEmpty()) {
            firstHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(35, true),
                    key,
                    value,
                    0
                )
            }
        }
        if (secondHeadingHash.isNotEmpty()) {
            secondHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(30, true),
                    key,
                    value,
                    0
                )
            }
        }
        if (thirdHeadingHash.isNotEmpty()) {
            thirdHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(25, true),
                    key,
                    value,
                    0
                )
            }
        }
        if (fourthHeadingHash.isNotEmpty()) {
            fourthHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(20, true),
                    key,
                    value,
                    0
                )
            }
        }
        if (fifthHeadingHash.isNotEmpty()) {
            fifthHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(15, true),
                    key,
                    value,
                    0
                )
            }
        }
        if (sixthHeadingHash.isNotEmpty()) {
            sixthHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(10, true),
                    key,
                    value,
                    0
                )
            }
        }
        if (blockQuoteHash.isNotEmpty()) {
            blockQuoteHash.forEach { (key, value) ->
                spannableString.setSpan(
                    QuoteSpan(),
                    key,
                    value,
                    0
                )
            }
        }
        if (referenceHash.isNotEmpty()) {
            referenceHash.forEach { (key, value) ->
                spannableString.setSpan(
                    URLSpan("https://google.com"),
                    value,
                    checkFirstSpace(text, value),
                    0
                )
            }
        }
        mainScope {
            binding.textView.text =
                spannableString
        }
    }


    //STAR--------------------------------------
//    var startStarCursor = -1
//    var endStarCursor = -1

    var starStack = Stack<Int>()
    var starHash = HashMap<Int, Int>()

//    var startTwoStarCursor = -1
//    var endTwoStarCursor = -1

    var twoStarStack = Stack<Int>()
    var twoStarHash = HashMap<Int, Int>()

//    var startThreeStarCursor = -1
//    var endThreeStarCursor = -1

    var threeStarStack = Stack<Int>()
    var threeStarHash = HashMap<Int, Int>()

    //--------------------------------------
    //STRIKETHROUGH--------------------------------------
//    var startStrikethroughCursor = -1
//    var endStrikethroughCursor = -1
    var strikethroughStack = Stack<Int>()
    var strikethroughHash = HashMap<Int, Int>()

    //--------------------------------------
    //HEADING--------------------------------------
    var firstHeadingHash = HashMap<Int, Int>()
    var secondHeadingHash = HashMap<Int, Int>()
    var thirdHeadingHash = HashMap<Int, Int>()
    var fourthHeadingHash = HashMap<Int, Int>()
    var fifthHeadingHash = HashMap<Int, Int>()
    var sixthHeadingHash = HashMap<Int, Int>()

    //--------------------------------------
    //BlockQuote--------------------------------------
    var blockQuoteHash = HashMap<Int, Int>()
    //--------------------------------------

    //References--------------------------------------
//    var startReferenceCursor = -1
//    var endReferenceCursor = -1
    var referenceStack = Stack<Int>()
    var referenceHash = HashMap<Int, Int>()
    //--------------------------------------


    var skipIteration = false
    var skipTwoIteration = false
    var skipThreeIteration = false
    var skipFourthIteration = false
    var skipFifthIteration = false
    var skipSixthIteration = false


    /**
     * BUG: в тексте "**wow** hm *no*" последняя звезда не удаляется - БАГ ИСПРАВЛЕН
     *
     **/
    suspend fun doWithTextNew(textParam: String, index: Int) = withContext(Dispatchers.Default) {
        async {
            if (textParam[index] == '*') {
                when (checkWhichStar(text, index)) {
                    Star.Empty -> {}

                    Star.OneStar -> {
                        if (starStack.isEmpty()) {
                            starStack.push(index)
                        } else {
                            starHash[starStack[0]] = index
                            starStack.pop()
                        }
//                        if (startStarCursor == -1) {
//                            startStarCursor = index
//                        } else {
//                            endStarCursor = index
//                            starHash[startStarCursor] = endStarCursor
//                            startStarCursor = -1
//                            endStarCursor = -1
//                        }
                        text = text.removeRange(index, index + 1)
                    }

                    Star.TwoStar -> {
                        if (twoStarStack.isEmpty()) {
                            twoStarStack.push(index)
                        } else {
                            twoStarHash[twoStarStack[0]] = index
                            twoStarStack.pop()
                        }
//                        if (startTwoStarCursor == -1) {
//                            startTwoStarCursor = index
//                        } else {
//                            endTwoStarCursor = index
//                            twoStarHash[startTwoStarCursor] = endTwoStarCursor
//                            startTwoStarCursor = -1
//                            endTwoStarCursor = -1
//                            skipIteration = true
//                        }
                        text = text.removeRange(index, index + 2)
                    }

                    Star.ThreeStar -> {
                        if (threeStarStack.isEmpty()) {
                            threeStarStack.push(index)
                        } else {
                            threeStarHash[threeStarStack[0]] = index
                            threeStarStack.pop()
                        }
//                        if (startThreeStarCursor == -1) {
//                            startThreeStarCursor = index
//                        } else {
//                            endThreeStarCursor = index
//                            threeStarHash[startThreeStarCursor] = endThreeStarCursor
//                            startThreeStarCursor = -1
//                            endThreeStarCursor = -1
//                            skipTwoIteration = true
//                        }
                        text = text.removeRange(index, index + 3)
                    }
                }
            } else if (textParam[index] == '~') {
                when (checkToStrikethrough(text, index)) {
                    true -> {
                        if (strikethroughStack.isEmpty()) {
                            strikethroughStack.push(index)
                        } else {
                            strikethroughHash[strikethroughStack[0]] = index
                            strikethroughStack.pop()
                        }
//                        if (startStrikethroughCursor == -1) {
//                            startStrikethroughCursor = index
//                        } else {
//                            endStrikethroughCursor = index
//                            strikethroughHash[startStrikethroughCursor] = endStrikethroughCursor
//                            startStrikethroughCursor = -1
//                            endStrikethroughCursor = -1
//                            skipIteration = true
//                        }
                        text = text.removeRange(index, index + 2)
                    }

                    else -> {}
                }
            } else if (textParam[index] == '#') {
                when (checkHeading(text, index)) {
                    Heading.FirstHeading -> {
                        text = text.removeRange(index, index + 2)
                        firstHeadingHash[index] = checkFirstNewLine(text, index + 2)
                    }

                    Heading.SecondHeading -> {
                        text = text.removeRange(index, index + 3)
                        secondHeadingHash[index] = checkFirstNewLine(text, index + 3)

                    }

                    Heading.ThreeHeading -> {
                        text = text.removeRange(index, index + 4)
                        thirdHeadingHash[index] = checkFirstNewLine(text, index + 4)

                    }

                    Heading.FourthHeading -> {
                        text = text.removeRange(index, index + 5)
                        fourthHeadingHash[index] = checkFirstNewLine(text, index + 5)
                    }

                    Heading.FifthHeading -> {
                        text = text.removeRange(index, index + 6)
                        fifthHeadingHash[index] = checkFirstNewLine(text, index + 6)
                    }

                    Heading.SixthHeading -> {
                        text = text.removeRange(index, index + 7)
                        sixthHeadingHash[index] = checkFirstNewLine(text, index + 7)
                    }
                }
            } else if (textParam[index] == '>') {
                // Не работает
                blockQuoteHash[index] = checkFirstNewLine(text, index)
            } else if (textParam[index] == '[') {
//                TODO("Доделать")
                if (referenceStack.isEmpty()) {
                    referenceStack.push(index)
                } else {
                    referenceHash[referenceStack[0]] = index
                    referenceStack.pop()
                }
//                if (startReferenceCursor == -1) {
//                    startReferenceCursor = index
//                } else {
//                    endReferenceCursor = index
//                    referenceHash[startReferenceCursor] = endReferenceCursor
//                    startReferenceCursor = -1
//                    endReferenceCursor = -1
//                }
                text = text.removeRange(index, index + 1)
            }
        }.await()
//        async {
//            setSpannableStringToTextView()
//        }
    }

    enum class Star {
        Empty, OneStar, TwoStar, ThreeStar
    }

    enum class Heading {
        FirstHeading, SecondHeading, ThreeHeading, FourthHeading, FifthHeading, SixthHeading,
    }

    suspend fun checkWhichStar(text: String, index: Int) = withContext(Dispatchers.Default) {
        if (text.getOrNull(index - 1) != null) {
            if (text[index - 1] == '\\') {
                return@withContext Star.Empty
            }
        }
        if (text.getOrNull(index + 1) != null) {
            if (text[index + 1] == '*') {
                if (text.getOrNull(index + 2) != null && text[index + 2] == '*') {
                    return@withContext Star.ThreeStar
                } else {
                    return@withContext Star.TwoStar
                }
            }
        }
        return@withContext Star.OneStar
    }

    suspend fun checkToStrikethrough(text: String, index: Int) = withContext(Dispatchers.Default) {
        if (text.getOrNull(index + 1) != null) {
            if (text[index + 1] == '~') {
                return@withContext true
            }
        }
        return@withContext false
    }

    suspend fun checkHeading(text: String, index: Int) = withContext(Dispatchers.Default) {
        when (checkHeadingIsAvailable(text, index, index + 5)) {
            6 -> {
                return@withContext Heading.SixthHeading
            }

            5 -> {
                return@withContext Heading.FifthHeading
            }

            4 -> {

                return@withContext Heading.FourthHeading
            }

            3 -> {
                return@withContext Heading.ThreeHeading
            }

            2 -> {
                return@withContext Heading.SecondHeading
            }

            1 -> {
                return@withContext Heading.FirstHeading
            }

            else -> {
                println("ELSELSELEL!")
            }
        }
    }

    fun checkHeadingIsAvailable(text: String, fromIndex: Int, toIndex: Int): Int {
        var fromI = fromIndex
        var count = 0
        while (fromI <= toIndex) {
            if (text.getOrNull(fromI) != null) {
                if (text[fromI] == '#') {
                    println("fromIndex: $fromIndex")
                    count++
                } else {
                    break
                }
            } else {
                break
            }
            fromI++
        }
        println("count: $count")
        for (i in fromIndex + count..fromIndex + count + 11) {
            println("word: ${text[i]}")
        }
        return count
    }

    fun checkFirstNewLine(text: String, index: Int): Int {
        var i = index
        var lastIndex = index
        while (i < text.length - 1) {
            if (text[i] == '\n') {
                return i
            }
            i++
        }
        return i
    }

    fun checkFirstSpace(text: String, index: Int): Int {
        var i = index
        var lastIndex = index
        while (i < text.length - 1) {
            if (text[i] == ' ') {
                return i
            }
            i++
        }
        return i
    }

}


