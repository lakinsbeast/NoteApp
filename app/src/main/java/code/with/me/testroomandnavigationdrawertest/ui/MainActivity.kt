package code.with.me.testroomandnavigationdrawertest.ui

import android.app.ActionBar
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navArgs
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.data.data_classes.NewNote
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import code.with.me.testroomandnavigationdrawertest.databinding.NoteItemBinding
import javax.inject.Inject
import javax.inject.Named


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
//    private lateinit var itemsBinding: NoteItemBinding

//    private val NewNotesArray: ArrayList<NewNote> = ArrayList()
//    private lateinit var adapter: BaseAdapter<NewNote, NoteItemBinding>

    private lateinit var appSettingPrefs: SharedPreferences
    private lateinit var sharedPrefsEdit: SharedPreferences.Editor
    private var isNightModeOn: Boolean = false

    @Inject
    @Named("noteVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var noteViewModel: NoteViewModel


    enum class States {

    }
    private val navController: NavController
        get() = Navigation.findNavController(this, R.id.fragment_detail)

    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {
                R.id.detailFragment -> {

                }
            }
        }


    override fun onResume() {
        super.onResume()
        navController.addOnDestinationChangedListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        navController.removeOnDestinationChangedListener(listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val appComponent = (application as NotesApplication).appComponent
        appComponent.inject(this)

//        appSettingPrefs = getSharedPreferences("AppSettingPrefs", 0).also {
//            sharedPrefsEdit = it.edit()
//            isNightModeOn = it.getBoolean("NightMode", false)
//        }
//
//        if (isNightModeOn) {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
//        } else {
//            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
//        }


        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)


        setContentView(binding.root)
//        setSupportActionBar(binding.toolbar)
//
//        supportActionBar?.apply {
//            setHomeAsUpIndicator(R.drawable.search48px)
//            setDisplayHomeAsUpEnabled(true)
//            setDisplayShowTitleEnabled(false)
//            title = "SimpleNote"
//            elevation = 32F
//        }

        noteViewModel = ViewModelProvider(this, factory)[NoteViewModel::class.java]

        binding.apply {
//            fab.setOnClickListener {
//                binding.apply {
//                    fragmentDetail.visibility = View.VISIBLE
//                }
////                navController.navigate(NotesListFragmentDirections.actionNotesListFragment2ToAddNoteFragment())
//                goToAddNoteFragment()
//            }
        }
    }

//    private fun goToAddNoteFragment() {
//        navController.navigate(NotesListFragmentDirections.actionNotesListFragment2ToAddNoteFragment(
//
//        ))
//    }

    override fun onPause() {
        super.onPause()
        overridePendingTransition(0, 0)
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

//    override fun onBackPressed() {
//        if (supportFragmentManager.backStackEntryCount == 0) {
//            this.finish()
//        } else {
////            binding.relativeLayout.visibility = View.VISIBLE
//            binding.toolbar.visibility = View.VISIBLE
//            binding.fragmentDetail.visibility = View.GONE
//            super.onBackPressed()
//        }
//    }


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