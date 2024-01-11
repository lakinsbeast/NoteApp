package code.with.me.testroomandnavigationdrawertest.audio

import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Build
import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import code.with.me.testroomandnavigationdrawertest.data.Utils.println
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.AudioPlayerState
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min

/**
 * Необходимо присваивать activity, но думаю переделать и везде, где нужен активити передавать
 * активити в параметры
 **/
class AudioController
    @Inject
    constructor() {
        // TODO перенести на exoplayer, много проблем с MediaPlayer

        var player: MediaPlayer? = null
        private var recorder: MediaRecorder? = null
        var activity: MainActivity? = null
        private var countDownTimer: CountDownTimer? = null

        val audioPlaybackStateLiveData = MutableLiveData<AudioPlayerState>()

        fun startPlaying(audioPath: String) {
            player =
                MediaPlayer().apply {
                    try {
                        setDataSource(audioPath)
                        prepare()
                        start()
                        audioPlaybackStateLiveData.postValue(AudioPlayerState.Playing)
                    } catch (e: IOException) {
                        "prepare() failed".println()
                        audioPlaybackStateLiveData.postValue(AudioPlayerState.Error(e.localizedMessage))
                    }
                }.apply {
                    this.setOnCompletionListener {
                        audioPlaybackStateLiveData.postValue(AudioPlayerState.Completed)
                    }
                    this.setOnErrorListener { mp, what, extra ->
                        audioPlaybackStateLiveData.postValue(AudioPlayerState.Error("Упс, что-то сломалось"))
                        true
                    }
                }
        }

        fun continuePlaying() {
            try {
                player?.let {
                    it.start()
                    audioPlaybackStateLiveData.postValue(AudioPlayerState.Playing)
                }
            } catch (e: IOException) {
                "continue mediaPlayer failed".println()
                audioPlaybackStateLiveData.postValue(AudioPlayerState.Error(e.localizedMessage))
            }
        }

        fun initPlayer(audioPath: String) {
            player =
                MediaPlayer().apply {
                    try {
                        setDataSource(audioPath)
                        prepare()
                    } catch (e: IOException) {
                        "prepare() failed".println()
                        audioPlaybackStateLiveData.postValue(AudioPlayerState.Error(e.localizedMessage))
                    }
                }
        }

        fun getAudioPath(activity: MainActivity): String {
            val fileName = "${activity.externalCacheDir?.absolutePath}/audiorecordtest.3gp"
            return fileName
        }

        fun getVolume() = recorder?.maxAmplitude ?: 0

        fun startRecording(scaleCallback: ((Float) -> Unit)) {
            if (isAudioRecording()) {
                recorder?.stop()
                countDownTimer?.cancel()
            }
            setRecorder()
            setAndStartRecorder(scaleCallback)
        }

        private inline fun setAndStartRecorder(crossinline scaleCallback: ((Float) -> Unit)) {
            countDownTimer =
                object : CountDownTimer(60_000, 100) {
                    override fun onTick(p0: Long) {
                        val volume = getVolume()
//                "Volume = $volume".println()
                        val scale = min(8.0, volume / MAX_RECORD_AMPLITUDE + 1.0).toFloat()
                        scaleCallback.invoke(scale)
                    }

                    override fun onFinish() {
                    }
                }.apply {
                    start()
                }
        }

        private fun setRecorder() {
            recorder =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    activity?.let { mainActivity ->
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
                    activity?.let { mainActivity ->
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

        fun isAudioRecording() = recorder != null

        fun isAudioPlaying() = player?.isPlaying == true

        fun stopRecording() {
            recorder?.apply {
                stop()
                reset()
                release()
            }
            countDownTimer?.cancel()
            recorder = null
        }

        fun pausePlaying() {
            player?.pause()
            audioPlaybackStateLiveData.postValue(AudioPlayerState.Paused)
        }

        fun stopPlaying() {
            player?.release()
            player = null
//        audioPlaybackStateLiveData.postValue(AudioPlayerState.Completed)
        }

        companion object {
            private const val MAX_RECORD_AMPLITUDE = 32768.0
            const val VOLUME_UPDATE_DURATION = 100L
        }
    }
