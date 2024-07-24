package code.with.me.testroomandnavigationdrawertest.data.di

import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.di.AudioModule
import code.with.me.testroomandnavigationdrawertest.ui.di.ControllerModule
import code.with.me.testroomandnavigationdrawertest.ui.di.FileModule
import code.with.me.testroomandnavigationdrawertest.ui.di.FormatterModule
import code.with.me.testroomandnavigationdrawertest.ui.di.MarkdownParserModule
import code.with.me.testroomandnavigationdrawertest.ui.dialog.AudioRecorderDialog
import code.with.me.testroomandnavigationdrawertest.ui.dialog.CreateFolderDialog
import code.with.me.testroomandnavigationdrawertest.ui.dialog.PreviewNoteDialog
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MainScreenFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MakeNoteFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.NotesListFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.SearchFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.NoteMenuSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.PaintSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.RenameFolderSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SelectFolderDestinationSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SettingsSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.ViewANoteSheet
import dagger.Component

// @FragmentScoped
@Component(
    modules = [
        DatabaseModule::class,
//        RepositoryModule::class,
//        BindAppModule::class,
        ControllerModule::class,
        AudioModule::class,
        FileModule::class,
        MarkdownParserModule::class,
        FormatterModule::class,
//        ViewModelModule::class,
//        SettingsModule::class,
//        AppModule::class,
    ],
)
interface AppComponent {
    fun inject(activity: MainActivity)

    fun inject(application: NotesApplication)

    fun inject(viewANoteSheet: ViewANoteSheet)

    fun inject(createFolderDialog: CreateFolderDialog)

    fun inject(mainScreenFragment: MainScreenFragment)

    fun inject(notesListFragment: NotesListFragment)

    fun inject(paintSheet: PaintSheet)

    fun inject(noteMenuSheet: NoteMenuSheet)

    fun inject(fragment: SearchFragment)

    fun inject(makeNoteFragment: MakeNoteFragment)

    fun inject(renameFolderSheet: RenameFolderSheet)

    fun inject(previewNoteDialog: PreviewNoteDialog)

    fun inject(audioRecorderDialog: AudioRecorderDialog)

    fun inject(settingsSheet: SettingsSheet)

    fun inject(selectFolderDestinationSheet: SelectFolderDestinationSheet)
}
