package code.with.me.testroomandnavigationdrawertest.markdown

import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.text.SpannableString
import android.text.style.AbsoluteSizeSpan
import android.text.style.ClickableSpan
import android.text.style.QuoteSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.view.View
import code.with.me.testroomandnavigationdrawertest.Utils.mainScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.Stack

class StringToMarkdownTextParser() : IMarkdownTextParser {

    var text = ""

    var index1 = 0

    var skipIteration = false
    var skipTwoIteration = false
    var skipThreeIteration = false
    var skipFourthIteration = false
    var skipFifthIteration = false
    var skipSixthIteration = false


    //STAR--------------------------------------
    var starStack = Stack<Int>()
    var starHash = HashMap<Int, Int>()

    var twoStarStack = Stack<Int>()
    var twoStarHash = HashMap<Int, Int>()

    var threeStarStack = Stack<Int>()
    var threeStarHash = HashMap<Int, Int>()

    //--------------------------------------
//STRIKETHROUGH--------------------------------------
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
    var referenceSquareStack = Stack<Int>()
    var referenceSquareHash = HashMap<Int, Int>()

    var referenceBracketStack = Stack<Int>()
    var referenceBracketHash = HashMap<Int, Int>()
//--------------------------------------

    val textChecker = TextCheckerImpl()

    override suspend fun parseText(text: String): SpannableString {
        this.text = text

        while (index1 < this.text.length) {
            when {
                skipIteration -> {
                    index1++
                    skipIteration = false
                    continue
                }

                skipTwoIteration -> {
                    index1 += 2
                    skipTwoIteration = false
                    continue
                }

                skipThreeIteration -> {
                    index1 += 3
                    skipThreeIteration = false
                    continue
                }

                skipFourthIteration -> {
                    index1 += 4
                    skipFourthIteration = false
                    continue
                }

                skipFifthIteration -> {
                    index1 += 5
                    skipFifthIteration = false
                    continue
                }

                skipSixthIteration -> {
                    index1 += 6
                    skipSixthIteration = false
                    continue
                }
            }
            doWithTextNew(this.text, index1)
            index1++
        }
        return setTextSpannable(this.text)
    }

    override suspend fun setTextSpannable(text: String): SpannableString {
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        QuoteSpan(Color.GREEN, 20, 20)
                    } else {
                        QuoteSpan(Color.GREEN)
                    },
                    key,
                    value,
                    0
                )
            }


        }

        val refs = mutableListOf<String>()
        var word = ""
        var i = 0
        if (referenceSquareHash.isNotEmpty() && referenceBracketHash.isNotEmpty()) {
            referenceBracketHash.forEach { (key, value) ->
                i = key
                while (i < value) {
                    word += text[i]
                    i++
                }
                refs.add(word)
                word = ""
            }
            i = 0
            referenceSquareHash.forEach { (key, value) ->
                spannableString.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            println("FJLKSAJFLKSJAFJLKSAJ")
                        }

                    },
                    key,
                    textChecker.checkFirstSpace(text, value),
                    0
                )
                i++
            }
        }
        return spannableString
    }

    suspend fun doWithTextNew(textParam: String, index: Int) = withContext(Dispatchers.Default) {
        async {
            if (textParam[index] == '*') {
                when (textChecker.checkWhichStar(text, index)) {
                    Star.Empty -> {}
                    Star.OneStar -> {
                        if (starStack.isEmpty()) {
                            starStack.push(index)
                        } else {
                            starHash[starStack[0]] = index
                            starStack.pop()
                        }
                        index1--
                        text = text.removeRange(index, index + 1)
                    }

                    Star.TwoStar -> {
                        if (twoStarStack.isEmpty()) {
                            twoStarStack.push(index)
                        } else {
                            twoStarHash[twoStarStack[0]] = index
                            twoStarStack.pop()
                        }
                        index1 -= 2
                        text = text.removeRange(index, index + 2)
                    }

                    Star.ThreeStar -> {
                        if (threeStarStack.isEmpty()) {
                            threeStarStack.push(index)
                        } else {
                            threeStarHash[threeStarStack[0]] = index
                            threeStarStack.pop()
                        }
                        index1 -= 3
                        text = text.removeRange(index, index + 3)
                    }
                }
            } else if (textParam[index] == '~') {
                when (textChecker.checkToStrikethrough(text, index)) {
                    true -> {
                        if (strikethroughStack.isEmpty()) {
                            strikethroughStack.push(index)
                        } else {
                            strikethroughHash[strikethroughStack[0]] = index
                            strikethroughStack.pop()
                        }
                        index1--
                        text = text.removeRange(index, index + 2)
                    }

                    else -> {}
                }
            } else if (textParam[index] == '#') {
                when (textChecker.checkHeading(text, index)) {
                    Heading.FirstHeading -> {
                        index1 -= 2
                        text = text.removeRange(index, index + 2)
                        firstHeadingHash[index] = textChecker.checkFirstNewLine(text, index + 2)
                    }

                    Heading.SecondHeading -> {
                        index1 -= 3
                        text = text.removeRange(index, index + 3)
                        secondHeadingHash[index] = textChecker.checkFirstNewLine(text, index + 3)

                    }

                    Heading.ThreeHeading -> {
                        index1 -= 4
                        text = text.removeRange(index, index + 4)
                        thirdHeadingHash[index] = textChecker.checkFirstNewLine(text, index + 4)

                    }

                    Heading.FourthHeading -> {
                        index1 -= 5
                        text = text.removeRange(index, index + 5)
                        fourthHeadingHash[index] = textChecker.checkFirstNewLine(text, index + 5)
                    }

                    Heading.FifthHeading -> {
                        index1 -= 6
                        text = text.removeRange(index, index + 6)
                        fifthHeadingHash[index] = textChecker.checkFirstNewLine(text, index + 6)
                    }

                    Heading.SixthHeading -> {
                        index1 -= 7
                        text = text.removeRange(index, index + 7)
                        sixthHeadingHash[index] = textChecker.checkFirstNewLine(text, index + 7)
                    }
                }
            } else if (textParam[index] == '>') {
                // Не работает
                blockQuoteHash[index] = textChecker.checkFirstNewLine(text, index)
            } else if (textParam[index] == '[' || textParam[index] == ']') {
                println("[ or ] founded index: $index text: ${text.substring(0, index)}")
                if (referenceSquareStack.isEmpty()) {
                    referenceSquareStack.push(index)
                } else {
                    referenceSquareHash[referenceSquareStack[0]] = index
                    referenceSquareStack.pop()
                }
                index1 -= 1
                text = text.removeRange(index, index + 1)
            } else if (textParam[index] == '(' || textParam[index] == ')') {
                println("( or ) founded index: $index text: ${text.substring(0, index)}")
                if (referenceBracketStack.isEmpty()) {
                    referenceBracketStack.push(index)
                } else {
                    referenceBracketHash[referenceBracketStack[0]] = index
                    referenceBracketStack.pop()
                }
                index1 -= 1
                text = text.removeRange(index, index + 1)
            }
        }.await()
    }
}