package code.with.me.testroomandnavigationdrawertest

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData

class NoteRepository(private val noteDao: NoteDAO) {
    // Room выполняет все запросы в отдельном потоке.
    // Observed Flow(или livedata, я его счас проверяю) уведомит наблюдателя об изменении данных.
    val allNotes: LiveData<List<Note>> = noteDao.getAll()



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