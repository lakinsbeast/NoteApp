package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.ROUNDED_CORNERS_SHEET
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.ROUNDED_CORNERS_STROKE
import code.with.me.testroomandnavigationdrawertest.data.utils.gone
import code.with.me.testroomandnavigationdrawertest.data.utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.data.utils.visible
import code.with.me.testroomandnavigationdrawertest.databinding.SettingsSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch

class SettingsSheet(val slide: (Float) -> Unit) :
    BaseSheet<SettingsSheetBinding>(SettingsSheetBinding::inflate) {
    // TODO: Добавить превьюдиалога для настроек и добавить возможность настраивать значения dimAmount и blurBehind

//    @Inject
//    @Named("settingsVMFactory")
//    lateinit var factory: ViewModelProvider.Factory
    private val viewModel: SettingsViewModel by viewModels()
//    lazy {
//        ViewModelProvider(this, factory)[SettingsViewModel::class.java]
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initRoundedCorners()
        initOnSlideCallback()
        initListeners()
        setStateCollapsed()
        setBackPressed()
    }

    /** при движении sheet делается видимым/невидимым текст "настроки" и кнопки назад
     * и становятся острее верхние углы тем сильнее, чем ближе вверх
     * */
    private fun initOnSlideCallback() {
        onSlide = {
            slide.invoke(it)
            binding.apply {
                include2.root.alpha = 1.0f - it
                initRoundedCornersWithParams(it)
                if (it > 0.01f) {
                    topMenuLayout.visible()
                    topMenuLayout.alpha = it
                } else {
                    topMenuLayout.gone()
                }
            }
        }
    }

    private fun SettingsSheetBinding.initRoundedCornersWithParams(it: Float) {
        mainLayout.setRoundedCornersView(
            (1.0f - it) * ROUNDED_CORNERS_SHEET,
            Color.WHITE,
            Color.BLACK,
            (1.0f - it) * ROUNDED_CORNERS_STROKE,
        )
    }

    /** делает углы sheet закругленными при создании */
    private fun initRoundedCorners() {
        binding.apply {
            mainLayout.setRoundedCornersView(
                ROUNDED_CORNERS_SHEET,
                Color.WHITE,
                Color.BLACK,
                ROUNDED_CORNERS_STROKE,
            )
        }
    }

    private fun initListeners() {
        binding.apply {
            backBtn.setOnClickListener {
                closeSettingsScreen()
            }
            isUseFolderSettingsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.saveSetting(
                    SettingsViewModel.SettingAction.ApplyFolderSettings(
                        isChecked,
                    ),
                )
            }
            useABehindBlurSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.saveSetting(
                    SettingsViewModel.SettingAction.ApplyBehindBlurSettings(
                        isChecked,
                    ),
                )
            }
            useABackgroundBlurSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                viewModel.saveSetting(
                    SettingsViewModel.SettingAction.ApplyBackgroundBlurSettings(
                        isChecked,
                    ),
                )
            }
        }
    }

    private fun initViewModel() {
        // TODO: есть ли смысл в нем?
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isUseFolderEnabled.collect {
                    binding.isUseFolderSettingsSwitch.isChecked = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isUseBehindBlurEnabled.collect {
                    binding.useABehindBlurSwitch.isChecked = it
                }
            }
        }
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.isUseBackgroundBlurEnabled.collect {
                    binding.useABackgroundBlurSwitch.isChecked = it
                }
            }
        }
    }

    private fun closeSettingsScreen() {
        activity().sheetController.closeSheet(activity(), this@SettingsSheet)
        activity().fragmentController.closeFragment(activity(), "SettingsFragment")
    }

    // onbackpressed нет, поэтому сделал такую штуку
    private fun setBackPressed() {
        dialog?.setOnKeyListener { dialog, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                closeSettingsScreen()
                return@setOnKeyListener true
            } else {
                return@setOnKeyListener false
            }
        }
    }
}
