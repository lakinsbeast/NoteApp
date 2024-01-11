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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.util.Stack
import javax.inject.Inject

interface Formatter

interface StarFormatter : Formatter {
    // STAR--------------------------------------
    var starStack: Stack<Int>
    var starHash: HashMap<Int, Int>

    var twoStarStack: Stack<Int>
    var twoStarHash: HashMap<Int, Int>

    var threeStarStack: Stack<Int>
    var threeStarHash: HashMap<Int, Int>

    // --------------------------------------

    fun handleStarFormatting(
        text: String,
        index: Int,
    ): Star
}

class StarFormatterImpl(private val textChecker: ITextCheckerT<Star>) : StarFormatter {
    override var starStack = Stack<Int>()
    override var starHash = HashMap<Int, Int>()

    override var twoStarStack = Stack<Int>()
    override var twoStarHash = HashMap<Int, Int>()

    override var threeStarStack = Stack<Int>()
    override var threeStarHash = HashMap<Int, Int>()

    override fun handleStarFormatting(
        text: String,
        index: Int,
    ): Star {
        when (textChecker.checkText(text, index)) {
            Star.Empty -> {
                return Star.Empty
            }

            Star.OneStar -> {
                if (starStack.isEmpty()) {
                    starStack.push(index)
                } else {
                    starHash[starStack[0]] = index
                    starStack.pop()
                }
                return Star.OneStar
            }

            Star.TwoStar -> {
                if (twoStarStack.isEmpty()) {
                    println("twoStarStack push index: $index")
                    twoStarStack.push(index)
                } else {
                    println("twoStarStack pop index=$index twoStarStack[0]=${twoStarStack[0]} ")
                    twoStarHash[twoStarStack[0]] = index
                    twoStarStack.pop()
                }
                return Star.TwoStar
            }

            Star.ThreeStar -> {
                if (threeStarStack.isEmpty()) {
                    threeStarStack.push(index)
                } else {
                    threeStarHash[threeStarStack[0]] = index
                    threeStarStack.pop()
                }
                return Star.ThreeStar
            }
        }
    }
}

interface StrikethroughFormatter : Formatter {
    // STRIKETHROUGH--------------------------------------
    var strikethroughStack: Stack<Int>
    var strikethroughHash: HashMap<Int, Int>

    // --------------------------------------

    fun handleStrikethroughFormatter(
        text: String,
        index: Int,
    ): Boolean
}

class StrikethroughFormatterImpl(private val textChecker: ITextCheckerT<Boolean>) :
    StrikethroughFormatter {
    override var strikethroughStack = Stack<Int>()
    override var strikethroughHash = HashMap<Int, Int>()

    override fun handleStrikethroughFormatter(
        text: String,
        index: Int,
    ): Boolean {
        return when (textChecker.checkText(text, index)) {
            true -> {
                if (strikethroughStack.isEmpty()) {
                    strikethroughStack.push(index)
                } else {
                    strikethroughHash[strikethroughStack[0]] = index
                    strikethroughStack.pop()
                }
                true
            }

            else -> {
                false
            }
        }
    }
}

interface HeadingFormatter : Formatter {
    // HEADING--------------------------------------
    var firstHeadingHash: HashMap<Int, Int>
    var secondHeadingHash: HashMap<Int, Int>
    var thirdHeadingHash: HashMap<Int, Int>
    var fourthHeadingHash: HashMap<Int, Int>
    var fifthHeadingHash: HashMap<Int, Int>
    var sixthHeadingHash: HashMap<Int, Int>

    // --------------------------------------
    var firstNewLineTextChecker: ITextCheckerT<Int>

    fun handleHeadingFormatter(
        text: String,
        index: Int,
    ): Heading
}

