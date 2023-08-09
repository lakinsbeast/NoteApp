package code.with.me.testroomandnavigationdrawertest.ui.di

import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteTagViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteTagViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderTagViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderTagViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MainScreenViewModelFactory
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
    @Named("folderTagVMFactory")
    abstract fun bindFolderTagVMFactory(factory: FolderTagViewModelFactory): ViewModelProvider.Factory

    @Binds
    @Singleton
    @Named("noteTagVMFactory")
    abstract fun bindNoteTagVMFactory(factory: NoteTagViewModelFactory): ViewModelProvider.Factory

    @Binds
    @Singleton
    @Named("mainScreenVMFactory")
    abstract fun bindMainScreenVMFactory(factory: MainScreenViewModelFactory): ViewModelProvider.Factory
}

@Module
class ApplicationModule(private val application: NotesApplication) {

    @Provides
    @Singleton
    fun provideApp(): NotesApplication = application

    @Provides
    fun provideNoteViewModel(
        repo: NoteRepositoryImpl
    ) = NoteViewModel(repo)

    @Provides
    fun provideNoteTagViewModel(
        repo: NoteTagRepositoryImpl
    ) = NoteTagViewModel(repo)

    @Provides
    fun provideFolderViewModel(
        repo: FolderRepositoryImpl
    ) = FolderViewModel(repo)

    @Provides
    fun provideFolderTagViewModel(
        repo: FolderTagRepositoryImpl
    ) = FolderTagViewModel(repo)

}
