package code.with.me.testroomandnavigationdrawertest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentController
import code.with.me.testroomandnavigationdrawertest.ui.controllers.SheetController
import code.with.me.testroomandnavigationdrawertest.ui.controllers.fragmentOptionsBuilder
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MainScreenFragment
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var fragmentController: FragmentController

    @Inject
    lateinit var sheetController: SheetController

    override fun onCreate(savedInstanceState: Bundle?) {
        initAppComponent()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val fragment = MainScreenFragment()
        fragmentController.openFragment(
            this,
            fragment,
            fragmentOptionsBuilder {
                fragmentLayout = R.id.fragment_detail
            })
    }

    private fun initAppComponent() {
        val appComponent = (application as NotesApplication).appComponent
        appComponent.inject(this)
    }
}
