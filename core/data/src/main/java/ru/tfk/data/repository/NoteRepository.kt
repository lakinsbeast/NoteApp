package ru.tfk.data.repository

import kotlinx.coroutines.flow.Flow
import ru.tfk.database.model.Note
import ru.tfk.database.model.NoteFTS

// много ненужных функции, нужно уменьшить интерфейс
interface NoteRepository {
    fun getListOfNotes(): Flow<List<Note>>

    fun getListOfNotesFTS(): List<NoteFTS>

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
