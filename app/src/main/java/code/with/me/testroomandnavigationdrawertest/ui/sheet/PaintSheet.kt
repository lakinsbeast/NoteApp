package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.drawToBitmap
import androidx.fragment.app.setFragmentResult
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityPaintBinding
import code.with.me.testroomandnavigationdrawertest.file.FilesController
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MakeNoteFragment.Companion.paintResultKey
import com.flask.colorpicker.ColorPickerView
import com.flask.colorpicker.builder.ColorPickerDialogBuilder
import com.google.android.material.bottomsheet.BottomSheetBehavior
import java.io.FileOutputStream
import java.lang.Exception
import java.util.*
import javax.inject.Inject

class PaintSheet : BaseSheet<ActivityPaintBinding>(ActivityPaintBinding::inflate) {
    private lateinit var drawuri: Uri
    private var isClicked: Boolean = false
    private var brushSizeForUndo: Float = 20f
    private var colorBrush: Int = -99999999

    @Inject
    lateinit var filesController: FilesController

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        initAppComponent()
        super.onViewCreated(view, savedInstanceState)
        setUpClickListeners()
        setUpPaint()
        setUpBottomSheetBehavior()
    }

    private fun setUpBottomSheetBehavior() {
        behavior?.state = BottomSheetBehavior.STATE_EXPANDED
        behavior?.isDraggable = false
    }

    private fun initAppComponent() {
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
    }

    private fun setUpClickListeners() {
        setUpColorSwitch()
        setUpClearButton()
        setUpStrokeWidth()
        setUpEraseButton()
        setUpUndoButton()
        setUpPaintButton()
        setUpSaveButton()
        binding.paintCanvas.setOnTouchListener { v, event ->
            println("binding.root touched")
            behavior?.isDraggable = false
            false
        }
        binding.layout.setOnTouchListener { v, event ->
            println("binding.layout touched")
            behavior?.isDraggable = true
            true
        }
    }

    private fun setUpSaveButton() {
        binding.saveBtn.setOnClickListener {
            try {
                val bitmap: Bitmap = binding.paintCanvas.drawToBitmap()

                val file =
                    filesController.saveToInternalStorage(
                        requireContext(),
                        "draw_${Calendar.getInstance().timeInMillis}_${
                            SimpleDateFormat(
                                "yyyyMMddHHmmSS",
                                Locale.getDefault(),
                            ).format(Date())
                        }.jpg",
                    )
                FileOutputStream(file).use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
                    outputStream.flush()
                }
                file?.let {
                    drawuri =
                        filesController.getUriForFile(
                            requireContext(),
                            file,
                        )
                } ?: run {
                    println("file is null")
                    return@setOnClickListener
                }

                val resultIntent =
                    Bundle().apply {
                        this.putString("pathBitmap", drawuri.toString())
                    }
                setFragmentResult(paintResultKey, resultIntent)
                dismiss()
            } catch (e: Exception) {
                println("Error in ${javaClass.simpleName} error is $e")
                Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                e.printStackTrace()
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
                binding.seekBar.setOnSeekBarChangeListener(
                    object :
                        SeekBar.OnSeekBarChangeListener {
                        override fun onProgressChanged(
                            seekBar: SeekBar?,
                            progress: Int,
                            fromUser: Boolean,
                        ) {
                            binding.paintCanvas.setStrokeWidth(progress.toFloat())
                            binding.brushSize.requestLayout()
                            brushSizeForUndo = progress.toFloat()
                            binding.brushSize.layoutParams.width = progress.toFloat().toInt() + 5
                            binding.brushSize.layoutParams.height = progress.toFloat().toInt() + 5
                        }

                        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

                        override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                    },
                )
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
                        Toast.LENGTH_SHORT,
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
