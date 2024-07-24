package code.with.me.testroomandnavigationdrawertest.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import code.with.me.testroomandnavigationdrawertest.databinding.ActivityMainBinding
import code.with.me.testroomandnavigationdrawertest.ui.controllers.FragmentController
import code.with.me.testroomandnavigationdrawertest.ui.controllers.SheetController
import dagger.hilt.android.AndroidEntryPoint
import ru.tfk.mainscreen.CustomNavHost
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var fragmentController: FragmentController

    @Inject
    lateinit var sheetController: SheetController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(binding.root)

        setContent {
            CustomNavHost()
        }

//        val fragment = MainScreenFragment()
//        fragmentController.openFragment(
//            this,
//            fragment,
//            fragmentOptionsBuilder {
//                fragmentLayout = R.id.fragment_detail
//            },
//        )
    }
}
