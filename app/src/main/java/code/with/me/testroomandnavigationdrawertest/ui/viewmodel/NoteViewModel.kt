package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class NoteViewModel
    @Inject
    constructor(
        private val repoNote: NoteRepository,
    ) : ViewModel() {
        // upd: кажись что-то надо менять

        private val _state = MutableLiveData<NoteState>(NoteState.Loading)
        private val _userActionsState = MutableLiveData<NotesListUserAction>()
        val state: LiveData<NoteState>
            get() = _state
        val userActionState: LiveData<NotesListUserAction>
            get() = _userActionsState

        private fun setState(state: NoteState) {
            _state.postValue(state)
        }

        var idFolder = -1L
        private lateinit var jobGetNote: Job

        fun getAllNotesFlow(): Flow<List<Note>> = repoNote.getListOfNotes()

        fun getAllNotes(): Job {
            // после смены фрагмента в mainscreen продолжались доставаться данные
            // из обеих flow с айди и без, принял решение сделать так:
            if (::jobGetNote.isInitialized) {
                jobGetNote.cancel()
            }
            jobGetNote =
                viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                    try {
                        repoNote.getListOfNotes().collect { notes ->
                            setState(NoteState.Result(notes))
                        }
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            setState(NoteState.Error(e.localizedMessage))
                        }
                    }
                }
            return jobGetNote
        }

        fun getAllNotes(id: Long): Job {
            if (::jobGetNote.isInitialized) {
                jobGetNote.cancel()
            }
            jobGetNote =
                viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                    try {
                        repoNote.getListOfNotes(id).collect { notes ->
                            setState(NoteState.Result(notes))
                        }
                    } catch (e: Exception) {
                        if (e !is CancellationException) {
                            setState(NoteState.Error(e.localizedMessage))
                        }
                    }
                }
            return jobGetNote
        }

        fun getNoteById(id: Long) {
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                try {
                    setState(NoteState.Loading)
                    withContext(Dispatchers.IO) {
                        setState(NoteState.Result(repoNote.getNoteById(id)))
                    }
                } catch (e: Exception) {
                    setState(NoteState.Error(e.localizedMessage))
                }
            }
        }

        fun shareTextNote(id: Long) {
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                try {
                    val note =
                        async {
                            return@async repoNote.getNoteById(id)
                        }.await()
                    val title = note.titleNote
                    val text = note.textNote
                    _userActionsState.postValue(NotesListUserAction.ShareText("$title\n$text"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun insert(note: Note) =
            viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                repoNote.insertNote(note)
            }

        fun insertOrUpdate(note: Note) =
            viewModelScope.launch {
                repoNote.insertOrUpdate(note)
            }

        fun getLastCustomer() = repoNote.getLastCustomer()

        fun delete(note: Note) =
            viewModelScope.launch {
                repoNote.deleteNote(note)
            }

        fun setToFavorite(
            id: Long,
            boolean: Boolean = true,
        ) = viewModelScope.launch {
            repoNote.setToFavorite(id)
        }

        fun update(note: Note) =
            viewModelScope.launch {
                repoNote.updateNote(note)
            }

        fun processUserActions(userAction: NotesListUserAction) {
            when (userAction) {
                is NotesListUserAction.ShareText<*> -> {
                    _userActionsState.value = NotesListUserAction.ShareText(userAction.data)
                }

                else -> {}
            }
        }
    }

sealed class NoteState {
    object Loading : NoteState()

    class Result<T>(val data: T) : NoteState()

    object EmptyResult : NoteState()

    class Error<T>(val error: T) : NoteState()
}

sealed class NotesListUserAction {
    class ShareText<T>(val data: T) : NotesListUserAction()
}

sealed class UserActionNote {
    object SavedNoteToDB : UserActionNote()

    object GetImage : UserActionNote()

    object GetCamera : UserActionNote()

    object GetMicrophone : UserActionNote()

    object GetDraw : UserActionNote()
}

class NoteViewModelFactory
    @Inject
    constructor(
        private val repo: NoteRepository,
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return NoteViewModel(
                    repo,
                ) as T
            }
            throw IllegalArgumentException("ukn VM class")
        }
    }
