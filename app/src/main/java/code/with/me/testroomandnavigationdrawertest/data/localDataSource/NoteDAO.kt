package code.with.me.testroomandnavigationdrawertest.data.localDataSource

import android.database.sqlite.SQLiteException
import androidx.room.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {
    @Query("SELECT * FROM note")
    @Throws(SQLiteException::class)
    fun getListOfNotes(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE folderId = :id")
    @Throws(SQLiteException::class)
    fun getListOfNotes(id: Int): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id IN (:userIds)")
    fun loadAllIds(userIds: IntArray): List<Note>

    @Query("SELECT * FROM note WHERE Title LiKE :title AND " + "text LIKE :text LIMIT 1")
    fun findByTitle(title: String, text: String): Note

    @Query("SELECT * FROM note WHERE id=:id ")
    fun getNoteById(id: Int): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Throws(SQLiteException::class)
    suspend fun insertOrUpdate(note: Note)

    @Insert
    @Throws(SQLiteException::class)
    suspend fun insertNote(note: Note)

    @Delete
    @Throws(SQLiteException::class)
    suspend fun deleteNote(note: Note): Int

    //    update ne working
    @Update
    suspend fun updateNote(note: Note)

    @Query("SELECT * FROM note ORDER BY id DESC LIMIT 1")
    fun getLastCustomer(): Long

}