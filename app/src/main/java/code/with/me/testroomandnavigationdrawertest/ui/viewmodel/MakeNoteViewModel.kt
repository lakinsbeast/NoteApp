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

    private var lastSaveID = 0L

    private var pickedColor: String = "#FFFFFFFF"

    private var listOfPhotos = mutableListOf<PhotoModel>()

    var titleText = ""
    var text = ""

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
            async {
                lastSaveID = getLastCustomer()
                lastSaveID += 1
            }.await()
            launch {
                insert(note)
            }
        }

    }

    private var note: Note = Note(
        lastSaveID.toInt(),
        titleText,
        text,
        listOfPhotos.toList(),
        audioInString,
        pickedColor,
        -1,
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
        }
    }


    fun saveNote() {
        val newNote = Note(
            lastSaveID.toInt(),
            titleText,
            text,
            listOfPhotos.toList(),
            audioInString,
            pickedColor,
            -1,
            lastCreatedTime,
            lastOpenedTime,
            false,
            "-1"
        )
        if (newNote.titleNote.isNotEmpty() || newNote.textNote.isNotEmpty() || newNote.listOfImages.isNotEmpty()) {
            viewModelScope.launch {
                repoNote.updateNote(newNote)
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