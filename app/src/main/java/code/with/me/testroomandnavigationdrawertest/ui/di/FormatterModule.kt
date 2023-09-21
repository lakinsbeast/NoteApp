package code.with.me.testroomandnavigationdrawertest.ui.di

import code.with.me.testroomandnavigationdrawertest.markdown.BlockQuoteFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.BlockQuoteFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.FirstNewLineTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.FirstSpaceTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.Formatter
import code.with.me.testroomandnavigationdrawertest.markdown.HeadingFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.HeadingFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.HeadingTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceBracketFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceBracketFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceSquareFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.ReferenceSquareFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StarFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.StarFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StarTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StrikethroughFormatter
import code.with.me.testroomandnavigationdrawertest.markdown.StrikethroughFormatterImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StrikethroughTextCheckerImpl
import dagger.Module
import dagger.Provides

@Module
class FormatterModule {

    @Provides
    fun provideFormatters(): Array<Formatter> {
        return arrayOf(
            provideStarFormatter(),
            provideStrikethroughFormatter(),
            provideHeadingFormatter(),
            provideBlockQuoteFormatter(),
            provideReferenceSquareFormatter(),
            provideReferenceBracketFormatter()
        )
    }


    @Provides
    fun provideStarFormatter(): StarFormatter {
        return StarFormatterImpl(StarTextCheckerImpl())
    }

    @Provides
    fun provideStrikethroughFormatter(): StrikethroughFormatter {
        return StrikethroughFormatterImpl(StrikethroughTextCheckerImpl())
    }

    @Provides
    fun provideHeadingFormatter(): HeadingFormatter {
        return HeadingFormatterImpl(HeadingTextCheckerImpl(), FirstNewLineTextCheckerImpl())
    }

    @Provides
    fun provideBlockQuoteFormatter(): BlockQuoteFormatter {
        return BlockQuoteFormatterImpl(FirstNewLineTextCheckerImpl())
    }

    @Provides
    fun provideReferenceSquareFormatter(): ReferenceSquareFormatter {
        return ReferenceSquareFormatterImpl(FirstSpaceTextCheckerImpl())
    }

    @Provides
    fun provideReferenceBracketFormatter(): ReferenceBracketFormatter {
        return ReferenceBracketFormatterImpl()
    }
}