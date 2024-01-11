package code.with.me.testroomandnavigationdrawertest.domain.repo

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteFTS
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getListOfNotes(): Flow<List<Note>>

    fun getNoteById(id: Long): Note

    suspend fun insertNote(note: Note)

    suspend fun insertOrUpdate(note: Note)

    suspend fun deleteNote(note: Note): Int

    suspend fun setToFavorite(id: Long)

    suspend fun updateNote(note: Note)

    fun getLastCustomer(): Long

    fun getNoteCount(): Long

    fun getFirstCustomer(): Long

    fun getListOfNotes(id: Long): Flow<List<Note>>

    suspend fun getNextAvailableId(currentId: Long): Long?

    fun getPreviousAvailableId(currentId: Long): Long?

    fun searchNotes(query: String): List<NoteFTS>
}
