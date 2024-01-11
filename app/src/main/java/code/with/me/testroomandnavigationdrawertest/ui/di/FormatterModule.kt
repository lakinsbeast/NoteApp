package code.with.me.testroomandnavigationdrawertest.ui.di

import code.with.me.testroomandnavigationdrawertest.markdown.BlockQuoteFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.BlockQuoteFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.Formatter
import code.with.me.testroomandnavigationdrawertest.markdown.Heading
import code.with.me.testroomandnavigationdrawertest.markdown.HeadingFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.HeadingFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.ITextCheckerT
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceBracketFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceBracketFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceSquareFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceSquareFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.Star
import code.with.me.testroomandnavigationdrawertest.markdown.StarFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.StarFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StrikethroughFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.StrikethroughFormatterImpl
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class FormatterModule {
    @Provides
    fun provideFormatters(
        starTextChecker: ITextCheckerT<Star>,
        strikethroughTextChecker: ITextCheckerT<Boolean>,
        headingTextChecker: ITextCheckerT<Heading>,
        @Named("FirstNewLineTextChecker") firstNewLineTextChecker: ITextCheckerT<Int>,
        @Named("FirstSpaceTextChecker") firstSpaceTextChecker: ITextCheckerT<Int>,
    ): Array<Formatter> {
        return arrayOf(
            provideStarFormatter(starTextChecker),
            provideStrikethroughFormatter(strikethroughTextChecker),
            provideHeadingFormatter(headingTextChecker, firstNewLineTextChecker),
            provideBlockQuoteFormatter(firstNewLineTextChecker),
            provideReferenceSquareFormatter(firstSpaceTextChecker),
            provideReferenceBracketFormatter(),
        )
    }

    @Provides
    fun provideStarFormatter(starTextChecker: ITextCheckerT<Star>): StarFormatter {
        return StarFormatterImpl(starTextChecker)
    }

    @Provides
    fun provideStrikethroughFormatter(strikethroughTextChecker: ITextCheckerT<Boolean>): StrikethroughFormatter {
        return StrikethroughFormatterImpl(strikethroughTextChecker)
    }

    @Provides
    fun provideHeadingFormatter(
        headingTextChecker: ITextCheckerT<Heading>,
        firstNewLineTextChecker: ITextCheckerT<Int>,
    ): HeadingFormatter {
        return HeadingFormatterImpl(headingTextChecker, firstNewLineTextChecker)
    }

    @Provides
    fun provideBlockQuoteFormatter(firstNewLineTextChecker: ITextCheckerT<Int>): BlockQuoteFormatter {
        return BlockQuoteFormatterImpl(firstNewLineTextChecker)
    }

    @Provides
    fun provideReferenceSquareFormatter(firstSpaceTextChecker: ITextCheckerT<Int>): ReferenceSquareFormatter {
        return ReferenceSquareFormatterImpl(firstSpaceTextChecker)
    }

    @Provides
    fun provideReferenceBracketFormatter(): ReferenceBracketFormatter {
        return ReferenceBracketFormatterImpl()
    }
}
