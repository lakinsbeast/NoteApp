package code.with.me.testroomandnavigationdrawertest.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityImageToFullScreenBinding

class ImageToFullScreenActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageToFullScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageToFullScreenBinding.inflate(layoutInflater).apply {
            setContentView(this.root)
        }
        intent.getStringExtra("imageUrl").apply {
            binding.imageToFS.setImageURI(Uri.parse(this))
        }
    }
}