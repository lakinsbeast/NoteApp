package code.with.me.testroomandnavigationdrawertest.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.DataClassAdapter
import code.with.me.testroomandnavigationdrawertest.data.data_classes.Note
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import code.with.me.testroomandnavigationdrawertest.ui.dropbox.MasterDropboxSettingsActivity
import kotlinx.coroutines.launch
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val NotesArray: ArrayList<Note> = ArrayList()

    private lateinit var appSettingPrefs: SharedPreferences
    private lateinit var sharedPrefsEdit: SharedPreferences.Editor
    private var isNightModeOn: Boolean = false

    @Inject
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteViewModel: NoteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val appComponent = (application as NotesApplication).appComponent
        appComponent.inject(this)
        appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0).also {
            sharedPrefsEdit = it.edit()
            isNightModeOn = it.getBoolean("NightMode", false)
        }
//        sharedPrefsEdit = appSettingPrefs.edit()
//        isNightModeOn = appSettingPrefs.getBoolean("NightMode", false)

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

        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        AnimationUtils.loadAnimation(this, R.anim.detail_background_color_anim).apply {
            binding.welcomeLayout.startAnimation(this)
        }

        DatabaseRVAdapter(
            DataClassAdapter(
                {
                    openDetailFragment(it)
                },
                NotesArray
            )
        ).apply {
            binding.recyc.adapter = this
        }

        binding.apply {
            fab.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddNoteActivity::class.java))
            }
            recyc.apply {
                scheduleLayoutAnimation()
                layoutManager = LinearLayoutManager(this@MainActivity)
                itemAnimator = null
            }
        }

        binding.importnotes.setOnClickListener {
            startActivity(Intent(this@MainActivity, MasterDropboxSettingsActivity::class.java))
        }


        lifecycleScope.launch {
            noteViewModel.getAllNotes().collect {
                if (it.isNotEmpty()) {
                    NotesArray.clear()
                    it.forEach {
                        NotesArray.add(it)
                        binding.apply {
                            welcomeLayout.visibility = View.GONE
                        }
                        binding.recyc.adapter?.notifyDataSetChanged()
                        binding.recyc.scheduleLayoutAnimation()
                    }
                } else {
                    binding.apply {
                        welcomeLayout.visibility = View.VISIBLE
                    }
                }
            }
        }
        binding.addnewnote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }
        

    }

//    fun transformNoteToDataClassAdapter(
//        note: Note,
//        dataClassAdapter: DataClassAdapter, clickListener: (Int) -> Unit,
//    ): DataClassAdapter {
//        return DataClassAdapter(clickListener, note.titleNote, note.textNote)
//    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
    }

    private fun openDetailFragment(id: Int) {
        binding.apply {
            relativeLayout.visibility = View.INVISIBLE
            toolbar.visibility = View.INVISIBLE
        }
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_detail, DetailFragment().apply {
                arguments = Bundle().apply {
                    putInt("num", id)
                }
            }).addToBackStack(null).commit()
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