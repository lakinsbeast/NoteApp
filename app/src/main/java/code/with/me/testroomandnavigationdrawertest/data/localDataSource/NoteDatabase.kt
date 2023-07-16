package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import androidx.room.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.FolderTag
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteTag
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.converters.PhotoModelListConverter

@Database(
    entities = [Note::class, Folder::class, FolderTag::class, NoteTag::class],
    exportSchema = false,
    version = 12
)
@TypeConverters(value = [PhotoModelListConverter::class])
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDAO

    abstract fun folderDao(): FolderDAO

    abstract fun folderTagDAO(): FolderTagDAO

    abstract fun noteTagDAO(): NoteTagDAO

}