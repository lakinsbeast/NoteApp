package code.with.me.testroomandnavigationdrawertest.ui

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityPaintBinding
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class PaintActivity : AppCompatActivity() {
    private var drawuri: Uri? = null


    private lateinit var binding: ActivityPaintBinding
    private var isClicked: Boolean = false
    private var brushSizeForUndo: Float = 20f
    private var colorBrush: Int = -99999999
    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            paintCanvas.setStrokeColor(Color.GREEN)
            paintCanvas.setStrokeWidth(20f)
            paintCanvas.canUserDraw(true)
            paintBtn.setBackgroundColor(R.drawable.roundcorners)
            colorswtchbtn.setOnClickListener {
                with(binding) {
                    ColorPickerDialogBuilder
                        .with(this@PaintActivity)
                        .initialColor(Color.WHITE)
                        .setTitle("Выбери цвет")
                        .density(15)
                        .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                        .setOnColorSelectedListener { selectedColor ->
                            Log.d("color", selectedColor.toString())
                            paintCanvas.setStrokeColor(selectedColor)
                            colorBrush = selectedColor
                        }
                        .setPositiveButton(
                            "ok"
                        ) { _, _, _ ->

                        }
                        .setNegativeButton(
                            "cancel"
                        ) { _, _ -> }
                        .build()
                        .show()

                }
            }


            clearBtn.setOnClickListener {
                val alrtDlg = AlertDialog.Builder(this@PaintActivity)
                alrtDlg.setTitle("Хотите очистить холст?")
                    .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                        paintCanvas.clearCanvas()
                    }.setNegativeButton("Нет") { _: DialogInterface, _: Int ->
                        Toast.makeText(this@PaintActivity, "Вы выбрали  \"нет\" ", Toast.LENGTH_SHORT).show()
                    }.show()
            }
            binding.strokeWidth.setOnClickListener {
                if (!isClicked) {
                    binding.brushLayout.visibility = View.VISIBLE
                    binding.seekBar.setOnSeekBarChangeListener(
                        object : SeekBar.OnSeekBarChangeListener {
                            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean
                            ) {
                                paintCanvas.setStrokeWidth(progress.toFloat())
                                binding.brushSize.requestLayout()
                                brushSizeForUndo = progress.toFloat()
                                binding.brushSize.layoutParams.width = progress.toFloat().toInt() + 5
                                binding.brushSize.layoutParams.height = progress.toFloat().toInt() + 5
                            }

                            override fun onStartTrackingTouch(seekBar: SeekBar?) {

                            }

                            override fun onStopTrackingTouch(seekBar: SeekBar?) {

                            }

                        }
                    )
                    isClicked = true
                } else {
                    binding.brushLayout.visibility = View.INVISIBLE
                    isClicked = false
                }


            }
            eraseBtn.setOnClickListener {
                paintBtn.setBackgroundColor(com.google.android.material.R.attr.selectableItemBackgroundBorderless)
                eraseBtn.setBackgroundColor(R.drawable.roundcorners)
                paintCanvas.enableErasing()
            }
            undoBtn.setOnClickListener {
                paintCanvas.undoMove()
                paintCanvas.setStrokeWidth(brushSizeForUndo)
            }
            paintBtn.setOnClickListener {
                eraseBtn.setBackgroundColor(com.google.android.material.R.attr.selectableItemBackgroundBorderless)
                paintBtn.setBackgroundColor(R.drawable.roundcorners)
                paintCanvas.setStrokeWidth(brushSizeForUndo)
                if (colorBrush == -99999999) {
                    paintCanvas.setStrokeColor(Color.GREEN)
                } else {
                    paintCanvas.setStrokeColor(colorBrush)
                }

            }

            saveBtn.setOnClickListener {
                val bitmap: Bitmap = binding.paintCanvas.drawToBitmap()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, ByteArrayOutputStream())

                val timeStamp = SimpleDateFormat("yyyyMMddHHmmSS").format(Date())
                val storageDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "NotesPhotos")
                storageDir.mkdir()
                val imageFile = File(storageDir, "draw".plus(Calendar.getInstance().timeInMillis).plus(timeStamp).plus(".jpg"))
                imageFile.createNewFile()
                val f0ut = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, f0ut)
                f0ut.flush()
                f0ut.close()
                imageFile.createNewFile()
                if (!imageFile.parentFile?.exists()!!) {
                    imageFile.parentFile?.mkdirs()
                }
                if (!imageFile.exists()) {
                    imageFile.mkdirs()
                }
                drawuri = FileProvider.getUriForFile(this@PaintActivity, "code.with.me.testroomandnavigationdrawertest.ui.AddNoteActivity.provider", imageFile)

                val intent = Intent(this@PaintActivity, AddNoteActivity::class.java)
                intent.putExtra("pathBitmap", drawuri.toString())
                setResult(RESULT_OK, intent)
                finish()
            }
        }

    }

}