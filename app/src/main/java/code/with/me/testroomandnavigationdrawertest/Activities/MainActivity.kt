package code.with.me.testroomandnavigationdrawertest.Activities

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionInflater
import android.util.DisplayMetrics
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding


    private val idsList = ArrayList<Int>()
    private val titlesList = ArrayList<String>()
    private val textList = ArrayList<String>()
    private var audioInRecycler = ArrayList<String>()
    private var cameraInRecycler = ArrayList<String>()
    private var drawInRecycler = ArrayList<String>()
    private var imageInRecycler = ArrayList<String>()
    private var pickedColorInRecycler = ArrayList<String>()

    private lateinit var appSettingPrefs: SharedPreferences
    private lateinit var sharedPrefsEdit: SharedPreferences.Editor
    private var isNightModeOn: Boolean = false

    val noteViewModel: NoteViewModel by viewModels {
        NoteViewModelFactory((application as NotesApplication).repo)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0)
        sharedPrefsEdit = appSettingPrefs.edit()
        isNightModeOn = appSettingPrefs.getBoolean("NightMode", false)
        if (isNightModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.search48px)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.title = "SimpleNote"
        supportActionBar?.elevation = 32F


        val dbca = AnimationUtils.loadAnimation(this, R.anim.detail_background_color_anim)
        binding.welcomeLayout.startAnimation(dbca)



        val adapter = RecyclerViewAdapter({
            openFragment(it)
//            val intent = Intent(this, DetailActivity::class.java)
//            intent.putExtra("id", it)
//            startActivity(intent)
        },titlesList, textList, cameraInRecycler, audioInRecycler, drawInRecycler, imageInRecycler, pickedColorInRecycler)

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        binding.recyc.scheduleLayoutAnimation()
        binding.recyc.layoutManager = LinearLayoutManager(this)
        binding.recyc.itemAnimator = null
        binding.recyc.adapter = adapter
//        itemTouchHelper.attachToRecyclerView(binding.recyc)

        noteViewModel.allNotes.observe(this) {
            it.forEach { i ->
                if (!(i.id in idsList && i.titleNote in titlesList && i.textNote in textList && i.audioUrl in audioInRecycler
                            && i.imageById in cameraInRecycler && i.paintUrl in
                            drawInRecycler && i.imgFrmGlrUrl in imageInRecycler && i.colorCard in pickedColorInRecycler)) {
                    idsList.add(i.id)
                    titlesList.add(i.titleNote)
                    textList.add(i.textNote)
                    cameraInRecycler.add(i.imageById)
                    audioInRecycler.add(i.audioUrl)
                    drawInRecycler.add(i.paintUrl)
                    imageInRecycler.add(i.imgFrmGlrUrl)
                    pickedColorInRecycler.add(i.colorCard)
                    if (idsList.isEmpty()) {
                        Log.d("itemcount", binding.recyc.adapter?.itemCount.toString())
                        with (binding) {
                            welcomeTitle.visibility = View.VISIBLE
                            welcomeText.visibility = View.VISIBLE
                            addnewnote.visibility = View.VISIBLE
                            importnotes.visibility = View.VISIBLE
                            welcomeImage.visibility = View.VISIBLE
                        }
                    } else {
                        with (binding) {
                            welcomeTitle.visibility = View.INVISIBLE
                            welcomeText.visibility = View.INVISIBLE
                            addnewnote.visibility = View.INVISIBLE
                            importnotes.visibility = View.INVISIBLE
                            welcomeImage.visibility = View.INVISIBLE
                        }
                    }
                }
            }
            binding.recyc.adapter?.notifyDataSetChanged()
            binding.recyc.scheduleLayoutAnimation()
        }
        binding.addnewnote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
    }

