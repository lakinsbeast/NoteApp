package ru.tfk.database.dao

import android.database.sqlite.SQLiteException
import androidx.room.*
import kotlinx.coroutines.flow.Flow
import ru.tfk.database.model.Note
import ru.tfk.database.model.NoteFTS

@Dao
interface NoteDAO {
    @Query(SELECT_NOTE)
    @Throws(SQLiteException::class)
    fun getListOfNotes(): Flow<List<Note>>

    @Query("$SELECT_NOTE WHERE folderId = :id")
    @Throws(SQLiteException::class)
    fun getListOfNotes(id: Long): Flow<List<Note>>

    @Query(SELECT_NOTE_FTS)
    @Throws(SQLiteException::class)
    fun getListOfNotesNotFlow(): List<NoteFTS>

    // для поиска по подобию нужно в конце в query добавить "*", а иначе он будет искать только так, чтоб он был равен :query
    // и если вместо MATCH использовать LIKE, то поиск будет в примерно 700 раз медленней
    @Query("SELECT * FROM NoteFTS WHERE NoteFTS MATCH :query")
    fun searchNotes(query: String): List<NoteFTS>

    @Query("$SELECT_NOTE WHERE id IN (:userIds)")
    fun loadAllIds(userIds: IntArray): List<Note>

    @Query("$SELECT_NOTE WHERE Title LiKE :title AND text LIKE :text LIMIT 1")
    fun findByTitle(
        title: String,
        text: String,
    ): Note

    @Query("$SELECT_NOTE WHERE id=:id ")
    @Throws(SQLiteException::class)
    fun getNoteById(id: Long): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Throws(SQLiteException::class)
    suspend fun insertOrUpdate(note: Note)

    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertNote(note: Note)

    @Query("UPDATE Note SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun setToFavorite(id: Long)

    @Delete
    @Throws(SQLiteException::class)
    suspend fun deleteNote(note: Note): Int

    @Update
    @Throws(SQLiteException::class)
    suspend fun updateNote(note: Note)

    @Query("SELECT id FROM note ORDER BY id DESC LIMIT 1")
    fun getLastCustomer(): Long

    @Query("SELECT COUNT(*) FROM Note")
    fun getNoteCount(): Long

    @Query("SELECT id FROM note ORDER BY id ASC LIMIT 1")
    fun getFirstCustomer(): Long

    @Query("SELECT id FROM Note WHERE id > :currentId ORDER BY id ASC LIMIT 1")
    fun getNextAvailableId(currentId: Long): Long?

    @Query("SELECT id FROM Note WHERE id < :currentId ORDER BY id DESC LIMIT 1")
    fun getPreviousAvailableId(currentId: Long): Long?

    companion object {
        private const val INSERT_NOTE = ""
        private const val SELECT_NOTE = "SELECT * FROM note"
        private const val SELECT_NOTE_FTS = "SELECT * FROM NoteFTS"
        private const val UPDATE_NOTE = "UPDATE Note SET"
    }
}
