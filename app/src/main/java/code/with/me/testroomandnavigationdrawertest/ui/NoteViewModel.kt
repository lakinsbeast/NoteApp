package code.with.me.testroomandnavigationdrawertest.ui

import androidx.lifecycle.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.repos.FolderRepositoryImpl
//import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepository
import code.with.me.testroomandnavigationdrawertest.data.repos.NoteRepositoryImpl
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class NoteViewModel @Inject constructor(
//    private val deleteNoteUseCase: deleteNoteUseCase,
//    private val getListOfNotesUseCase: getListOfNotesUseCase,
//    private val insertNoteUseCase: insertNoteUseCase,
//    private val updateNoteUseCase: updateNoteUseCase
    private val repoNote: NoteRepositoryImpl
) : ViewModel() {

    fun getAllNotes(): Flow<List<Note>> = repoNote.getListOfNotes()

    fun insert(note: Note) = viewModelScope.launch {
        repoNote.insertNote(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        repoNote.deleteNote(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        repoNote.updateNote(note)
    }
}

class NoteViewModelFactory @Inject constructor(
//    private val deleteNoteUseCase: deleteNoteUseCase,
//    private val getListOfNotesUseCase: getListOfNotesUseCase,
//    private val insertNoteUseCase: insertNoteUseCase,
//    private val updateNoteUseCase: updateNoteUseCase
    private val repo: NoteRepositoryImpl
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(
//                deleteNoteUseCase,
//                getListOfNotesUseCase,
//                insertNoteUseCase,
//                updateNoteUseCase
                repo
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }

}