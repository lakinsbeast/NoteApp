@file:OptIn(ExperimentalCoroutinesApi::class)

package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.domain.repo.NoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class ViewANoteViewModel
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
            timerJob =
                viewModelScope.launch(Dispatchers.IO.limitedParallelism(1)) {
                    delay(delayLong)
                    _timerState.emit(Unit)
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
                                waveformUpdateJob =
                                    launch {
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
    }

sealed class UserActionAudioState {
    object Loading : UserActionAudioState()

    class StartPlaying(val audioUri: String) : UserActionAudioState()

    class StartPlayingAt(val play: String) : UserActionAudioState()

    object PausePlaying : UserActionAudioState()

    class SeekTo(val position: Int) : UserActionAudioState()

    data class IsPlaying(val isPlaying: Boolean) : UserActionAudioState()

    class InitPlayer(val audioUri: String) : UserActionAudioState()

    class Error<T>(val error: T) : UserActionAudioState()
}

sealed class AudioPlayerState {
    //    data object Idle : AudioPlayerState()
    object Playing : AudioPlayerState()

    object Paused : AudioPlayerState()

    object Completed : AudioPlayerState()

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
