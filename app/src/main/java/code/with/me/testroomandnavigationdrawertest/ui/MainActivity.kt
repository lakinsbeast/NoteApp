package code.with.me.testroomandnavigationdrawertest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentController
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentOptions
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MainScreenFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AudioRecorderSheet
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var fragmentController: FragmentController

    @Inject
    lateinit var sheetController: SheetController

    override fun onCreate(savedInstanceState: Bundle?) {
        initDI()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val fragment = MainScreenFragment()
        val sheet = AudioRecorderSheet()
//        fragmentController.openFragment(this, fragment, FragmentOptions(R.id.fragment_detail))
        sheetController.showSheet(this, sheet)
    }

    private fun initDI() {
        val appComponent = (application as NotesApplication).appComponent
        appComponent.inject(this)
    }
}