package code.with.me.testroomandnavigationdrawertest.Activities

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Resources.getSystem
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import com.google.android.material.color.MaterialColors.ALPHA_FULL
import kotlinx.coroutines.launch
import kotlin.math.abs


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val idsList = ArrayList<Int>()
    private val titlesList = ArrayList<String>()
    private val textList = ArrayList<String>()
    private var audioInRecycler = ArrayList<String>()
    private var cameraInRecycler = ArrayList<String>()
    private var drawInRecycler = ArrayList<String>()
    private var imageInRecycler = ArrayList<String>()

    val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NotesApplication).repo)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "SimpleNote"


        val adapter = RecyclerViewAdapter({
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("id", it)
            startActivity(intent)
        },titlesList, textList, audioInRecycler, cameraInRecycler, drawInRecycler, imageInRecycler)



        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        binding.recyc.layoutManager = LinearLayoutManager(this)
        binding.recyc.adapter = adapter
        itemTouchHelper.attachToRecyclerView(binding.recyc)

        noteViewModel.allNotes.observe(this) {
            it.forEach { i ->
                if (!(i.id in idsList && i.titleNote in titlesList && i.textNote in textList && i.audioUrl in audioInRecycler
                            && i.imageById in cameraInRecycler && i.paintUrl in
                            drawInRecycler && i.imgFrmGlrUrl in imageInRecycler)) {
                    idsList.add(i.id)
                    titlesList.add(i.titleNote)
                    textList.add(i.textNote)
                    audioInRecycler.add(i.audioUrl)
                    cameraInRecycler.add(i.imageById)
                    drawInRecycler.add(i.paintUrl)
                    imageInRecycler.add(i.imgFrmGlrUrl)
                }
                adapter.notifyDataSetChanged()

            }

//            for (index in idsList.indices) {
////                        binding.recyc.adapter?.notifyItemInserted(index)
//                binding.recyc.adapter?.notifyItemRangeChanged(0,idsList.size)
//                Log.d( "itemCount", binding.recyc.adapter?.itemCount.toString())
//            }
        }
    }
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object: ItemTouchHelper.Callback() {
            // Используется для установки направления перетаскивания и скольжения
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                // Устанавливаем направление, в котором разрешено перетаскивать элемент, линейный макет имеет 2 направления
                val dragFlags = UP or DOWN
                // Устанавливаем направление скольжения с двух сторон
                val swipeFlags = START
                return makeMovementFlags(0, swipeFlags)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                if (direction == START) {
                    val pos = viewHolder.adapterPosition
                    val noteToDel = Note(idsList[pos], titlesList[pos], textList[pos], cameraInRecycler[pos],
                        audioInRecycler[pos], drawInRecycler[pos], imageInRecycler[pos])
                    noteViewModel.delete(noteToDel)
                    val fileImage = DocumentFile.fromSingleUri(this@MainActivity, Uri.parse(cameraInRecycler[pos]))
                    val filePaint = DocumentFile.fromSingleUri(this@MainActivity, Uri.parse(drawInRecycler[pos]))
                    if (fileImage != null && cameraInRecycler[pos].isNotEmpty()) {
                        Log.d("testUrl", cameraInRecycler[pos])
                        contentResolver.delete(Uri.parse(cameraInRecycler[pos]), null, null)
                    }
                    if (filePaint != null && drawInRecycler[pos].isNotEmpty()) {
                        contentResolver.delete(Uri.parse(drawInRecycler[pos]), null, null)
                    }
                    Log.d("testDelete", idsList[pos].toString())
                    val intent = intent
                    finish()
                    startActivity(intent)
                }
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
                dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
            ) {
                val icon =
                    AppCompatResources.getDrawable(this@MainActivity, R.drawable.delete_icon)!!.toBitmap()
                val itemView = viewHolder.itemView
                val red = Paint()
                red.setARGB(255, 255, 0, 0)
                val green = Paint()
                green.setARGB(255, 0, 255, 0)
                val white = Paint()
                white.setARGB(255,255,255,255)

                if (dX < 0) {
                    c.drawRect(
                        itemView.left.toFloat(), itemView.top.toFloat(),
                        itemView.right.toFloat(), itemView.bottom.toFloat(), red)
                    c.drawBitmap(icon,itemView.right.toFloat() + convertDpToPx(-16) - icon.width, itemView.top.toFloat() +
                            (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height)/2, red)
                }
                val alpha = ALPHA_FULL - abs(dX) / viewHolder.itemView.width.toFloat()
                viewHolder.itemView.alpha = alpha
                viewHolder.itemView.translationX = dX

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
                )
            }
        }
        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun convertDpToPx(dp: Int): Int {
        return Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
}