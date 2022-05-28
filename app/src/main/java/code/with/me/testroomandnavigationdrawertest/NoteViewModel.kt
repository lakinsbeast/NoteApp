package code.with.me.testroomandnavigationdrawertest

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class NoteViewModel(private val repo: NoteRepository): ViewModel() {

    // Использование LiveData и кэширование того, что возвращает allWords, имеет несколько преимуществ:
    // - Мы можем поместить наблюдателя на данные (вместо опроса на предмет изменений) и обновлять
    // пользовательский интерфейс только тогда, когда данные действительно изменяются.
    // - Репозиторий полностью отделен от пользовательского интерфейса через ViewModel.
    val allNotes: LiveData<MutableList<Note>> = repo.allNotes

    fun insert(note: Note) = viewModelScope.launch {
        repo.insert(note)
    }
    fun delete(note: Note) = viewModelScope.launch {
        repo.delete(note)
    }
    fun update(note: Note) = viewModelScope.launch {
        repo.update(note)
    }
    fun deleteById(id: Int) = viewModelScope.launch {
        repo.deleteById(id)
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