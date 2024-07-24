package ru.tfk.database.dao

import androidx.room.*
import ru.tfk.database.converters.PhotoModelListConverter
import ru.tfk.database.model.Folder
import ru.tfk.database.model.FolderTag
import ru.tfk.database.model.Note
import ru.tfk.database.model.NoteFTS
import ru.tfk.database.model.NoteTag

@Database(
    entities = [Note::class, Folder::class, FolderTag::class, NoteTag::class, NoteFTS::class],
    exportSchema = false,
    version = 15,
)
@TypeConverters(value = [PhotoModelListConverter::class])
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDAO

    abstract fun folderDao(): FolderDAO

    abstract fun folderTagDAO(): FolderTagDAO

    abstract fun noteTagDAO(): NoteTagDAO
}
