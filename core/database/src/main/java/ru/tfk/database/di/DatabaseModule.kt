package ru.tfk.database.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.tfk.database.const.Const.Companion.DATABASE_NAME
import ru.tfk.database.dao.FolderDAO
import ru.tfk.database.dao.FolderTagDAO
import ru.tfk.database.dao.NoteDAO
import ru.tfk.database.dao.NoteDatabase
import ru.tfk.database.dao.NoteTagDAO

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule() {
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
    ): NoteDatabase =
        Room.databaseBuilder(
            context,
            NoteDatabase::class.java,
            DATABASE_NAME,
        )
            .fallbackToDestructiveMigration().build()

    @Provides
    fun provideNoteDAO(db: NoteDatabase): NoteDAO = db.noteDao()

    @Provides
    fun provideFolderDAO(db: NoteDatabase): FolderDAO = db.folderDao()

    @Provides
    fun provideTagDAO(db: NoteDatabase): FolderTagDAO = db.folderTagDAO()

    @Provides
    fun provideNoteTagDAO(db: NoteDatabase): NoteTagDAO = db.noteTagDAO()
}