class HeadingFormatterImpl(
    private val textChecker: ITextCheckerT<Heading>,
    firstNewLineTC: ITextCheckerT<Int>,
) : HeadingFormatter {
    override var firstNewLineTextChecker: ITextCheckerT<Int> = firstNewLineTC

    override var firstHeadingHash = HashMap<Int, Int>()
    override var secondHeadingHash = HashMap<Int, Int>()
    override var thirdHeadingHash = HashMap<Int, Int>()
    override var fourthHeadingHash = HashMap<Int, Int>()
    override var fifthHeadingHash = HashMap<Int, Int>()
    override var sixthHeadingHash = HashMap<Int, Int>()

    override fun handleHeadingFormatter(
        text: String,
        index: Int,
    ): Heading {
        when (textChecker.checkText(text, index)) {
            Heading.FirstHeading -> {
                return Heading.FirstHeading
            }

            Heading.SecondHeading -> {
                return Heading.SecondHeading
            }

            Heading.ThreeHeading -> {
                return Heading.ThreeHeading
            }

            Heading.FourthHeading -> {
                return Heading.FourthHeading
            }

            Heading.FifthHeading -> {
                return Heading.FifthHeading
            }

            Heading.SixthHeading -> {
                return Heading.SixthHeading
            }
        }
    }
}

interface BlockQuoteFormatter : Formatter {
    // BlockQuote--------------------------------------
    var blockQuoteHash: HashMap<Int, Int>
    var firstNewLineTextChecker: ITextCheckerT<Int>

    fun handleBlockQuote(
        text: String,
        index: Int,
    )
}

class BlockQuoteFormatterImpl(override var firstNewLineTextChecker: ITextCheckerT<Int>) :
    BlockQuoteFormatter {
    override var blockQuoteHash = HashMap<Int, Int>()

    override fun handleBlockQuote(
        text: String,
        index: Int,
    ) {
        blockQuoteHash[index] = firstNewLineTextChecker.checkText(text, index)
    }
}

interface ReferenceFormatter : Formatter {
    fun handleReference(
        text: String,
        index: Int,
    )
}

interface ReferenceSquareFormatter : ReferenceFormatter {
    var referenceSquareStack: Stack<Int>
    var referenceSquareHash: HashMap<Int, Int>
    var firstSpaceTextCheckerImpl: ITextCheckerT<Int>
}

interface ReferenceBracketFormatter : ReferenceFormatter {
    var referenceBracketStack: Stack<Int>
    var referenceBracketHash: HashMap<Int, Int>
}

class ReferenceSquareFormatterImpl(override var firstSpaceTextCheckerImpl: ITextCheckerT<Int>) :
    ReferenceSquareFormatter {
    override var referenceSquareStack = Stack<Int>()
    override var referenceSquareHash = HashMap<Int, Int>()

    override fun handleReference(
        text: String,
        index: Int,
    ) {
        println("ReferenceSquareFormatterImpl handleReference index:$index")
        if (referenceSquareStack.isEmpty()) {
            referenceSquareStack.push(index)
            println("pushed")
        } else {
            referenceSquareHash[referenceSquareStack[0]] = index
            referenceSquareStack.pop()
            println("poped")
        }
    }
}

class ReferenceBracketFormatterImpl : ReferenceBracketFormatter {
    override var referenceBracketStack = Stack<Int>()
    override var referenceBracketHash = HashMap<Int, Int>()

    override fun handleReference(
        text: String,
        index: Int,
    ) {
        if (referenceBracketStack.isEmpty()) {
            referenceBracketStack.push(index)
        } else {
            referenceBracketHash[referenceBracketStack[0]] = index
            referenceBracketStack.pop()
        }
    }
}

