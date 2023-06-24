package code.with.me.testroomandnavigationdrawertest.data.di

import code.with.me.testroomandnavigationdrawertest.FolderListFragment
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.NotesListFragment
import code.with.me.testroomandnavigationdrawertest.domain.di.RepositoryModule
import code.with.me.testroomandnavigationdrawertest.domain.di.UseCaseModule
import code.with.me.testroomandnavigationdrawertest.ui.AddNoteActivity
import code.with.me.testroomandnavigationdrawertest.ui.AddNoteFragment
import code.with.me.testroomandnavigationdrawertest.ui.DetailFragment
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.di.ApplicationModule
import code.with.me.testroomandnavigationdrawertest.ui.di.BindAppModule
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AddFolderSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.RenameFolderSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SelectFolderDestinationSheet
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, DatabaseModule::class,UseCaseModule::class, RepositoryModule::class, BindAppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: AddNoteActivity)
    fun inject(fragment: DetailFragment)
    fun inject(fragment: AddNoteFragment)
    fun inject(application: NotesApplication)
    fun inject(notesListFragment: NotesListFragment)
    fun inject(folderListFragment: FolderListFragment)
    fun inject(addFolderSheet: AddFolderSheet)
    fun inject(renameFolderSheet: RenameFolderSheet)
    fun inject(selectFolderDestinationSheet: SelectFolderDestinationSheet)

}