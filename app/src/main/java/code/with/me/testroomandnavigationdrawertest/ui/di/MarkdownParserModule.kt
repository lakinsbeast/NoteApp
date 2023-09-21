package code.with.me.testroomandnavigationdrawertest.ui.di

import code.with.me.testroomandnavigationdrawertest.markdown.FirstNewLineTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.FirstSpaceTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.Heading
import code.with.me.testroomandnavigationdrawertest.markdown.HeadingTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.ITextCheckerT
import code.with.me.testroomandnavigationdrawertest.markdown.Star
import code.with.me.testroomandnavigationdrawertest.markdown.StarTextCheckerImpl
import code.with.me.testroomandnavigationdrawertest.markdown.StrikethroughTextCheckerImpl
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
class MarkdownParserModule {
    @Provides
    fun provideStarTextChecker(): ITextCheckerT<Star> {
        return StarTextCheckerImpl()
    }

    @Provides
    fun provideStrikethroughTextChecker(): ITextCheckerT<Boolean> {
        return StrikethroughTextCheckerImpl()
    }

    @Provides
    fun provideHeadingTextChecker(): ITextCheckerT<Heading> {
        return HeadingTextCheckerImpl()
    }

    @Provides
    @Named("FirstNewLineTextChecker")
    fun provideFirstNewLineTextChecker(): ITextCheckerT<Int> {
        return FirstNewLineTextCheckerImpl()
    }

    @Provides
    @Named("FirstSpaceTextChecker")
    fun provideFirstSpaceTextChecker(): ITextCheckerT<Int> {
        return FirstSpaceTextCheckerImpl()
    }

}