package code.with.me.testroomandnavigationdrawertest.data.di

import androidx.room.Room
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDatabase
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderTagDAO
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(private val application: NotesApplication) {

    @Provides
    @Singleton
    fun provideDatabase(): NoteDatabase = Room.databaseBuilder(application.applicationContext,NoteDatabase::class.java, "note").fallbackToDestructiveMigration().build()

    @Provides
    @Singleton
    fun provideNoteDAO(db: NoteDatabase): NoteDAO = db.noteDao()

    @Provides
    @Singleton
    fun provideFolderDAO(db: NoteDatabase): FolderDAO = db.folderDao()

    @Provides
    @Singleton
    fun provideTagDAO(db: NoteDatabase): FolderTagDAO = db.tagDAO()
}
