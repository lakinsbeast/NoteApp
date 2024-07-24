package code.with.me.testroomandnavigationdrawertest.ui.di

import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.markdown.Formatter
import code.with.me.testroomandnavigationdrawertest.markdown.IStringToMarkdownTextParser
import code.with.me.testroomandnavigationdrawertest.markdown.StringToMarkdownTextParser
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MakeNoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteMenuSheetViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SearchViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SettingsViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.ViewANoteViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
class ViewModelModule {
    @Provides
    fun provideNoteViewModel(repo: NoteRepositoryImpl) = NoteViewModel(repo)

    @Provides
    fun provideSearchViewModel(repo: NoteRepositoryImpl) = SearchViewModel(repo)

    @Provides
    fun provideMakeNoteViewModel(repo: NoteRepositoryImpl) = MakeNoteViewModel(repo)

    @Provides
    fun provideNoteMenuSheetViewModel(repo: NoteRepositoryImpl) = NoteMenuSheetViewModel(repo)

    @Provides
    fun provideViewANoteViewModel(
        repo: NoteRepositoryImpl,
        audioController: AudioController,
    ) = ViewANoteViewModel(repo, audioController)

    @Provides
    fun provideFolderViewModel(
        repo: FolderRepositoryImpl,
//        dataStore: DataStoreManager,
    ) = FolderViewModel(repo /*dataStore*/)

    @Provides
    fun provideSettingsViewModel(/*dataStore: DataStoreManager*/) = SettingsViewModel(/*dataStore*/)

    @Provides
    fun provideStringToMarkdownTextParser(vararg formatters: Formatter): IStringToMarkdownTextParser {
        return StringToMarkdownTextParser(*formatters)
    }
}
