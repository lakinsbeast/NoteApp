package ru.tfk.makenote

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import ru.tfk.data.repository.NoteRepository
import ru.tfk.database.model.Note
import ru.tfk.database.model.PhotoModel
import ru.tfk.model.states.NoteState
import ru.tfk.model.states.UserActionNote
import javax.inject.Inject

@HiltViewModel
class MakeNoteViewModel
    @Inject
    constructor(
        private val repoNote: NoteRepository,
    ) : ViewModel() {
        lateinit var cameraUri: Uri

        /** ссылка на фото из камеры*/
        private var cameraInString: String = ""

        /** ссылка на аудио*/
        var audioInString: String = ""

        /** ссылка на нарисованную картинку (сохраняется как фото)*/
        private var paintInString: String = ""

        /** ссылка на фото из галереи*/
        private var imageInString: String = ""

        /** таймстап когда создана заметка*/
        private var lastCreatedTime: Long = 0L

        /** таймстап когда открыта заметка*/
        private var lastOpenedTime: Long = 0L

        /** заголовок заметки*/
        var title = ""
            set(value) {
                if (field != value) {
                    field = value
                    saveNote()
                }
            }

        /** текст заметки*/
        var textNote = ""
            set(value) {
                if (field != value) {
                    field = value
                    saveNote()
                }
            }

        /** последний сохраненный айди заметки*/
        private var lastSaveID = -1L

        private var pickedColor: String = "#FFFFFFFF"
        private var folderId = -1L

        /**
         * Todo: понять зачем нужен и отличие от lastSaveID
         *
         */
        var noteId = -1L
            set(value) {
                if (field != value) {
                    field = value
                    lastSaveID = value

                    viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                        val note =
                            async {
                                repoNote.getNoteById(value)
                            }.await()
                        _note.postValue(note)
                        audioInString = note.audioUrl
                    }
                }
            }

        init {
            /** при создании viewmodel сохраняются таймстапмы и достается id последней заметки
             * и сохраняется id как id += 1 и сохраняется новая заметка сразу */
            lastCreatedTime = System.currentTimeMillis()
            lastOpenedTime = System.currentTimeMillis()

            viewModelScope.launch(Dispatchers.IO) {
                val count =
                    async {
                        repoNote.getNoteCount()
                    }.await()
                async {
                    if (count == 0L) {
                        lastSaveID = 1
                        insertNote()
                    } else {
                        if (lastSaveID == -1L) {
                            lastSaveID = getLastCustomer()
                            lastSaveID += 1
                            insertNote()
                        }
                    }
                }.await()
            }
        }

        private val _note = MutableLiveData<Note>()
        val noteObs: LiveData<Note>
            get() = _note

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
                CAMERA,
                IMAGE_GALLERY,
                AUDIO,
                WRITE_EXTERNAL,
                EMPTY,
            }
        }

        private suspend fun insertNote() {
            note =
                Note(
                    title.toString(),
                    textNote.toString(),
                    listOfPhotos.toList(),
                    audioInString,
                    pickedColor,
                    folderId,
                    lastCreatedTime,
                    lastOpenedTime,
                    false,
                    "-1",
                ).apply {
                    id = lastSaveID
                }
            insert(note)
        }

        private lateinit var note: Note

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

        private fun getLastCustomer() = repoNote.getLastCustomer()

        suspend fun insert(note: Note) {
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
            val newNote =
                Note(
                    title,
                    textNote,
                    listOfPhotos.toList(),
                    audioInString,
                    pickedColor,
                    folderId,
                    lastCreatedTime,
                    lastOpenedTime,
                    false,
                    "-1",
                ).apply {
                    id = lastSaveID
                }
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

        fun setFolderId(id: Long) {
            folderId = id
        }

        private fun MutableList<PhotoModel>.addAndUpdate(newValue: PhotoModel) {
            this.add(newValue)
            _updateAdapterObserver.postValue(this)
        }
    }
