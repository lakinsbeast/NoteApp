package code.with.me.testroomandnavigationdrawertest.data.repos

import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDAO
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class NoteRepositoryImpl @Inject constructor(private val noteDAO: NoteDAO) : NoteRepository {

    override fun getListOfNotes(): Flow<List<Note>> = noteDAO.getListOfNotes()

    override suspend fun insertNote(note: Note) = noteDAO.insertNote(note)

    override suspend fun insertOrUpdate(note: Note) = noteDAO.insertOrUpdate(note)

    override suspend fun deleteNote(note: Note) = noteDAO.deleteNote(note)

    override suspend fun updateNote(note: Note) = noteDAO.updateNote(note)
    override fun getLastCustomer(): Long = noteDAO.getLastCustomer()

    override fun getListOfNotes(id: Int): Flow<List<Note>> = noteDAO.getListOfNotes(id)

}