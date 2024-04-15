package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import javax.inject.Inject
import kotlin.math.roundToInt

class PreviewNoteDialogViewModel
@Inject
constructor(
    private val repoNote: NoteRepository,
    private val audioController: AudioController,
) : ViewModel() {

    private val _state = MutableStateFlow<NoteState>(NoteState.Loading)
    val state = _state.asStateFlow()

    private val _userActionAudioState =
        MutableStateFlow<UserActionAudioState>(UserActionAudioState.Loading)
    val userActionAudioState = _userActionAudioState.asStateFlow()


    private val _waveFormProgress = MutableStateFlow(0f)
    val waveFormProgress = _waveFormProgress.asStateFlow()

    private val _timerState = MutableStateFlow(Unit)
    val timerState = _timerState.asStateFlow()

    var currentNote: Note? = null

    //можно было использовать sharedflow, вместо стандартных значений = -1 но пока попробую так
    private val _firstAndLastId = MutableStateFlow(Pair(0, 0))
    val firstAndLastId = _firstAndLastId.asStateFlow()

    private val _firstAndLastAvailableId = MutableStateFlow(Pair(0L, 0L))
    val firstAndLastAvailableId = _firstAndLastAvailableId.asStateFlow()

    val audioPlaybackObserver = Observer<AudioPlayerState> { state -> }

    private var currentPos: Int = -1

    private var waveformUpdateJob: Job? = null
    private var timerJob: Job? = null

    var idIntent = 0L
    var doWaveFormOnTouch = false

    private suspend fun setState(state: NoteState) {
        _state.emit(state)
    }

    init {
        audioController.audioPlaybackStateLiveData.observeForever(audioPlaybackObserver)
    }

    override fun onCleared() {
        waveformUpdateJob?.cancel()
        timerJob?.cancel()
        waveformUpdateJob = null
        timerJob = null
        audioController.audioPlaybackStateLiveData.removeObserver(audioPlaybackObserver)
        super.onCleared()
    }

    fun startTimer(delayLong: Long) {
        timerJob?.let {
            timerJob?.cancel()
            timerJob = null
        }
        timerJob = viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            delay(delayLong)
            _timerState.emit(Unit)
        }
    }

    fun getFirstAndLastAvailableId(id: Long) {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val firstIdDeferred = async { repoNote.getNextAvailableId(id) }
            val lastIdDeferred = async { repoNote.getPreviousAvailableId(id) }

            val firstId = firstIdDeferred.await() ?: 0L
            val lastId = lastIdDeferred.await() ?: 0L

            _firstAndLastAvailableId.emit(Pair(firstId, lastId))
        }
    }


    fun getNoteById(id: Long) {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            try {
                setState(NoteState.Loading)
                withContext(Dispatchers.IO) {
                    repoNote.getNoteById(id).apply {
                        currentNote = this
                        setState(NoteState.Result(this))
                    }
                }
            } catch (e: Exception) {
                setState(NoteState.Error(e.localizedMessage))
            }
        }
    }

    fun getFirstAndLastCustomer() {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val firstIdDeferred = async { repoNote.getFirstCustomer() }
            val lastIdDeferred = async { repoNote.getLastCustomer() }

            val firstId = firstIdDeferred.await().toInt()
            val lastId = lastIdDeferred.await().toInt()

            _firstAndLastId.emit(Pair(firstId, lastId))
        }
    }


    fun processUserActionsAudio(userAction: UserActionAudioState) {
        viewModelScope.launch {
            when (userAction) {
                is UserActionAudioState.IsPlaying -> {
                    audioController.isAudioPlaying()
                }

                is UserActionAudioState.StartPlaying -> {
//                _audioPlayerState.postValue(AudioPlayerState.Playing)
                    _userActionAudioState.emit(UserActionAudioState.StartPlaying(userAction.audioUri))
                    try {
                        if (!audioController.isAudioPlaying()) {
                            if (currentPos != -1) {
                                audioController.continuePlaying()
                                audioController.player.seekTo(currentPos)
                            } else {
                                audioController.startPlaying(userAction.audioUri)
                            }
                            waveformUpdateJob?.cancel() // Cancel previous job if running
                            waveformUpdateJob = launch {
                                // TODO currentPosition всегда меньше duration и если поставить на паузу в конце, то он будет считаться завершенным
                                while (audioController.isAudioPlaying()) {
                                    try {
                                        val player = audioController.player
                                        currentPos =
                                            player.currentPosition
                                        if (currentPos != -1) {
                                            setWaveFormProgress(((currentPos * 100) / player.duration).toFloat())
                                        }
                                        if (player.currentPosition == player.duration) {
                                            currentPos = -1
                                            setWaveFormProgress(0f)
                                        }
                                        if (!audioController.isAudioPlaying()) {
                                            audioController.pausePlaying()
                                            break
                                        }
                                    } catch (e: Exception) {
                                        processUserActionsAudio(UserActionAudioState.Error(e.localizedMessage))
                                        break
                                    }
                                    delay(10)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        processUserActionsAudio(UserActionAudioState.Error(e.localizedMessage))
                    }
                }

                is UserActionAudioState.PausePlaying -> {
                    _userActionAudioState.emit(UserActionAudioState.PausePlaying)
                    if (audioController.isAudioPlaying()) {
                        audioController.pausePlaying()
                    }
                    waveformUpdateJob?.cancel()
                }

                else -> {
                }
            }
        }
    }

    private suspend fun setWaveFormProgress(progress: Float) {
        _waveFormProgress.emit(progress)
    }

    fun isAudioPlaying(): Boolean {
        return audioController.isAudioPlaying()
    }

    fun checkAudioPlayer(): Boolean {
        return try {
            audioController.player
            true
        } catch (e: UninitializedPropertyAccessException) {
            false
        }
    }

    fun setCurrentPos(progress: Float) {
        audioController.player.let {
            currentPos = ((it.duration.times(progress)).roundToInt()) / 100
        }
    }

    companion object {
        class PreviewNoteDialogViewModelFactory
        @Inject
        constructor(
            private val repo: NoteRepository,
            private val audioController: AudioController,
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PreviewNoteDialogViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PreviewNoteDialogViewModel(
                        repo,
                        audioController,
                    ) as T
                }
                throw IllegalArgumentException("ukn VM class")
            }
        }
    }

}