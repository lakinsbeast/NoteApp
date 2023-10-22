package code.with.me.testroomandnavigationdrawertest

import android.app.Application
import code.with.me.testroomandnavigationdrawertest.data.Utils.findActivity
import code.with.me.testroomandnavigationdrawertest.data.di.AppComponent
import code.with.me.testroomandnavigationdrawertest.data.di.DaggerAppComponent
import code.with.me.testroomandnavigationdrawertest.data.di.DatabaseModule
import code.with.me.testroomandnavigationdrawertest.data.di.SettingsModule
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDatabase
import code.with.me.testroomandnavigationdrawertest.ui.di.ApplicationModule
//import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob


class NotesApplication : Application() {

    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().applicationModule(ApplicationModule(this))
            .databaseModule(DatabaseModule(this)).settingsModule(SettingsModule(this)).build()
    }

    override fun onCreate() {
        super.onCreate()


//        appComponent = DaggerAppComponent.builder().build().inject(this)
    }


}