//    private val itemTouchHelper by lazy {
//        val simpleItemTouchCallback = object: ItemTouchHelper.Callback() {
//            // Используется для установки направления перетаскивания и скольжения
//            override fun getMovementFlags(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder
//            ): Int {
//                // Устанавливаем направление, в котором разрешено перетаскивать элемент, линейный макет имеет 2 направления
//                val dragFlags = UP or DOWN or START or END
//                // Устанавливаем направление скольжения с двух сторон
//                val swipeFlags = START
//                return makeMovementFlags(0, 0)
//            }
//
//            override fun onMove(
//                recyclerView: RecyclerView,
//                viewHolder: RecyclerView.ViewHolder,
//                target: RecyclerView.ViewHolder
//            ): Boolean {
//                return false
//            }
//            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
////                if (direction == START) {
////                    val pos = viewHolder.adapterPosition
////                    val noteToDel = Note(idsList[pos], titlesList[pos], textList[pos], cameraInRecycler[pos],
////                        audioInRecycler[pos], drawInRecycler[pos], imageInRecycler[pos], )
//////                    noteViewModel.delete(noteToDel)
////                    GlobalScope.launch(Dispatchers.IO) {
////                        noteViewModel.delete(noteToDel)
////                    }
////                    val fileImage = DocumentFile.fromSingleUri(this@MainActivity, Uri.parse(cameraInRecycler[pos]))
////                    val filePaint = DocumentFile.fromSingleUri(this@MainActivity, Uri.parse(drawInRecycler[pos]))
////                    if (fileImage != null && cameraInRecycler[pos].isNotEmpty()) {
////                        GlobalScope.launch(Dispatchers.IO) {
////                            contentResolver.delete(Uri.parse(cameraInRecycler[pos]), null, null)
////                        }
////                    }
////                    if (filePaint != null && drawInRecycler[pos].isNotEmpty()) {
////                        GlobalScope.launch(Dispatchers.IO) {
////                            contentResolver.delete(Uri.parse(drawInRecycler[pos]), null, null)
////                        }
////                    }
////
////
//////                    binding.recyc.scheduleLayoutAnimation()
//////                    val snackbar: Snackbar = Snackbar.make(binding.activitytorefresh, "Восстановить?", Snackbar.LENGTH_LONG)
//////                    snackbar.setAction("Да") {
//////                        noteViewModel.insert(noteToDel)
//////                        binding.recyc.adapter?.notifyItemInserted(noteToDel.id)
//////                        Toast.makeText(this@MainActivity, "Типо восстановил", Toast.LENGTH_SHORT)
//////                            .show()
//////                    }
//////                    snackbar.show()
////
////                }
//            }
//
////            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder,
////                                     dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean
////            ) {
////                val icon =
////                    AppCompatResources.getDrawable(this@MainActivity, R.drawable.delete_icon)!!.toBitmap()
////                val itemView = viewHolder.itemView
////                val red = Paint()
////                red.setARGB(255, 255, 0, 0)
////                val green = Paint()
////                green.setARGB(255, 0, 255, 0)
////                val white = Paint()
////                white.setARGB(255,255,255,255)
////
////                if (dX < 0) {
////                    c.drawRect(
////                        itemView.left.toFloat(), itemView.top.toFloat(),
////                        itemView.right.toFloat(), itemView.bottom.toFloat(), red)
////                    c.drawBitmap(icon,itemView.right.toFloat() + convertDpToPx(-16) - icon.width, itemView.top.toFloat() +
////                            (itemView.bottom.toFloat() - itemView.top.toFloat() - icon.height)/2, red)
////                }
////                val alpha = ALPHA_FULL - abs(dX) / viewHolder.itemView.width.toFloat()
////                viewHolder.itemView.alpha = alpha
////                viewHolder.itemView.translationX = dX
////
////                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive
////                )
////            }
//        }
//        ItemTouchHelper(simpleItemTouchCallback)
//    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun openFragment(id: Int) {
        binding.relativeLayout.visibility = View.INVISIBLE
        binding.toolbar.visibility = View.INVISIBLE
        val bundle = Bundle()
        bundle.putInt("num", id)
        val fragment =  DetailFragment()
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_detail, fragment)
            .addToBackStack(null)
            .commit()
    }

    private fun convertDpToPx(dp: Int): Int {
        return Math.round(dp * (resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.mainactivitymenu, menu)
        if (isNightModeOn) {
            menu?.getItem(0)?.icon =
                ContextCompat
                    .getDrawable(this, R.drawable.light_mode48px)
        } else {
            menu?.getItem(0)?.icon =
                ContextCompat
                    .getDrawable(this, R.drawable.dark_mode48px)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 0) {
            this.finish()
        } else {
            binding.relativeLayout.visibility = View.VISIBLE
            binding.toolbar.visibility = View.VISIBLE
            super.onBackPressed()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.nightMode) {
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                sharedPrefsEdit.putBoolean("NightMode", false)
                sharedPrefsEdit.apply()
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                sharedPrefsEdit.putBoolean("NightMode", true)
                sharedPrefsEdit.apply()
            }
        }
//        if (item.itemId == R.id.dayMode) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }
        return super.onOptionsItemSelected(item)
    }
}