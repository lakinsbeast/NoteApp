package code.with.me.testroomandnavigationdrawertest.data.di

import android.content.Context
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.DataStoreManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule() {
    @Provides
    fun provideDataStoreManager(
        @ApplicationContext context: Context,
    ) = DataStoreManager(context)
}
