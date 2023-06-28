package code.with.me.testroomandnavigationdrawertest.domain.repo

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {

    fun getListOfNotes(): Flow<List<Note>>

    suspend fun insertNote(note: Note)

    suspend fun insertOrUpdate(note: Note)

    suspend fun deleteNote(note: Note): Int

    suspend fun updateNote(note: Note)

    fun getLastCustomer(): Long

    fun getListOfNotes(id: Int): Flow<List<Note>>
}