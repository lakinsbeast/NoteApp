package ru.tfk.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import ru.tfk.data.repository.NoteRepository
import ru.tfk.database.model.NoteFTS
import javax.inject.Inject

@HiltViewModel
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
            searchJob =
                viewModelScope.launch(Dispatchers.IO) {
                    try {
                        val list =
                            if (text.isEmpty()) {
                                repoNote.getListOfNotesFTS()
                            } else {
                                repoNote.searchNotes("$text*")
                            }
                        _noteList.emit(list)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
        }
    }
