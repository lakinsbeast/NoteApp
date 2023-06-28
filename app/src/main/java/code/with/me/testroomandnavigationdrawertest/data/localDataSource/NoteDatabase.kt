package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import androidx.room.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag

@Database(entities = [Note::class, Folder::class, FolderTag::class], exportSchema = false, version = 8)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDAO

    abstract fun folderDao(): FolderDAO

    abstract fun tagDAO(): FolderTagDAO

}