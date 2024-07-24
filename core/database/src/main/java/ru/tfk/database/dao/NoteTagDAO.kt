package ru.tfk.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import ru.tfk.database.model.NoteTag

@Dao
interface NoteTagDAO {
    @Query("SELECT * FROM notetag")
    fun getAllTags(): List<NoteTag>

    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertTag(noteTag: NoteTag)
}
