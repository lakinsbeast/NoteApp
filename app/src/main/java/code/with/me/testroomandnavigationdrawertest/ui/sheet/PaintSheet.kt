package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.Utils.providerName
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityPaintBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.sheet.MakeANoteSheet.Companion.paintResultKey
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*


class PaintSheet : BaseSheet<ActivityPaintBinding>(ActivityPaintBinding::inflate) {
    private var drawuri: Uri? = null
    private var isClicked: Boolean = false
    private var brushSizeForUndo: Float = 20f
    private var colorBrush: Int = -99999999

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
        setUpPaint()
    }

    private fun setUpClickListeners() {
        setUpColorSwitch()
        setUpClearButton()
        setUpStrokeWidth()
        setUpEraseButton()
        setUpUndoButton()
        setUpPaintButton()
        setUpSaveButton()
    }

    private fun setUpSaveButton() {
        binding.saveBtn.setOnClickListener {
            try {
                val bitmap: Bitmap = binding.paintCanvas.drawToBitmap()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 60, ByteArrayOutputStream())
                val storageDir = File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),
                    "NotesPhotos"
                ).apply {
                    mkdir()
                }
                val imageFile = File(
                    storageDir,
                    "draw".plus(Calendar.getInstance().timeInMillis)
                        .plus(
                            SimpleDateFormat(
                                "yyyyMMddHHmmSS",
                                Locale.getDefault()
                            ).format(Date())
                        ).plus(".jpg")
                )
                imageFile.createNewFile()
                FileOutputStream(imageFile).apply {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, this)
                    flush()
                    close()
                }
                imageFile.createNewFile()
                if (!imageFile.parentFile?.exists()!!) {
                    imageFile.parentFile?.mkdirs()
                }
                if (!imageFile.exists()) {
                    imageFile.mkdirs()
                }
                drawuri = FileProvider.getUriForFile(
                    requireContext(),
                    providerName,
                    imageFile
                )
                val resultIntent = Bundle().apply {
                    this.putString("pathBitmap", drawuri.toString())
                }
                setFragmentResult(paintResultKey, resultIntent)
                findNavController().popBackStack()
            } catch (e: Exception) {
                println("Error in ${javaClass.simpleName} error is ${e}")
                Toast.makeText(context, "Ошибка: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                findNavController().popBackStack()
            }
        }
    }

    private fun setUpPaintButton() {
        binding.paintBtn.setOnClickListener {
            binding.eraseBtn.setBackgroundColor(com.google.android.material.R.attr.selectableItemBackgroundBorderless)
            binding.paintBtn.setBackgroundColor(R.drawable.roundcorners)
            binding.paintCanvas.setStrokeWidth(brushSizeForUndo)
            if (colorBrush == -99999999) {
                binding.paintCanvas.setStrokeColor(Color.GREEN)
            } else {
                binding.paintCanvas.setStrokeColor(colorBrush)
            }
        }
    }

    private fun setUpUndoButton() {
        binding.undoBtn.setOnClickListener {
            binding.paintCanvas.apply {
                undoMove()
                setStrokeWidth(brushSizeForUndo)
            }
        }
    }

    private fun setUpEraseButton() {
        binding.eraseBtn.setOnClickListener {
            binding.paintBtn.setBackgroundColor(com.google.android.material.R.attr.selectableItemBackgroundBorderless)
            binding.eraseBtn.setBackgroundColor(R.drawable.roundcorners)
            binding.paintCanvas.enableErasing()
        }
    }

    private fun setUpStrokeWidth() {
        binding.strokeWidth.setOnClickListener {
            if (!isClicked) {
                binding.brushLayout.visibility = View.VISIBLE
                binding.seekBar.setOnSeekBarChangeListener(object :
                    SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        seekBar: SeekBar?,
                        progress: Int,
                        fromUser: Boolean
                    ) {
                        binding.paintCanvas.setStrokeWidth(progress.toFloat())
                        binding.brushSize.requestLayout()
                        brushSizeForUndo = progress.toFloat()
                        binding.brushSize.layoutParams.width = progress.toFloat().toInt() + 5
                        binding.brushSize.layoutParams.height = progress.toFloat().toInt() + 5
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
                isClicked = true
            } else {
                binding.brushLayout.visibility = View.INVISIBLE
                isClicked = false
            }
        }
    }

    private fun setUpClearButton() {
        binding.clearBtn.setOnClickListener {
            AlertDialog.Builder(requireContext()).setTitle("Хотите очистить холст?")
                .setPositiveButton("Да") { _: DialogInterface, _: Int ->
                    binding.paintCanvas.clearCanvas()
                }.setNegativeButton("Нет") { _: DialogInterface, _: Int ->
                    Toast.makeText(
                        requireContext(),
                        "Вы выбрали  \"нет\"",
                        Toast.LENGTH_SHORT
                    ).show()
                }.show()
        }
    }

    private fun setUpColorSwitch() {
        binding.colorswtchbtn.setOnClickListener {
            ColorPickerDialogBuilder
                .with(requireContext())
                .initialColor(Color.WHITE)
                .setTitle("Выбери цвет")
                .density(15)
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .setOnColorSelectedListener { selectedColor ->
                    Log.d("color", selectedColor.toString())
                    binding.paintCanvas.setStrokeColor(selectedColor)
                    colorBrush = selectedColor
                }
                .setPositiveButton("ok") { _, _, _ ->

                }
                .setNegativeButton("cancel") { _, _ -> }
                .build()
                .show()
        }
    }

    private fun setUpPaint() {
        binding.paintCanvas.apply {
            setStrokeColor(Color.GREEN)
            setStrokeWidth(20f)
            canUserDraw(true)
        }
        binding.paintBtn.setBackgroundColor(R.drawable.roundcorners)

    }
}
