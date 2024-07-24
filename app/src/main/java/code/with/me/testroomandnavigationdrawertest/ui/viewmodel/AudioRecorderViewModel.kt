package code.with.me.testroomandnavigationdrawertest.ui.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import code.with.me.testroomandnavigationdrawertest.audio.AudioController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException
import javax.inject.Inject

@HiltViewModel
class AudioRecorderViewModel
    @Inject
    constructor() : ViewModel() {
        private var seconds: Long = 0L
        private val _recordingTime = MutableStateFlow(0L)
        val recordingTime: StateFlow<Long> get() = _recordingTime.asStateFlow()

        private val _scale = MutableStateFlow(0F)
        val scale: StateFlow<Float> get() = _scale.asStateFlow()

        private val timerJob: Job by lazy {
            CoroutineScope(Dispatchers.IO.limitedParallelism(1)).launch {
                while (true) {
                    _recordingTime.emit(seconds)
                    delay(1000)
                    seconds++
                }
            }
        }

        fun startRecording(
            audioController: AudioController,
            activity: Activity,
        ) {
            audioController.startRecording(activity) { scale ->
                viewModelScope.launch {
                    _scale.emit(scale)
                }
            }

            startTimer()
        }

        fun stopRecording(audioController: AudioController) {
            audioController.stopRecording()
            stopTimer()
        }

        fun getAudioPath(
            audioController: AudioController,
            activity: Activity,
        ) = audioController.getAudioPath(activity)

        fun stopPlaying(audioController: AudioController) {
            audioController.stopPlaying()
        }

        private fun startTimer() {
            timerJob
        }

        private fun stopTimer() {
            timerJob.cancel()
        }

        companion object {
            class AudioRecorderViewModelFactory
                @Inject
                constructor() : ViewModelProvider.Factory {
                    override fun <T : ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(AudioRecorderViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return AudioRecorderViewModel() as T
                        }
                        throw IllegalArgumentException("ukn VM class")
                    }
                }
        }
    }
