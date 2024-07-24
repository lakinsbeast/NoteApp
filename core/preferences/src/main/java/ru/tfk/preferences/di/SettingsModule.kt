package ru.tfk.preferences.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class SettingsModule() {
//    @Provides
//    fun provideDataStoreManager(
//        @ApplicationContext context: Context,
//    ) = DataStoreManager(context)
}
