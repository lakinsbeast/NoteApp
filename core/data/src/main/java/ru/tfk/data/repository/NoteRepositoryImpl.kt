package ru.tfk.data.repository

import kotlinx.coroutines.flow.Flow
import ru.tfk.database.dao.NoteDAO
import ru.tfk.database.model.Note
import ru.tfk.database.model.NoteFTS
import javax.inject.Inject

class NoteRepositoryImpl
    @Inject
    constructor(private val noteDAO: NoteDAO) : NoteRepository {
        override fun getListOfNotes(): Flow<List<Note>> = noteDAO.getListOfNotes()

        override fun getListOfNotesFTS(): List<NoteFTS> = noteDAO.getListOfNotesNotFlow()

        override fun getNoteById(id: Long): Note = noteDAO.getNoteById(id)

        override suspend fun insertNote(note: Note) = noteDAO.insertNote(note)

        override suspend fun insertOrUpdate(note: Note) = noteDAO.insertOrUpdate(note)

        override suspend fun deleteNote(note: Note) = noteDAO.deleteNote(note)

        override suspend fun setToFavorite(id: Long) = noteDAO.setToFavorite(id)

        override suspend fun updateNote(note: Note) = noteDAO.updateNote(note)

        override fun getLastCustomer(): Long = noteDAO.getLastCustomer()

        override fun getNoteCount(): Long = noteDAO.getNoteCount()

        override fun getFirstCustomer(): Long = noteDAO.getFirstCustomer()

        override suspend fun getNextAvailableId(currentId: Long): Long? = noteDAO.getNextAvailableId(currentId)

        override fun getPreviousAvailableId(currentId: Long): Long? = noteDAO.getPreviousAvailableId(currentId)

        override fun getListOfNotes(id: Long): Flow<List<Note>> = noteDAO.getListOfNotes(id)

        override fun searchNotes(query: String): List<NoteFTS> = noteDAO.searchNotes(query)
    }
