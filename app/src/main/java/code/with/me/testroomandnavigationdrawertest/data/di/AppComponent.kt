package code.with.me.testroomandnavigationdrawertest.data.di

import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.domain.di.RepositoryModule
import code.with.me.testroomandnavigationdrawertest.domain.di.UseCaseModule
import code.with.me.testroomandnavigationdrawertest.ui.AddNoteActivity
import code.with.me.testroomandnavigationdrawertest.ui.DetailFragment
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.di.ApplicationModule
import code.with.me.testroomandnavigationdrawertest.ui.di.BindAppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, DatabaseModule::class,UseCaseModule::class, RepositoryModule::class, BindAppModule::class])
interface AppComponent {
    fun inject(activity: MainActivity)
    fun inject(activity: AddNoteActivity)
    fun inject(fragment: DetailFragment)
    fun inject(application: NotesApplication)

}