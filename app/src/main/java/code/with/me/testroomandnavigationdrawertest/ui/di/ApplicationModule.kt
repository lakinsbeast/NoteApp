package code.with.me.testroomandnavigationdrawertest.ui.di

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
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
import code.with.me.testroomandnavigationdrawertest.ui.mainScreenFragment.MainScreenViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.CreateFolderViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.FolderViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.MakeNoteViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteMenuSheetViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.NoteViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.PreviewNoteDialogViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SearchViewModel
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SettingsViewModelFactory
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.ViewANoteViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BindAppModule {
    @Binds
    // @FragmentScoped
    @Named("noteVMFactory")
    abstract fun bindNoteVMFactory(factory: NoteViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("searchVMFactory")
    abstract fun bindSearchVMFactory(factory: SearchViewModel.Companion.SearchViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("makeNoteVMFactory")
    abstract fun bindMakeNoteVMFactory(factory: MakeNoteViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("noteMenuSheetVMFactory")
    abstract fun bindNoteMenuSheetVMFactory(factory: NoteMenuSheetViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("viewANoteVMFactory")
    abstract fun bindViewANoteVMFactory(factory: ViewANoteViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("settingsVMFactory")
    abstract fun bindSettingsVMFactoryFactory(factory: SettingsViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("folderVMFactory")
    abstract fun bindFolderVMFactory(factory: FolderViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("createFolderVMFactory")
    abstract fun bindCreateFolderVMFactory(factory: CreateFolderViewModel.Companion.CreateFolderViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("mainScreenVMFactory")
    abstract fun bindMainScreenVMFactory(factory: MainScreenViewModelFactory): ViewModelProvider.Factory

    @Binds
    // @FragmentScoped
    @Named("previewVMFactory")
    abstract fun bindPreviewVMFactory(
        factory: PreviewNoteDialogViewModel.Companion.PreviewNoteDialogViewModelFactory,
    ): ViewModelProvider.Factory
}

@Module
@InstallIn(ActivityComponent::class)
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
    fun provideCloseFragmentImpl(getFragment: IGetFragment): ICloseFragment = CloseFragmentImpl(getFragment)

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
@InstallIn(ActivityComponent::class)
class AudioModule {
    @Provides
    fun provideAudioController() = AudioController()
}

@Module
@InstallIn(ActivityComponent::class)
class FileModule {
    @Provides
    fun provideFilesController() = FilesController()
}

// если убрать этот модуль, то выкидывает "[Dagger/MissingBinding] @dagger.hilt.android.qualifiers.ApplicationContext android.content.Context cannot
// be provided without an @Provides-annotated method", а если вернуть,
// то пишет, что происходит дублирование applicationcontext, hilt - говно собачье
@Module
@InstallIn(SingletonComponent::class)
class AppModule() {
    // добавил пустой конструктор, потому что выкидывает ошибку при билде " [Hilt] Modules that need to be instantiated by Hilt must have a visible, empty constructor."
//    constructor() : this(Application())

    @Singleton
    @Provides
    fun provideApplicationContext(application: Application): Context = application.applicationContext
}
