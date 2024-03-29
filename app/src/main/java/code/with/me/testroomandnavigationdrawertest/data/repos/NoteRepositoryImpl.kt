package code.with.me.testroomandnavigationdrawertest.data.repos

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteFTS
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDAO
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl
    @Inject
    constructor(private val noteDAO: NoteDAO) : NoteRepository {
        override fun getListOfNotes(): Flow<List<Note>> = noteDAO.getListOfNotes()

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
