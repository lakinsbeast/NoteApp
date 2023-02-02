package code.with.me.testroomandnavigationdrawertest

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDAO {
    @Query("SELECT * FROM note")
    fun getAll(): Flow<List<Note>>

    @Query("SELECT * FROM note WHERE id IN (:userIds)")
    fun loadAllIds(userIds: IntArray): List<Note>

    @Query("SELECT * FROM note WHERE Title LiKE :title AND " + "text LIKE :text LIMIT 1")
    fun findByTitle(title: String, text: String): Note

    @Query("SELECT * FROM note WHERE id=:id ")
    fun getNoteById(id: Int): Note

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(note: Note)

    @Insert
    suspend fun insert(note: Note)

    @Delete
    suspend fun delete(note: Note): Int

//    update nihyua ne working nenvizhu eto govno
    @Update
    suspend fun update(note: Note)

}