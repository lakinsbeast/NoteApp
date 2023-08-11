package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.*
import code.with.me.testroomandnavigationdrawertest.Utils.println
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
//import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import javax.inject.Inject

class NoteViewModel @Inject constructor(
    private val repoNote: NoteRepository
) : ViewModel() {

    //MVI реализовал не самым лучшим способом, можно было создать
    //BaseViewModel<MviState, MviActions> и сделать проще

    //upd: i dont like this ☹️

    private val _state = MutableLiveData<NoteState>(NoteState.Loading)
    private val _userActionsState = MutableLiveData<UserActionNote>()
    val state: LiveData<NoteState>
        get() = _state
    val userActionState: LiveData<UserActionNote>
        get() = _userActionsState

    private fun setState(state: NoteState) {
        _state.postValue(state)
    }

    private lateinit var jobGetNote: Job

    override fun onCleared() {
        "before oncleared".println()
        super.onCleared()
        "after oncleared".println()
    }

    fun getAllNotes(): Job {
        //после смены фрагмента в mainscreen продолжались доставаться данные
        //из обеих flow с айди и без, принято решение сделать так:
        if (::jobGetNote.isInitialized) {
            jobGetNote.cancel()
        }
        jobGetNote = viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            try {
                repoNote.getListOfNotes().collect() { notes ->
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

    fun getAllNotes(id: Int): Job {
        if (::jobGetNote.isInitialized) {
            jobGetNote.cancel()
        }
        jobGetNote = viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            try {
                repoNote.getListOfNotes(id).collect() { notes ->
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


    fun getNoteById(id: Int) {
        viewModelScope.launch {
            try {
                setState(NoteState.Loading)
                withContext(Dispatchers.IO.limitedParallelism(1)) {
                    setState(NoteState.Result(repoNote.getNoteById(id)))
                }
            } catch (e: Exception) {
                setState(NoteState.Error(e.localizedMessage))
            }
        }
    }

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
        repoNote.insertNote(note)
    }

    fun insertOrUpdate(note: Note) = viewModelScope.launch {
        repoNote.insertOrUpdate(note)
    }

    fun getLastCustomer() = repoNote.getLastCustomer()

    fun delete(note: Note) = viewModelScope.launch {
        repoNote.deleteNote(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repoNote.updateNote(note)
    }


    fun processUserActions(userAction: UserActionNote) {
        when (userAction) {
            is UserActionNote.GetImage -> {
                _userActionsState.value = UserActionNote.GetImage
            }

            is UserActionNote.GetCamera -> {
                _userActionsState.value = UserActionNote.GetCamera
            }

            is UserActionNote.GetMicrophone -> {
                _userActionsState.value = UserActionNote.GetMicrophone
            }

            is UserActionNote.GetDraw -> {
                _userActionsState.value = UserActionNote.GetDraw
            }

            is UserActionNote.SavedNoteToDB -> {
                _userActionsState.value = UserActionNote.SavedNoteToDB
            }
        }
    }


    fun saveNote(
        noteId: Int,
        title: String,
        text: String,
        listOfPhotos: List<PhotoModel>,
        audio: String,
        color: String,
        folderId: Int,
        createdTime: Long,
        openedTime: Long,
        isDeleted: Boolean,
        customData: String
    ) {
        if (title.isNotEmpty() || text.isNotEmpty() || listOfPhotos.isNotEmpty()) {
            viewModelScope.launch {
                val note = Note(
                    noteId,
                    title,
                    text,
                    listOfPhotos,
                    audio,
                    color,
                    folderId,
                    createdTime,
                    openedTime,
                    isDeleted,
                    customData
                )
                repoNote.updateNote(note)
                processUserActions(UserActionNote.SavedNoteToDB)
            }
        }
    }

}

sealed class NoteState {
    data object Loading : NoteState()
    class Result<T>(val data: T) : NoteState()
    data object EmptyResult : NoteState()
    class Error<T>(val error: T) : NoteState()
}

sealed class UserActionNote {
    data object SavedNoteToDB : UserActionNote()
    data object GetImage : UserActionNote()
    data object GetCamera : UserActionNote()
    data object GetMicrophone : UserActionNote()
    data object GetDraw : UserActionNote()
}

class NoteViewModelFactory @Inject constructor(
    private val repo: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(
                repo
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }

}