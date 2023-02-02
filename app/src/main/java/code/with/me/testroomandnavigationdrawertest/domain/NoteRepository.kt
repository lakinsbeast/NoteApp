package code.with.me.testroomandnavigationdrawertest.domain

import androidx.annotation.WorkerThread
import code.with.me.testroomandnavigationdrawertest.Note
import code.with.me.testroomandnavigationdrawertest.NoteDAO
import kotlinx.coroutines.flow.Flow

class NoteRepository(private val noteDao: NoteDAO) {
    // Room выполняет все запросы в отдельном потоке.
    // Observed Flow(или livedata, я его счас проверяю) уведомит наблюдателя об изменении данных.
    fun getAll(): Flow<List<Note>> = noteDao.getAll()



    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }
    @WorkerThread
    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }
    @WorkerThread
    suspend fun update(note: Note) {
        noteDao.update(note)
    }

}