abstract class IStringToMarkdownTextParser(
    vararg formatter: Formatter,
) : IMarkdownTextParser {
    var text: String = ""
    var index1: Int = 0

    init {
        formatter.forEach {
            when (it) {
                is StarFormatter -> starFormatter = it
                is StrikethroughFormatter -> strikethroughFormatter = it
                is HeadingFormatter -> headingFormatter = it
                is BlockQuoteFormatter -> blockquoteFormatter = it
                is ReferenceBracketFormatter -> referenceBracketFormatter = it
                is ReferenceSquareFormatter -> referenceSquareFormatter = it
            }
        }
    }

    lateinit var starFormatter: StarFormatter
    lateinit var strikethroughFormatter: StrikethroughFormatter
    lateinit var headingFormatter: HeadingFormatter
    lateinit var blockquoteFormatter: BlockQuoteFormatter
    lateinit var referenceBracketFormatter: ReferenceBracketFormatter
    lateinit var referenceSquareFormatter: ReferenceSquareFormatter

    var skipIteration = false
    var skipTwoIteration = false
    var skipThreeIteration = false
    var skipFourthIteration = false
    var skipFifthIteration = false
    var skipSixthIteration = false

    override suspend fun getParsedText(text: String): SpannableString =
        withContext(Dispatchers.Default) {
            this@IStringToMarkdownTextParser.text = text

            while (index1 < this@IStringToMarkdownTextParser.text.length) {
                /**
                 * change to :
                 * val skipIterations = arrayOf(skipIteration, skipTwoIteration, skipThreeIteration, skipFourthIteration, skipFifthIteration, skipSixthIteration)
                 *
                 * for (i in 0 until skipIterations.size) {
                 *     if (skipIterations[i]) {
                 *         index1 += i + 1
                 *         skipIterations[i] = false
                 *         continue
                 *     }
                 * }
                 *
                 **/

                /**
                 * change to :
                 * val skipIterations = arrayOf(skipIteration, skipTwoIteration, skipThreeIteration, skipFourthIteration, skipFifthIteration, skipSixthIteration)
                 *
                 * for (i in 0 until skipIterations.size) {
                 *     if (skipIterations[i]) {
                 *         index1 += i + 1
                 *         skipIterations[i] = false
                 *         continue
                 *     }
                 * }
                 *
                 **/
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
                async {
                    doWithTextNew(this@IStringToMarkdownTextParser.text, index1)
                }.await()
                index1++
            }
            return@withContext parseText(this@IStringToMarkdownTextParser.text)
        }

    override suspend fun parseText(text: String): SpannableString {
        val spannableString =
            SpannableString(text)
        val starHash = starFormatter.starHash
        val twoStarHash = starFormatter.twoStarHash
        val threeStarHash = starFormatter.threeStarHash
        val strikethroughHash = strikethroughFormatter.strikethroughHash
        val firstHeadingHash = headingFormatter.firstHeadingHash
        val secondHeadingHash = headingFormatter.secondHeadingHash
        val thirdHeadingHash = headingFormatter.thirdHeadingHash
        val fourthHeadingHash = headingFormatter.fourthHeadingHash
        val fifthHeadingHash = headingFormatter.fifthHeadingHash
        val sixthHeadingHash = headingFormatter.sixthHeadingHash
        val blockQuoteHash = blockquoteFormatter.blockQuoteHash
        val referenceBracketHash = referenceBracketFormatter.referenceBracketHash
        val referenceSquareHash = referenceSquareFormatter.referenceSquareHash

        if (starHash.isNotEmpty()) {
            starHash.forEach { (key, value) ->
                spannableString.setSpan(
                    StyleSpan(Typeface.ITALIC),
                    key,
                    value,
                    0,
                )
            }
        }
        if (twoStarHash.isNotEmpty()) {
            twoStarHash.forEach { (key, value) ->
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD),
                    key,
                    value,
                    0,
                )
            }
        }
        if (threeStarHash.isNotEmpty()) {
            threeStarHash.forEach { (key, value) ->
                spannableString.setSpan(
                    StyleSpan(Typeface.BOLD_ITALIC),
                    key,
                    value,
                    0,
                )
            }
        }
        if (strikethroughHash.isNotEmpty()) {
            strikethroughHash.forEach { (key, value) ->
                spannableString.setSpan(
                    StrikethroughSpan(),
                    key,
                    value,
                    0,
                )
            }
        }
        if (firstHeadingHash.isNotEmpty()) {
            firstHeadingHash.forEach { (key, value) ->
                println("key: $key value: $value")
                spannableString.setSpan(
                    AbsoluteSizeSpan(35, true),
                    key,
                    value,
                    0,
                )
            }
        }
        if (secondHeadingHash.isNotEmpty()) {
            secondHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(30, true),
                    key,
                    value,
                    0,
                )
            }
        }
        if (thirdHeadingHash.isNotEmpty()) {
            thirdHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(25, true),
                    key,
                    value,
                    0,
                )
            }
        }
        if (fourthHeadingHash.isNotEmpty()) {
            fourthHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(20, true),
                    key,
                    value,
                    0,
                )
            }
        }
        if (fifthHeadingHash.isNotEmpty()) {
            fifthHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(15, true),
                    key,
                    value,
                    0,
                )
            }
        }
        if (sixthHeadingHash.isNotEmpty()) {
            sixthHeadingHash.forEach { (key, value) ->
                spannableString.setSpan(
                    AbsoluteSizeSpan(10, true),
                    key,
                    value,
                    0,
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
                    0,
                )
            }
        }

        val refs = mutableListOf<String>()
        var word = ""
        var i = 0
        if (referenceSquareHash.isNotEmpty() && referenceBracketHash.isNotEmpty()) {
            referenceBracketHash.forEach { (key, value) ->
                println("bracket: k:$key v:$value")
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
                println("square: k:$key v:$value")
                spannableString.setSpan(
                    object : ClickableSpan() {
                        override fun onClick(widget: View) {
                            println("FJLKSAJFLKSJAFJLKSAJ")
                        }
                    },
                    key,
                    referenceSquareFormatter.firstSpaceTextCheckerImpl.checkText(text, value),
                    0,
                )
                i++
            }
        }
        return spannableString
    }

    open suspend fun doWithTextNew(
        textParam: String,
        index: Int,
    ) {
        withContext(Dispatchers.Default) {
            async {
                println("index before:$index")
                if (textParam[index] == '*') {
                    println("index:$index")
                    when (
                        starFormatter.handleStarFormatting(
                            text,
                            index,
                        )
                    ) {
                        Star.Empty -> {}
                        Star.OneStar -> {
                            index1--
                            text = text.removeRange(index, index + 1)
                        }

                        Star.TwoStar -> {
                            index1++
                            text = text.removeRange(index, index + 2)
                        }

                        Star.ThreeStar -> {
                            index1 -= 2
                            text = text.removeRange(index, index + 3)
                        }
                    }
                } else if (textParam[index] == '~') {
                    when (
                        strikethroughFormatter.handleStrikethroughFormatter(
                            text,
                            index,
                        )
                    ) {
                        true -> {
                            index1--
                            text = text.removeRange(index, index + 2)
                        }

                        else -> {
                        }
                    }
                } else if (textParam[index] == '#') {
                    headingFormatter.apply {
                        when (
                            headingFormatter.handleHeadingFormatter(
                                text,
                                index,
                            )
                        ) {
                            Heading.FirstHeading -> {
                                text = text.removeRange(index, index + 1)
                                firstHeadingHash[index] =
                                    firstNewLineTextChecker.checkText(text, index + 1)
                            }

                            Heading.SecondHeading -> {
                                index1 -= 1
                                text = text.removeRange(index, index + 2)
                                secondHeadingHash[index] =
                                    firstNewLineTextChecker.checkText(text, index + 2)
                            }

                            Heading.ThreeHeading -> {
                                index1 -= 2
                                text = text.removeRange(index, index + 3)
                                thirdHeadingHash[index] =
                                    firstNewLineTextChecker.checkText(text, index + 3)
                            }

                            Heading.FourthHeading -> {
                                index1 -= 3
                                text = text.removeRange(index, index + 4)
                                fourthHeadingHash[index] =
                                    firstNewLineTextChecker.checkText(text, index + 4)
                            }

                            Heading.FifthHeading -> {
                                index1 -= 4
                                text = text.removeRange(index, index + 5)
                                fifthHeadingHash[index] =
                                    firstNewLineTextChecker.checkText(text, index + 5)
                            }

                            Heading.SixthHeading -> {
                                index1 -= 5
                                text = text.removeRange(index, index + 6)
                                sixthHeadingHash[index] =
                                    firstNewLineTextChecker.checkText(
                                        text,
                                        index + 6,
                                    )
                                println("after text: $text")
                            }
                        }
                    }
                } else if (textParam[index] == '>') {
                    // Не работает
                    blockquoteFormatter.handleBlockQuote(text, index)
                } else if (textParam[index] == '[' || textParam[index] == ']') {
                    println("[ or ] founded index: $index text: ${text.substring(0, index)}")
                    referenceBracketFormatter.handleReference(text, index)
                    index1 -= 1
                    text = text.removeRange(index, index + 1)
                } else if (textParam[index] == '(' || textParam[index] == ')') {
                    println("( or ) founded index: $index text: ${text.substring(0, index)}")
                    referenceSquareFormatter.handleReference(text, index)
                    index1 -= 1
                    text = text.removeRange(index, index + 1)
                } else {
                }
            }.await()
        }
    }
}

class StringToMarkdownTextParser
    @Inject
    constructor(
        vararg formatter: Formatter,
    ) : IStringToMarkdownTextParser(*formatter)
