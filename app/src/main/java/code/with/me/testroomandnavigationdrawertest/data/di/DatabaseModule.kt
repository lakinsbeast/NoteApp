package code.with.me.testroomandnavigationdrawertest.data.di

import android.content.Context
import androidx.room.Room
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.DATABASE_NAME
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.FolderTagDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDatabase
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteTagDAO
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

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
        ).fallbackToDestructiveMigration().build()

    @Provides
    fun provideNoteDAO(db: NoteDatabase): NoteDAO = db.noteDao()

    @Provides
    fun provideFolderDAO(db: NoteDatabase): FolderDAO = db.folderDao()

    @Provides
    fun provideTagDAO(db: NoteDatabase): FolderTagDAO = db.folderTagDAO()

    @Provides
    fun provideNoteTagDAO(db: NoteDatabase): NoteTagDAO = db.noteTagDAO()
}
