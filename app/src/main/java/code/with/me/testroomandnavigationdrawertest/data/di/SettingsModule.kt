package code.with.me.testroomandnavigationdrawertest.data.di

import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.DataStoreManager
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class SettingsModule(private val application: NotesApplication) {
    @Provides
    @Singleton
    fun provideDataStoreManager() = DataStoreManager(application.applicationContext)
}
