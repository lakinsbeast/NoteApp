package code.with.me.testroomandnavigationdrawertest.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.drawToBitmap
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityPaintBinding
import java.io.ByteArrayOutputStream

@Suppress("DEPRECATION")
class PaintActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPaintBinding

    private var isClicked: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            rbtngreen.isChecked = true
            paintCanvas.setStrokeColor(Color.GREEN)
            paintCanvas.setStrokeWidth(20f)
            paintCanvas.canUserDraw(true)

            colorswtchbtn.setOnClickListener {
                if (!isClicked) {
                    with(binding) {
                        rbtngray.visibility = View.VISIBLE
                        rbtngreen.visibility = View.VISIBLE
                        rbtnlightblue.visibility = View.VISIBLE
                        rbtnred.visibility = View.VISIBLE
                    }
                    isClicked = true
                } else {
                    with(binding) {
                        rbtngray.visibility = View.INVISIBLE
                        rbtngreen.visibility = View.INVISIBLE
                        rbtnlightblue.visibility = View.INVISIBLE
                        rbtnred.visibility = View.INVISIBLE
                    }
                    isClicked = false
                }
            }
            rbtnred.setOnClickListener {
                paintCanvas.setStrokeColor(Color.RED)
                rbtnred.isChecked = true
                rbtngray.isChecked = false
                rbtngreen.isChecked = false
                rbtnlightblue.isChecked = false
            }
            rbtnlightblue.setOnClickListener {
                paintCanvas.setStrokeColor(Color.BLUE)
                rbtnred.isChecked = false
                rbtngray.isChecked = false
                rbtngreen.isChecked = false
                rbtnlightblue.isChecked = true
            }
            rbtngreen.setOnClickListener {
                paintCanvas.setStrokeColor(Color.GREEN)
                rbtnred.isChecked = false
                rbtngray.isChecked = false
                rbtngreen.isChecked = true
                rbtnlightblue.isChecked = false
            }
            rbtngray.setOnClickListener {
                paintCanvas.setStrokeColor(Color.GRAY)
                rbtnred.isChecked = false
                rbtngray.isChecked = true
                rbtngreen.isChecked = false
                rbtnlightblue.isChecked = false
            }

            clearBtn.setOnClickListener {
                paintCanvas.clearCanvas()
            }
            eraseBtn.setOnClickListener {
                paintCanvas.enableErasing()
            }
            undoBtn.setOnClickListener {
                paintCanvas.undoMove()
            }
            paintBtn.setOnClickListener {
                paintCanvas.setStrokeColor(Color.GREEN)
                paintCanvas.setStrokeWidth(20f)
            }

            saveBtn.setOnClickListener {
                val bitmap: Bitmap = binding.paintCanvas.drawToBitmap()
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, stream)
                val path: String = MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    "Title",
                    null
                )
                val intent = Intent(this@PaintActivity, AddNoteActivity::class.java)
                intent.putExtra("pathBitmap", path)
                setResult(RESULT_OK, intent)
                finish()
            }
        }

    }
}