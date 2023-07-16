package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import code.with.me.testroomandnavigationdrawertest.databinding.AudioRecorderBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import java.io.IOException

class AudioRecorderSheet : BaseSheet<AudioRecorderBinding>(AudioRecorderBinding::inflate) {

    private var fileAudioName: String? = null

    private var recorder: MediaRecorder? = null
    private var audioInString: String = ""
    private var micSelected: (String) -> Unit = {}
    private val randomNum = System.currentTimeMillis() / 1000


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.circleBar?.setPlayer()
        audioRec()

    }

    private fun stopAudio() {
        try {
            activity?.let {
                recorder?.apply {
                    stop()
                    release()
                }
//                binding.imageButtonVoice.setImageResource(R.drawable.ic_baseline_keyboard_voice_24)
//                saveNote()
                Toast.makeText(it, "Запись голоса завершена", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.d("audioRecorder", e.message.toString())
        }
    }

    private fun audioRec() {
        activity?.let {
            if (ActivityCompat.checkSelfPermission(
                    it, Manifest.permission.RECORD_AUDIO
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    it, arrayOf(Manifest.permission.RECORD_AUDIO), Companion.RECORD_AUDIO
                )
            } else {
                fileAudioName = it.externalCacheDir?.absolutePath.toString() + "/$randomNum.3gp"
                audioInString = fileAudioName.toString()
                micSelected.invoke(audioInString)
                Log.d("filename", fileAudioName!!)

                recorder = MediaRecorder().apply {
                    setAudioSource(MediaRecorder.AudioSource.MIC)
                    setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                    setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                    setOutputFile(fileAudioName)
                    try {
                        prepare()
                        start()
//                        binding.imageButtonVoice.setImageResource(R.drawable.mic_off48px)
                        Toast.makeText(it, "Началась запись голоса", Toast.LENGTH_SHORT).show()
                    } catch (e: IOException) {
                        Log.d("audioException", e.message.toString())
                        Toast.makeText(it, e.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val RECORD_AUDIO: Int = 0
    }


}