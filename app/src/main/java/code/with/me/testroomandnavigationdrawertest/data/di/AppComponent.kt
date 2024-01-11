package code.with.me.testroomandnavigationdrawertest.data.di

import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.domain.di.RepositoryModule
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFolderListFragment
import code.with.me.testroomandnavigationdrawertest.ui.di.ApplicationModule
import code.with.me.testroomandnavigationdrawertest.ui.di.AudioModule
import code.with.me.testroomandnavigationdrawertest.ui.di.BindAppModule
import code.with.me.testroomandnavigationdrawertest.ui.di.ControllerModule
import code.with.me.testroomandnavigationdrawertest.ui.di.FileModule
import code.with.me.testroomandnavigationdrawertest.ui.di.FormatterModule
import code.with.me.testroomandnavigationdrawertest.ui.di.MarkdownParserModule
import code.with.me.testroomandnavigationdrawertest.ui.di.ViewModelModule
import code.with.me.testroomandnavigationdrawertest.ui.dialog.CreateFolderDialog
import code.with.me.testroomandnavigationdrawertest.ui.dialog.PreviewNoteDialog
import code.with.me.testroomandnavigationdrawertest.ui.fragment.FavoriteFoldersListFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.FolderHomeFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.FolderListFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.LastEditedFolderListFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.LastViewedFoldersListFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MainScreenFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MakeNoteFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.NoteHomeFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.NotesListFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.SearchFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddFolderSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddFolderTagSheetMenu
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddNoteTagSheetMenu
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AudioRecorderDialog
import code.with.me.testroomandnavigationdrawertest.ui.sheet.NoteMenuSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.PaintSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.RenameFolderSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SeeTextSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SelectFolderDestinationSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SettingsSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.ViewANoteSheet
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        ApplicationModule::class,
        DatabaseModule::class,
        RepositoryModule::class,
        BindAppModule::class,
        ControllerModule::class,
        AudioModule::class,
        FileModule::class,
        MarkdownParserModule::class,
        FormatterModule::class,
        ViewModelModule::class,
        SettingsModule::class,
    ],
)
interface AppComponent {
    fun inject(activity: MainActivity)

    fun inject(application: NotesApplication)

    fun inject(viewANoteSheet: ViewANoteSheet)

    fun inject(createFolderDialog: CreateFolderDialog)

    fun inject(baseFolderListFragment: BaseFolderListFragment)

    fun inject(mainScreenFragment: MainScreenFragment)

    fun inject(notesListFragment: NotesListFragment)

    fun inject(folderListFragment: FolderListFragment)

    fun inject(paintSheet: PaintSheet)

    fun inject(addFolderSheet: AddFolderSheet)

    fun inject(noteMenuSheet: NoteMenuSheet)

    fun inject(fragment: SearchFragment)

    fun inject(addNoteTagSheetMenu: AddNoteTagSheetMenu)

    fun inject(makeNoteFragment: MakeNoteFragment)

    fun inject(renameFolderSheet: RenameFolderSheet)

    fun inject(lastViewedFoldersFragment: LastViewedFoldersListFragment)

    fun inject(favoriteFoldersFragment: FavoriteFoldersListFragment)

    fun inject(lastEditedFolderFragment: LastEditedFolderListFragment)

    fun inject(addFolderTagSheetMenu: AddFolderTagSheetMenu)

    fun inject(folderHomeFragment: FolderHomeFragment)

    fun inject(previewNoteDialog: PreviewNoteDialog)

    fun inject(audioRecorderDialog: AudioRecorderDialog)

    fun inject(settingsSheet: SettingsSheet)

    fun inject(selectFolderDestinationSheet: SelectFolderDestinationSheet)

    fun inject(noteHomeFragment: NoteHomeFragment)

    fun inject(seeTextSheet: SeeTextSheet)
}
