package code.with.me.testroomandnavigationdrawertest.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.data.DataClassAdapter
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import kotlinx.coroutines.launch


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

    private val noteViewModel: NoteViewModel by viewModels {
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
        with(supportActionBar) {
            this?.setHomeAsUpIndicator(R.drawable.search48px)
            this?.setDisplayHomeAsUpEnabled(true)
            this?.setDisplayShowTitleEnabled(false)
            title = "SimpleNote"
            this?.elevation = 32F
        }

        val dbca = AnimationUtils.loadAnimation(this, R.anim.detail_background_color_anim)
        binding.welcomeLayout.startAnimation(dbca)


        val adapter = DatabaseRVAdapter(
            DataClassAdapter(
                {
                    openDetailFragment(it)
                },
                titlesList,
                textList,
                cameraInRecycler,
                audioInRecycler,
                drawInRecycler,
                imageInRecycler,
                pickedColorInRecycler
            )
        )

        binding.fab.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))

        }

        binding.recyc.scheduleLayoutAnimation()
        binding.recyc.layoutManager = LinearLayoutManager(this)
        binding.recyc.itemAnimator = null
        binding.recyc.adapter = adapter
//        itemTouchHelper.attachToRecyclerView(binding.recyc)

        lifecycleScope.launch{
            noteViewModel.getAll().collect {
                if (it.isNotEmpty()) {
                    it.forEach {
                        idsList.add(it.id)
                        titlesList.add(it.titleNote)
                        textList.add(it.textNote)
                        cameraInRecycler.add(it.imageById)
                        audioInRecycler.add(it.audioUrl)
                        drawInRecycler.add(it.paintUrl)
                        imageInRecycler.add(it.imgFrmGlrUrl)
                        pickedColorInRecycler.add(it.colorCard)
                        if (idsList.isEmpty()) {
                            Log.d("itemcount", binding.recyc.adapter?.itemCount.toString())
                            with(binding) {
                                welcomeTitle.visibility = View.VISIBLE
                                welcomeText.visibility = View.VISIBLE
                                addnewnote.visibility = View.VISIBLE
                                importnotes.visibility = View.VISIBLE
                                welcomeImage.visibility = View.VISIBLE
                            }
                        } else {
                            with(binding) {
                                welcomeTitle.visibility = View.INVISIBLE
                                welcomeText.visibility = View.INVISIBLE
                                addnewnote.visibility = View.INVISIBLE
                                importnotes.visibility = View.INVISIBLE
                                welcomeImage.visibility = View.INVISIBLE
                            }
                        }
                        binding.recyc.adapter?.notifyDataSetChanged()
                        binding.recyc.scheduleLayoutAnimation()
                    }
                }
            }
        }
        binding.addnewnote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun openDetailFragment(id: Int) {
        binding.relativeLayout.visibility = View.INVISIBLE
        binding.toolbar.visibility = View.INVISIBLE
        val bundle = Bundle()
        bundle.putInt("num", id)
        val fragment = DetailFragment()
        fragment.arguments = bundle
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_detail, fragment)
            .addToBackStack(null)
            .commit()
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
        return super.onOptionsItemSelected(item)
    }
}