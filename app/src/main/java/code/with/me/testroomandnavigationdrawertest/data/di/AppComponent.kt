package code.with.me.testroomandnavigationdrawertest.data.di

import code.with.me.testroomandnavigationdrawertest.ui.fragment.FolderListFragment
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.ui.fragment.NotesListFragment
import code.with.me.testroomandnavigationdrawertest.domain.di.RepositoryModule
import code.with.me.testroomandnavigationdrawertest.ui.AddNoteActivity
import code.with.me.testroomandnavigationdrawertest.ui.fragment.FavoriteFoldersListFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.FolderHomeFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.LastEditedFolderListFragment
import code.with.me.testroomandnavigationdrawertest.ui.fragment.LastViewedFoldersListFragment
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.fragment.NoteHomeFragment
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFolderListFragment
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseNotesListFragment
import code.with.me.testroomandnavigationdrawertest.ui.di.ApplicationModule
import code.with.me.testroomandnavigationdrawertest.ui.di.BindAppModule
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddFolderSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddFolderTagSheetMenu
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddNoteTagSheetMenu
import code.with.me.testroomandnavigationdrawertest.ui.sheet.MakeANoteSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.RenameFolderSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SelectFolderDestinationSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.ViewANoteSheet
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, DatabaseModule::class, RepositoryModule::class, BindAppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: AddNoteActivity)
    fun inject(application: NotesApplication)
    fun inject(viewANoteSheet: ViewANoteSheet)
    fun inject(baseFolderListFragment: BaseFolderListFragment)
    fun inject(notesListFragment: NotesListFragment)
    fun inject(folderListFragment: FolderListFragment)
    fun inject(addFolderSheet: AddFolderSheet)
    fun inject(baseNotesListFragment: BaseNotesListFragment)
    fun inject(addNoteTagSheetMenu: AddNoteTagSheetMenu)
    fun inject(makeANoteSheet: MakeANoteSheet)
    fun inject(renameFolderSheet: RenameFolderSheet)
    fun inject(lastViewedFoldersFragment: LastViewedFoldersListFragment)
    fun inject(favoriteFoldersFragment: FavoriteFoldersListFragment)
    fun inject(lastEditedFolderFragment: LastEditedFolderListFragment)
    fun inject(addFolderTagSheetMenu: AddFolderTagSheetMenu)
    fun inject(folderHomeFragment: FolderHomeFragment)
    fun inject(selectFolderDestinationSheet: SelectFolderDestinationSheet)
    fun inject(noteHomeFragment: NoteHomeFragment)

}