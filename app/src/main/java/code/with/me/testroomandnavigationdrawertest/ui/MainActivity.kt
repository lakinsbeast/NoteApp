package code.with.me.testroomandnavigationdrawertest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import code.with.me.testroomandnavigationdrawertest.*
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MainScreenFragment


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val navController: NavController
        get() = Navigation.findNavController(this, R.id.fragment_detail)

    val fragmentController = FragmentController(this, R.id.fragment_detail)
    val sheetController = SheetController(this)
    private val listener =
        NavController.OnDestinationChangedListener { controller, destination, arguments ->
            when (destination.id) {

            }
        }


    override fun onResume() {
        super.onResume()
//        navController.addOnDestinationChangedListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
//        navController.removeOnDestinationChangedListener(listener)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)


        val fragment = MainScreenFragment()
        fragmentController.openFragment(fragment)
    }

    private fun initDI() {
        val appComponent = (application as NotesApplication).appComponent
        appComponent.inject(this)
    }
}