@file:OptIn(ExperimentalCoroutinesApi::class)

package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import javax.inject.Inject
import kotlin.math.roundToInt

class ViewANoteViewModel
@Inject
constructor(
    private val repoNote: NoteRepository,
    private val audioController: AudioController,
) : ViewModel() {
    private var currentPos: Int = -1

    private val _state = MutableLiveData<NoteState>(NoteState.Loading)
    private val _userActionAudioState =
        MutableLiveData<UserActionAudioState>(UserActionAudioState.Loading)
    private val _audioPlayerState =
        MutableLiveData<AudioPlayerState>(AudioPlayerState.Paused)

    private val _waveFormProgress = MutableLiveData(0f)

    val audioPlaybackObserver =
        Observer<AudioPlayerState> { state ->
        }
    val state: LiveData<NoteState>
        get() = _state

    val userActionAudioState: LiveData<UserActionAudioState>
        get() = _userActionAudioState
    val audioPlayerState: LiveData<AudioPlayerState>
        get() = _audioPlayerState
    val waveFormProgress: LiveData<Float>
        get() = _waveFormProgress

    private val _lastIdOfNotes = MutableLiveData<Int>()
    val lastIdOfNotes: LiveData<Int>
        get() = _lastIdOfNotes

    private val _previousIdNote = MutableLiveData<Long>()
    val previousIdNote: LiveData<Long>
        get() = _previousIdNote

    private val _nextIdNote = MutableLiveData<Long>()
    val nextIdNote: LiveData<Long>
        get() = _nextIdNote

    private val _firstIdOfNotes = MutableLiveData<Int>()
    val firstIdOfNotes: LiveData<Int>
        get() = _firstIdOfNotes

    private fun setState(state: NoteState) {
        _state.postValue(state)
    }

    private fun setWaveFormProgress(progress: Float) {
        _waveFormProgress.postValue(progress)
    }

    init {
        audioController.audioPlaybackStateLiveData.observeForever(audioPlaybackObserver)
    }

    fun getAudioPlaybackStateLiveData(): MutableLiveData<AudioPlayerState> {
        return audioController.audioPlaybackStateLiveData
    }


    fun isAudioPlaying(): Boolean {
        return audioController.isAudioPlaying()
    }

    fun checkAudioPlayer(): MediaPlayer {
        return audioController.player
    }

    fun getDuration(): Int {
        return audioController.player.duration
    }

    fun setCurrentPos(progress: Float) {
        audioController.player.let {
            currentPos = ((it.duration.times(progress)).roundToInt()) / 100
        }
    }

    fun processUserActionsAudio(userAction: UserActionAudioState) {
        when (userAction) {
            is UserActionAudioState.IsPlaying -> {
                audioController.isAudioPlaying()
            }

            is UserActionAudioState.StartPlaying -> {
                _audioPlayerState.postValue(AudioPlayerState.Playing)
                _userActionAudioState.postValue(UserActionAudioState.StartPlaying(userAction.audioUri))
                try {
                    if (!audioController.isAudioPlaying()) {
                        if (currentPos != -1) {
                            audioController.continuePlaying()
                            audioController.player.seekTo(currentPos)
                        } else {
                            audioController.startPlaying(userAction.audioUri)
                        }
                        viewModelScope.launch {
                            // TODO currentPosition всегда меньше duration и если поставить на паузу в конце, то он будет считаться завершенным
                            while (true) {
//                                println("currentPos: $currentPos duration ${audioController.player?.duration}")
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
                _audioPlayerState.postValue(AudioPlayerState.Paused)
                _userActionAudioState.postValue(UserActionAudioState.PausePlaying)
                if (audioController.isAudioPlaying()) {
                    audioController.pausePlaying()
                }
            }

            else -> {
            }
        }
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

    fun getLastCustomer() {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val lastId =
                async {
                    return@async repoNote.getLastCustomer()
                }.await()
            _lastIdOfNotes.postValue(lastId.toInt())
        }
    }

    fun getPreviousAvailableId(id: Long) {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val lastId =
                async {
                    return@async repoNote.getPreviousAvailableId(id)
                }.await()
            _previousIdNote.postValue(lastId ?: 0L)
        }
    }

    fun getNextAvailableId(id: Long) {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val lastId =
                async {
                    return@async repoNote.getNextAvailableId(id)
                }.await()
            _nextIdNote.postValue(lastId ?: 0L)
        }
    }

    fun getFirstCustomer() {
        viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
            val firstId =
                async {
                    return@async repoNote.getFirstCustomer()
                }.await()
            _firstIdOfNotes.postValue(firstId.toInt())
        }
    }

    override fun onCleared() {
        audioController.audioPlaybackStateLiveData.removeObserver(audioPlaybackObserver)
        super.onCleared()
    }
}

sealed class UserActionAudioState {
    data object Loading : UserActionAudioState()

    class StartPlaying(val audioUri: String) : UserActionAudioState()

    class StartPlayingAt(val play: String) : UserActionAudioState()

    data object PausePlaying : UserActionAudioState()

    class SeekTo(val position: Int) : UserActionAudioState()

    data class IsPlaying(val isPlaying: Boolean) : UserActionAudioState()

    class InitPlayer(val audioUri: String) : UserActionAudioState()

    class Error<T>(val error: T) : UserActionAudioState()
}

sealed class AudioPlayerState {
    //    data object Idle : AudioPlayerState()
    data object Playing : AudioPlayerState()

    data object Paused : AudioPlayerState()

    data object Completed : AudioPlayerState()

    class Error<T>(val error: T) : AudioPlayerState()
}

class ViewANoteViewModelFactory
@Inject
constructor(
    private val repo: NoteRepository,
    private val audioController: AudioController,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ViewANoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ViewANoteViewModel(
                repo,
                audioController,
            ) as T
        }
        throw IllegalArgumentException("ukn VM class")
    }
}
