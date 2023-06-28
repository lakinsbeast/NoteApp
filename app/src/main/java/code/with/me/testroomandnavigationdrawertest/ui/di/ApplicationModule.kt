package code.with.me.testroomandnavigationdrawertest.ui.di

import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderFolderTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.ui.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.FolderViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.NoteViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.TagViewModel
import code.with.me.testroomandnavigationdrawertest.ui.TagViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton


@Module
abstract class BindAppModule {
    @Binds
    @Singleton
    @Named("noteVMFactory")
    abstract fun bindNoteVMFactory(factory: NoteViewModelFactory): ViewModelProvider.Factory

    @Binds
    @Singleton
    @Named("folderVMFactory")
    abstract fun bindFolderVMFactory(factory: FolderViewModelFactory): ViewModelProvider.Factory

    @Binds
    @Singleton
    @Named("tagVMFactory")
    abstract fun bindTagVMFactory(factory: TagViewModelFactory): ViewModelProvider.Factory
}

@Module
class ApplicationModule(private val application: NotesApplication) {

    @Provides
    @Singleton
    fun provideApp(): NotesApplication = application

    @Provides
    fun provideNoteViewModel(
//        deleteNoteUseCase: deleteNoteUseCase,
//        getListOfNotesUseCase: getListOfNotesUseCase,
//        insertNoteUseCase: insertNoteUseCase,
//        updateNoteUseCase: updateNoteUseCase
        repo: NoteRepositoryImpl
    ) = NoteViewModel(repo)

    @Provides
    fun provideFolderViewModel(
        repo: FolderRepositoryImpl
    ) = FolderViewModel(repo)

    @Provides
    fun provideTagViewModel(
        repo: FolderFolderTagRepositoryImpl
    ) = TagViewModel(repo)

}
