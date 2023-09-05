package code.with.me.testroomandnavigationdrawertest.ui.di

import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteTagRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.file.FilesController
import code.with.me.testroomandnavigationdrawertest.ui.FragmentBackStackManager
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.SheetController
import code.with.me.testroomandnavigationdrawertest.ui.controllers.CloseFragmentImpl
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentController
import code.with.me.testroomandnavigationdrawertest.ui.controllers.GetFragmentImpl
import code.with.me.testroomandnavigationdrawertest.ui.controllers.ICloseFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.IGetFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.IOpenFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.IReplaceFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.OpenFragmentImpl
import code.with.me.testroomandnavigationdrawertest.ui.controllers.ReplaceFragmentImpl
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

@Module
class ControllerModule {

    @Provides
    fun provideGetFragmentImpl(): IGetFragment = GetFragmentImpl()

    @Provides
    fun provideOpenFragmentImpl(): IOpenFragment = OpenFragmentImpl()

    @Provides
    fun provideReplaceFragmentImpl(): IReplaceFragment = ReplaceFragmentImpl()

    @Provides
    fun provideCloseFragmentImpl(getFragment: IGetFragment): ICloseFragment =
        CloseFragmentImpl(getFragment)

    @Provides
    fun provideFragmentController(
        getFragmentImpl: IGetFragment,
        openFragmentImpl: IOpenFragment,
        replaceFragmentImpl: IReplaceFragment,
        closeFragmentImpl: ICloseFragment
    ) = FragmentController(
        getFragmentImpl,
        openFragmentImpl,
        replaceFragmentImpl,
        closeFragmentImpl
    )

    @Provides
    fun provideSheetController() = SheetController()

    @Provides
    fun provideFragmentBackStackManager() = FragmentBackStackManager()
}


@Module
class AudioModule {
    @Provides
    fun provideAudioController() = AudioController()
}

@Module
class FileModule {
    @Provides
    fun provideFilesController() = FilesController()
}
