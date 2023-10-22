package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import android.database.sqlite.SQLiteException
import androidx.room.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {

    @Query(SELECT_NOTE)
    @Throws(SQLiteException::class)
    fun getListOfNotes(): Flow<List<Note>>

    @Query("$SELECT_NOTE WHERE folderId = :id")
    @Throws(SQLiteException::class)
    fun getListOfNotes(id: Int): Flow<List<Note>>

    @Query("$SELECT_NOTE WHERE id IN (:userIds)")
    fun loadAllIds(userIds: IntArray): List<Note>

    @Query("$SELECT_NOTE WHERE Title LiKE :title AND text LIKE :text LIMIT 1")
    fun findByTitle(title: String, text: String): Note

    @Query("$SELECT_NOTE WHERE second_id=:id ")
    @Throws(SQLiteException::class)
    fun getNoteById(id: Int): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Throws(SQLiteException::class)
    suspend fun insertOrUpdate(note: Note)

    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertNote(note: Note)

    @Query("UPDATE Note SET isFavorite = CASE WHEN isFavorite = 1 THEN 0 ELSE 1 END WHERE second_id = :id")
    suspend fun setToFavorite(id: Int)

    @Delete
    @Throws(SQLiteException::class)
    suspend fun deleteNote(note: Note): Int

    //    update not working
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateNote(note: Note)

    @Query("$SELECT_NOTE ORDER BY second_id DESC LIMIT 1")
    fun getLastCustomer(): Long
    @Query("SELECT COUNT(*) FROM Note")
    fun getNoteCount(): Long
    @Query("$SELECT_NOTE ORDER BY second_id ASC LIMIT 1")
    fun getFirstCustomer(): Long

    @Query("SELECT second_id FROM Note WHERE second_id > :currentId ORDER BY second_id ASC LIMIT 1")
    fun getNextAvailableId(currentId: Int): Int?

    @Query("SELECT second_id FROM Note WHERE second_id < :currentId ORDER BY second_id DESC LIMIT 1")
    fun getPreviousAvailableId(currentId: Int): Int?


    companion object {
        private const val INSERT_NOTE = ""
        private const val SELECT_NOTE = "SELECT * FROM note"
        private const val UPDATE_NOTE = "UPDATE Note SET"
    }


}