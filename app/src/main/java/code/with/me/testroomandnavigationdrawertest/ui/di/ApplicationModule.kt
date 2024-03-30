package code.with.me.testroomandnavigationdrawertest.ui.di

import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.file.FilesController
import code.with.me.testroomandnavigationdrawertest.ui.FragmentBackStackManager
import code.with.me.testroomandnavigationdrawertest.ui.controllers.CloseFragmentImpl
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentController
import code.with.me.testroomandnavigationdrawertest.ui.controllers.GetFragmentImpl
import code.with.me.testroomandnavigationdrawertest.ui.controllers.ICloseFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.IGetFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.IOpenFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.IReplaceFragment
import code.with.me.testroomandnavigationdrawertest.ui.controllers.OpenFragmentImpl
import code.with.me.testroomandnavigationdrawertest.ui.controllers.ReplaceFragmentImpl
import code.with.me.testroomandnavigationdrawertest.ui.controllers.SheetController
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderTagViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MainScreenViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MakeNoteViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteMenuSheetViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteTagViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SearchViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SettingsViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.ViewANoteViewModelFactory
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
    @Named("searchVMFactory")
    abstract fun bindSearchVMFactory(factory: SearchViewModel.Companion.SearchViewModelFactory): ViewModelProvider.Factory

    @Binds
    @Singleton
    @Named("makeNoteVMFactory")
    abstract fun bindMakeNoteVMFactory(factory: MakeNoteViewModelFactory): ViewModelProvider.Factory

    @Binds
    @Singleton
    @Named("noteMenuSheetVMFactory")
    abstract fun bindNoteMenuSheetVMFactory(factory: NoteMenuSheetViewModelFactory): ViewModelProvider.Factory

    @Binds
    @Singleton
    @Named("viewANoteVMFactory")
    abstract fun bindViewANoteVMFactory(factory: ViewANoteViewModelFactory): ViewModelProvider.Factory

    @Binds
    @Singleton
    @Named("settingsVMFactory")
    abstract fun bindSettingsVMFactoryFactory(factory: SettingsViewModelFactory): ViewModelProvider.Factory

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

//    @Provides
//    fun provideStringToMarkdownTextParser(vararg formatters: Formatter): IStringToMarkdownTextParser {
//        return StringToMarkdownTextParser(*formatters)
//    }
}

@Module
class ControllerModule {
    @Provides
    fun provideFragmentBackStackManager(): FragmentBackStackManager = FragmentBackStackManager()

    @Provides
    fun provideGetFragmentImpl(): IGetFragment = GetFragmentImpl()

    @Provides
    fun provideOpenFragmentImpl(fragmentBackStackManager: FragmentBackStackManager): IOpenFragment =
        OpenFragmentImpl(fragmentBackStackManager)

    @Provides
    fun provideReplaceFragmentImpl(fragmentBackStackManager: FragmentBackStackManager): IReplaceFragment =
        ReplaceFragmentImpl(fragmentBackStackManager)

    @Provides
    fun provideCloseFragmentImpl(getFragment: IGetFragment): ICloseFragment =
        CloseFragmentImpl(getFragment)

    @Provides
    fun provideFragmentController(
        getFragmentImpl: IGetFragment,
        openFragmentImpl: IOpenFragment,
        replaceFragmentImpl: IReplaceFragment,
        closeFragmentImpl: ICloseFragment,
    ) = FragmentController(
        getFragmentImpl,
        openFragmentImpl,
        replaceFragmentImpl,
        closeFragmentImpl,
    )

    @Provides
    fun provideSheetController() = SheetController()
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
