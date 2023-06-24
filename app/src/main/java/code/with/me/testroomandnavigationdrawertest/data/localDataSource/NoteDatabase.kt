package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import android.content.Context
import androidx.room.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.deleteNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.getListOfNotesUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.insertNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.updateNoteUseCase

@Database(entities = [Note::class, Folder::class], exportSchema = false, version = 3)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDAO

    abstract fun folderDao(): FolderDAO

}