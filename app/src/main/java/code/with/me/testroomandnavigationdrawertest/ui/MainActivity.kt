package code.with.me.testroomandnavigationdrawertest.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentController
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentOptions
import code.with.me.testroomandnavigationdrawertest.ui.dialog.CreateFolderDialog
import code.with.me.testroomandnavigationdrawertest.ui.fragment.MainScreenFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.AudioRecorderDialog
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
        fragmentController.openFragment(this, fragment, FragmentOptions(R.id.fragment_detail))
    }

    private fun initDI() {
        val appComponent = (application as NotesApplication).appComponent
        appComponent.inject(this)
    }
}