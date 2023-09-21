package code.with.me.testroomandnavigationdrawertest.markdown

//interface ITextChecker {
//    fun checkWhichStar(text: String, index: Int): Star
//    fun checkToStrikethrough(text: String, index: Int): Boolean
//    fun checkHeading(text: String, index: Int): Heading
//    fun checkHeadingIsAvailable(text: String, fromIndex: Int, toIndex: Int): Int
//    fun checkFirstNewLine(text: String, index: Int): Int
//    fun checkFirstSpace(text: String, index: Int): Int
//
//}

interface ITextCheckerT<T> {
    fun checkText(text: String, index: Int): T
}

class StarTextCheckerImpl : ITextCheckerT<Star> {
    override fun checkText(text: String, index: Int): Star {
        if (text.getOrNull(index - 1) != null) {
            if (text[index - 1] == '\\') {
                return Star.Empty
            }
        }
        if (text.getOrNull(index + 1) != null) {
            if (text[index + 1] == '*') {
                if (text.getOrNull(index + 2) != null && text[index + 2] == '*') {
                    return Star.ThreeStar
                } else {
                    return Star.TwoStar
                }
            }
        }
        return Star.OneStar
    }
}

class StrikethroughTextCheckerImpl : ITextCheckerT<Boolean> {
    override fun checkText(text: String, index: Int): Boolean {
        if (text.getOrNull(index + 1) != null) {
            if (text[index + 1] == '~') {
                return true
            }
        }
        return false
    }
}

interface IHeadingChecker : ITextCheckerT<Heading> {
    override fun checkText(text: String, index: Int): Heading
    fun checkHeadingIsAvailable(text: String, fromIndex: Int, toIndex: Int): Int
}

class HeadingTextCheckerImpl : IHeadingChecker {
    override fun checkText(text: String, index: Int): Heading {
        when (checkHeadingIsAvailable(text, index, index + 5)) {
            6 -> {
                return Heading.SixthHeading
            }

            5 -> {
                return Heading.FifthHeading
            }

            4 -> {

                return Heading.FourthHeading
            }

            3 -> {
                return Heading.ThreeHeading
            }

            2 -> {
                return Heading.SecondHeading
            }

            1 -> {
                return Heading.FirstHeading
            }

            else -> {
                return Heading.FirstHeading
            }
        }
    }

    override fun checkHeadingIsAvailable(text: String, fromIndex: Int, toIndex: Int): Int {
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
        return count
    }
}

class FirstNewLineTextCheckerImpl : ITextCheckerT<Int> {
    override fun checkText(text: String, index: Int): Int {
        var i = index
        while (i < text.length - 1) {
            if (text[i] == '\n') {
                return i
            }
            i++
        }
        return i
    }
}

class FirstSpaceTextCheckerImpl : ITextCheckerT<Int> {
    override fun checkText(text: String, index: Int): Int {
        var i = index
        while (i < text.length - 1) {
            if (text[i] == ' ') {
                return i
            }
            i++
        }
        return i
    }
}

