package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class NoteMenuSheetViewModel
@Inject
constructor(
    private val repoNote: NoteRepository,
) : ViewModel() {
    /**
     * шерит текст и заголовок для создания интента
     **/
    private var _shareTextLiveData = MutableStateFlow("")
    var shareTextLiveData = _shareTextLiveData.asStateFlow()

    /**
     * достает текст и заголовок у заметки по [id]
     */
    fun shareText(id: Long) {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val note = async {
                repoNote.getNoteById(id)
            }.await()
            _shareTextLiveData.emit("${note.titleNote}\n${note.textNote}")
        }
    }
}

class NoteMenuSheetViewModelFactory
@Inject
constructor(
    private val repo: NoteRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteMenuSheetViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteMenuSheetViewModel(
                repo,
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }
}
