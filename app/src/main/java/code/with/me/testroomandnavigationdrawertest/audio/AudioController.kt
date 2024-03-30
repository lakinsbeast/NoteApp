package code.with.me.testroomandnavigationdrawertest.audio

import android.app.Activity
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import code.with.me.testroomandnavigationdrawertest.data.utils.println
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.AudioPlayerState
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

class AudioController @Inject constructor() {
    // TODO перенести на exoplayer, много проблем с MediaPlayer
    lateinit var player: MediaPlayer
    private lateinit var recorder: MediaRecorder
    private lateinit var countDownTimer: CountDownTimer

    //    val _audioPlaybackStateLiveData = MutableStateFlow<AudioPlayerState>(AudioPlayerState)
    val audioPlaybackStateLiveData = MutableLiveData<AudioPlayerState>()

    fun startPlaying(audioPath: String) {
        player = MediaPlayer().apply {
            try {
                setDataSource(audioPath)
                prepare()
                start()
                audioPlaybackStateLiveData.postValue(AudioPlayerState.Playing)
                setOnCompletionListener {
                    audioPlaybackStateLiveData.postValue(AudioPlayerState.Completed)
                }
                setOnErrorListener { mp, what, extra ->
                    audioPlaybackStateLiveData.postValue(AudioPlayerState.Error("Упс, что-то сломалось"))
                    true
                }
            } catch (e: IOException) {
                "prepare() failed".println()
                audioPlaybackStateLiveData.postValue(AudioPlayerState.Error(e.localizedMessage))
            }
        }
    }

    fun continuePlaying() {
        try {
            player.let {
                it.start()
                audioPlaybackStateLiveData.postValue(AudioPlayerState.Playing)
            }
        } catch (e: IOException) {
            "continue mediaPlayer failed".println()
            audioPlaybackStateLiveData.postValue(AudioPlayerState.Error(e.localizedMessage))
        }
    }

    fun initPlayer(audioPath: String) {
        player = MediaPlayer().apply {
            try {
                setDataSource(audioPath)
                prepare()
            } catch (e: IOException) {
                "prepare() failed".println()
                audioPlaybackStateLiveData.postValue(AudioPlayerState.Error(e.localizedMessage))
            }
        }
    }

    fun getAudioPath(activity: Activity): String {
        return "${activity.externalCacheDir?.absolutePath}/audiorecordtest.3gp"
    }

    fun getVolume() = recorder.maxAmplitude

    fun startRecording(activity: Activity, scaleCallback: ((Float) -> Unit)) {
        if (isAudioRecording()) {
            recorder.stop()
            countDownTimer.cancel()
        }
        setRecorder(activity)
        setAndStartRecorder(scaleCallback)
    }

    private inline fun setAndStartRecorder(crossinline scaleCallback: ((Float) -> Unit)) {
        countDownTimer = object : CountDownTimer(60_000, 100) {
            override fun onTick(p0: Long) {
                val volume = getVolume()
                val scale = min(8.0, volume / MAX_RECORD_AMPLITUDE + 1.0).toFloat()
                scaleCallback.invoke(scale)
            }

            override fun onFinish() {
            }
        }.apply {
            start()
        }
    }

    private fun setRecorder(activity: Activity) {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity.let { mainActivity ->
                MediaRecorder(mainActivity).apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(getAudioPath(mainActivity))
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                    try {
                        prepare()
                    } catch (e: IOException) {
                        "prepare() failed".println()
                        audioPlaybackStateLiveData.postValue(AudioPlayerState.Error(e.localizedMessage))
                    }

                    start()
                }
            }
        } else {
            activity.let { mainActivity ->
                MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(getAudioPath(mainActivity))
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                    try {
                        prepare()
                    } catch (e: IOException) {
                        "prepare() failed".println()
                        audioPlaybackStateLiveData.postValue(AudioPlayerState.Error(e.localizedMessage))
                    }

                    start()
                }
            }
        }
    }

    private fun isAudioRecording() = ::recorder.isInitialized

    fun isAudioPlaying() = if (!::player.isInitialized) false else player.isPlaying

    fun stopRecording() {
        recorder.apply {
            stop()
            reset()
            release()
        }
        countDownTimer.cancel()
    }

    fun pausePlaying() {
        player.pause()
        audioPlaybackStateLiveData.postValue(AudioPlayerState.Paused)
    }

    fun stopPlaying() {
        player.release()
    }

    companion object {
        private const val MAX_RECORD_AMPLITUDE = 32768.0
        const val VOLUME_UPDATE_DURATION = 100L
    }
}
