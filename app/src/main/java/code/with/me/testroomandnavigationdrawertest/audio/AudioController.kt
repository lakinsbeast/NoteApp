package code.with.me.testroomandnavigationdrawertest.audio

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.os.CountDownTimer
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import code.with.me.testroomandnavigationdrawertest.Utils.println
import code.with.me.testroomandnavigationdrawertest.ui.MainActivity
import java.io.FileDescriptor
import java.io.IOException
import javax.inject.Inject
import kotlin.math.min


class AudioController @Inject constructor() {

    private var player: MediaPlayer? = null
    private var recorder: MediaRecorder? = null
    var activity: MainActivity? = null
    private var countDownTimer: CountDownTimer? = null

    private var time = System.currentTimeMillis()

    private var requestAudioPermission =
        activity?.registerForActivityResult(ActivityResultContracts.RequestPermission()) { it ->
            if (it) {
                startRecording()
            }
        }

    var audiouri: Uri? = null

    fun startPlaying() {
        player = MediaPlayer().apply {
            try {
                //TODO do with this
                setDataSource(getAudioPath())
                prepare()
                start()
            } catch (e: IOException) {
                "prepare() failed".println()
            }
        }
    }

    private fun checkPermission(): Boolean {
        activity?.let { mainActivity ->
            if (ActivityCompat.checkSelfPermission(
                    mainActivity,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            } else {
                requestAudioPermission?.launch(
                    Manifest.permission.RECORD_AUDIO
                )
                return false
            }
        }
        return false
    }

    fun getAudioPath(): FileDescriptor? {
        val values = ContentValues(4)
        values.put(MediaStore.Audio.Media.TITLE, System.currentTimeMillis())
        values.put(MediaStore.Audio.Media.DATE_ADDED, (System.currentTimeMillis() / 1000).toInt())
        values.put(MediaStore.Audio.Media.MIME_TYPE, "audio/mp3")
        values.put(MediaStore.Audio.Media.RELATIVE_PATH, "Music/Recordings/")

        audiouri =
            activity?.contentResolver?.insert(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, values);
        var file = audiouri?.let { activity?.contentResolver?.openFileDescriptor(it, "w") }
        return file?.fileDescriptor
    }

    fun getVolume() = recorder?.maxAmplitude ?: 0


    fun startRecording(scaleCallback: ((Float) -> Unit)? = null) {
        if (!checkPermission()) return
        if (isAudioRecording()) {
            recorder?.stop()
            countDownTimer?.cancel()
        }
        setRecorder()
        setAndStartRecorder(scaleCallback)
    }

    private fun setAndStartRecorder(scaleCallback: ((Float) -> Unit)?) {
        countDownTimer = object : CountDownTimer(60_000, 100) {
            override fun onTick(p0: Long) {
                val volume = getVolume()
//                "Volume = $volume".println()
                val scale = min(8.0, volume / MAX_RECORD_AMPLITUDE + 1.0).toFloat()
                scaleCallback?.invoke(scale)
            }

            override fun onFinish() {
            }
        }.apply {
            start()
        }
    }

    private fun setRecorder() {
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            activity?.let { mainActivity ->
                MediaRecorder(mainActivity).apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                    setOutputFile(getAudioPath())
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                    try {
                        prepare()
                    } catch (e: IOException) {
                        "prepare() failed".println()
                    }

                    start()
                }
            }
        } else {
            MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                setOutputFile(getAudioPath())
                setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)

                try {
                    prepare()
                } catch (e: IOException) {
                    "prepare() failed".println()
                }

                start()
            }
        }
    }

    fun isAudioRecording() = recorder != null


    fun stopRecording() {
        recorder?.apply {
            stop()
            release()
        }
        countDownTimer?.cancel()
        recorder = null
    }

    fun stopPlaying() {
        player?.release()
        player = null
    }


    companion object {
        private const val MAX_RECORD_AMPLITUDE = 32768.0
        const val VOLUME_UPDATE_DURATION = 100L
    }

}