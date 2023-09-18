package code.with.me.testroomandnavigationdrawertest.markdown

interface ITextChecker {
    fun checkWhichStar(text: String, index: Int): Star
    fun checkToStrikethrough(text: String, index: Int): Boolean
    fun checkHeading(text: String, index: Int): Heading
    fun checkHeadingIsAvailable(text: String, fromIndex: Int, toIndex: Int): Int
    fun checkFirstNewLine(text: String, index: Int): Int
    fun checkFirstSpace(text: String, index: Int): Int

}