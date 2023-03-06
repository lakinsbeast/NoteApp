package code.with.me.testroomandnavigationdrawertest.ui

import androidx.lifecycle.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
//import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepository
import code.with.me.testroomandnavigationdrawertest.data.localDataSource.NoteDAO
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.deleteNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.getListOfNotesUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.insertNoteUseCase
import code.with.me.testroomandnavigationdrawertest.domain.noteUseCases.updateNoteUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class NoteViewModel @Inject constructor(
    private val deleteNoteUseCase: deleteNoteUseCase,
    private val getListOfNotesUseCase: getListOfNotesUseCase,
    private val insertNoteUseCase: insertNoteUseCase,
    private val updateNoteUseCase: updateNoteUseCase
) : ViewModel() {

    fun getAllNotes(): Flow<List<Note>> = getListOfNotesUseCase()

    fun insert(note: Note) = viewModelScope.launch {
        insertNoteUseCase(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        deleteNoteUseCase(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        updateNoteUseCase(note)
    }

}

class NoteViewModelFactory @Inject constructor(
    private val deleteNoteUseCase: deleteNoteUseCase,
    private val getListOfNotesUseCase: getListOfNotesUseCase,
    private val insertNoteUseCase: insertNoteUseCase,
    private val updateNoteUseCase: updateNoteUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(
                deleteNoteUseCase,
                getListOfNotesUseCase,
                insertNoteUseCase,
                updateNoteUseCase
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }

}