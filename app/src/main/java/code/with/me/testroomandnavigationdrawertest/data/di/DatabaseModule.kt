package code.with.me.testroomandnavigationdrawertest.data.di

import android.content.Context
import androidx.room.Room
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDAO
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule(private val application: NotesApplication) {

    @Provides
    @Singleton
    fun provideDatabase(): NoteDatabase = Room.databaseBuilder(application.applicationContext,NoteDatabase::class.java, "note").build()

    @Provides
    @Singleton
    fun provideNoteDAO(db: NoteDatabase): NoteDAO = db.noteDao()
}
