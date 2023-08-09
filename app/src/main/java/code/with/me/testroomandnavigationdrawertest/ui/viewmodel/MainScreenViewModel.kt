package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Folder
import code.with.me.testroomandnavigationdrawertest.domain.repo.FolderRepository
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class MainScreenViewModel(
    private val repoNote: NoteRepository,
    private val repoFolder: FolderRepository,
) : ViewModel() {

    fun getAllFolders(): Flow<List<Folder>> = repoFolder.getAllFolders()


    fun getAllNotes() = repoNote.getListOfNotes()

    fun getAllNotes(id: Int) = repoNote.getListOfNotes(id)
}

class MainScreenViewModelFactory @Inject constructor(
    private val repoNote: NoteRepository,
    private val repoFolder: FolderRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainScreenViewModel(
                repoNote, repoFolder
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }

}