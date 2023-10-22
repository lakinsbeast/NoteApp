package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.data.data_classes.PhotoModel
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MakeNoteFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

class MakeNoteViewModel @Inject constructor(
    private val repoNote: NoteRepository
) : ViewModel() {

    private var cameraInString: String = ""
    var audioInString: String = ""
    private var paintInString: String = ""
    private var imageInString: String = ""
    private var lastCreatedTime: Long = 0L
    private var lastOpenedTime: Long = 0L
    var title = ""
        set(value) {
            if (field != value) {
                field = value
                saveNote()
            }

        }
    var textNote = ""
        set(value) {
            if (field != value) {
                field = value
                saveNote()
            }

        }

    private var lastSaveID = 0

    private var pickedColor: String = "#FFFFFFFF"
    private var folderId = -1
    var noteId = -1
        set(value) {
            if (field != value) {
                lastSaveID = field
                viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                    val note = async {
                        repoNote.getNoteById(value)
                    }.await()
                    _note.postValue(note)
                    audioInString = note.audioUrl
                }
            }
            field = value
        }

    private var _note = MutableLiveData<Note>()
    var noteObs: LiveData<Note> = _note

    private var listOfPhotos = mutableListOf<PhotoModel>()

    private var _titleText = MutableLiveData("")
    val titleText: LiveData<String>
        get() {
            return _titleText
        }

    private var _text = MutableLiveData("")
    val text: LiveData<String>
        get() = _text

    private var _currentPermission = MutableLiveData(TypeOfPermission.EMPTY)
    val currentPermission: LiveData<TypeOfPermission>
        get() = _currentPermission

    companion object {
        enum class TypeOfPermission {
            CAMERA, IMAGE_GALLERY, AUDIO, WRITE_EXTERNAL, EMPTY,
        }
    }

    init {
        lastCreatedTime = System.currentTimeMillis()
        lastOpenedTime = System.currentTimeMillis()

        viewModelScope.launch(Dispatchers.IO) {
            var count = async {
                repoNote.getNoteCount()
            }.await()
            async {
                if (count == 0L) {
                    lastSaveID = 0
                } else {
                    lastSaveID = getLastCustomer().toInt()
                    lastSaveID += 1
                }

            }.await()
//            launch {
//                insert(note)
//            }
        }
    }

    fun insertNote() {
        viewModelScope.launch(Dispatchers.IO) {
            insert(note)
        }
    }

    private var note: Note = Note(
        lastSaveID.toInt(),
        title.toString(),
        textNote.toString(),
        listOfPhotos.toList(),
        audioInString,
        pickedColor,
        folderId,
        lastCreatedTime,
        lastOpenedTime,
        false,
        "-1"
    )


    private val _updateAdapterObserver = MutableLiveData(listOfPhotos)
    val updateAdapterObserver: LiveData<MutableList<PhotoModel>>
        get() = _updateAdapterObserver

    private val _state = MutableLiveData<NoteState>(NoteState.Loading)
    private val _userActionsState = MutableLiveData<UserActionNote>()
    val state: LiveData<NoteState>
        get() = _state
    val userActionState: LiveData<UserActionNote>
        get() = _userActionsState

    private fun setState(state: NoteState) {
        _state.postValue(state)
    }

    fun setTitleText(text: String) {
        _titleText.postValue(text)
    }

    fun setText(text: String) {
        _text.postValue(text)
    }

    fun setPermission(newPermission: TypeOfPermission) {
        _currentPermission.postValue(newPermission)
    }

    fun getLastCustomer() = repoNote.getLastCustomer()

    fun insert(note: Note) = viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
        repoNote.insertNote(note)
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

            else -> {}
        }
    }


    fun saveNote() {
        val newNote = Note(
            lastSaveID,
            title,
            textNote,
            listOfPhotos.toList(),
            audioInString,
            pickedColor,
            folderId,
            lastCreatedTime,
            lastOpenedTime,
            false,
            "-1"
        )
        println("newNote: $newNote")
        if (newNote.titleNote.isNotEmpty() || newNote.textNote.isNotEmpty() || newNote.listOfImages.isNotEmpty()) {
            viewModelScope.launch {
                try {
                    repoNote.updateNote(newNote)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun getImageFromGallery(imageUri: Uri) {
        imageInString = imageUri.toString()
        listOfPhotos.addAndUpdate(PhotoModel(imageInString))
        println()
    }

    fun setPaintSheetResult(paintBitmap: String) {
        paintInString = paintBitmap
        listOfPhotos.addAndUpdate(PhotoModel(paintInString))

    }

    fun getImageFromCamera(cameraUri: String) {
        cameraInString = cameraUri
        listOfPhotos.addAndUpdate(PhotoModel(cameraInString))
    }

    fun setFolderId(id: Int) {
        folderId = id
    }


    fun MutableList<PhotoModel>.addAndUpdate(newValue: PhotoModel) {
        this.add(newValue)
        _updateAdapterObserver.postValue(this)
    }


}


class MakeNoteViewModelFactory @Inject constructor(
    private val repo: NoteRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MakeNoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MakeNoteViewModel(
                repo
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }
}