package code.with.me.testroomandnavigationdrawertest.ui

import androidx.lifecycle.*
import code.with.me.testroomandnavigationdrawertest.Note
import code.with.me.testroomandnavigationdrawertest.domain.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class NoteViewModel(private val repo: NoteRepository): ViewModel() {
    fun getAll(): Flow<List<Note>> = repo.getAll()

    fun insert(note: Note) = viewModelScope.launch {
        repo.insert(note)
    }
    fun delete(note: Note) = viewModelScope.launch {
        repo.delete(note)
    }
    fun update(note: Note) = viewModelScope.launch {
        repo.update(note)
    }

}

class NoteViewModelFactory(private val repo: NoteRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(repo) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }

}