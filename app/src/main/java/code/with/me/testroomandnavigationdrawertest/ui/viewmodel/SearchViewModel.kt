package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NoteFTS
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class SearchViewModel
@Inject
constructor(
    private val repoNote: NoteRepository,
) : ViewModel() {
    /** список заметок*/
    private var _noteList = MutableStateFlow<List<NoteFTS>>(emptyList())
    val noteList = _noteList.asStateFlow()

    private var searchJob: Job? = null

    /** поиск заметок по тексту [text]*/
    fun search(text: String) {
        searchJob?.cancel()
        searchJob = null
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            val list = repoNote.searchNotes("$text*")
            _noteList.emit(list)
        }
    }

    companion object {
        class SearchViewModelFactory
        @Inject
        constructor(
            private val repo: NoteRepository,
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(SearchViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return SearchViewModel(
                        repo,
                    ) as T
                }
                throw IllegalArgumentException("ukn VM class")
            }
        }
    }
}
