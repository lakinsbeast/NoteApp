package code.with.me.testroomandnavigationdrawertest

import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import androidx.fragment.app.Fragment
import code.with.me.testroomandnavigationdrawertest.data.di.AppComponent
import code.with.me.testroomandnavigationdrawertest.data.di.DaggerAppComponent
import code.with.me.testroomandnavigationdrawertest.data.di.DatabaseModule
import code.with.me.testroomandnavigationdrawertest.data.di.SettingsModule

class NotesApplication : Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.builder().databaseModule(DatabaseModule(this)).settingsModule(SettingsModule(this)).build()
    }
}

val Activity.appComponent: AppComponent
    get() = (this.application as NotesApplication).appComponent

val Fragment.appComponent: AppComponent
    get() = (this.requireActivity().application as NotesApplication).appComponent

val Dialog.appComponent: AppComponent
    get() = (this.ownerActivity?.application as NotesApplication).appComponent