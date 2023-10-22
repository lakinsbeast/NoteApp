package code.with.me.testroomandnavigationdrawertest.ui.sheet

import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.ViewModelProvider
import code.with.me.testroomandnavigationdrawertest.NotesApplication
import code.with.me.testroomandnavigationdrawertest.data.Utils.gone
import code.with.me.testroomandnavigationdrawertest.data.Utils.setRoundedCornersView
import code.with.me.testroomandnavigationdrawertest.data.Utils.visible
import code.with.me.testroomandnavigationdrawertest.databinding.SettingsSheetBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseSheet
import code.with.me.testroomandnavigationdrawertest.ui.viewmodel.SettingsViewModel
import javax.inject.Inject
import javax.inject.Named

class SettingsSheet(val slide: (Float) -> Unit) :
    BaseSheet<SettingsSheetBinding>(SettingsSheetBinding::inflate) {

    //TODO: Добавить превьюдиалога для настроек и добавить возможность настраивать значения dimAmount и blurBehind

    @Inject
    @Named("settingsVMFactory")
    lateinit var factory: ViewModelProvider.Factory
    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        val appComponent = (requireActivity().application as NotesApplication).appComponent
        appComponent.inject(this)
        super.onCreate(savedInstanceState)
        settingsViewModel = ViewModelProvider(this, factory)[SettingsViewModel::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        settingsViewModel.isUseFolderEnabled.observe(viewLifecycleOwner) {
            binding.isUseFolderSettingsSwitch.isChecked = it
        }
        settingsViewModel.isUseBehindBlurEnabled.observe(viewLifecycleOwner) {
            binding.useABehindBlurSwitch.isChecked = it
        }
        settingsViewModel.isUseBackgroundBlurEnabled.observe(viewLifecycleOwner) {
            binding.useABackgroundBlurSwitch.isChecked = it
        }

        binding.mainLayout.setRoundedCornersView(
            (1.0f) * 64f,
            Color.WHITE,
            Color.BLACK,
            (1.0f) * 5f
        )

        onSlide = {
            slide.invoke(it)
            binding.apply {
                include2.root.alpha = 1.0f - it
                mainLayout.setRoundedCornersView(
                    (1.0f - it) * 64f,
                    Color.WHITE,
                    Color.BLACK,
                    (1.0f - it) * 5f
                )
                if (it > 0.01f) {
                    topMenuLayout.visible()
                    topMenuLayout.alpha = it
                } else {
                    topMenuLayout.gone()
                }
            }
        }
        binding.apply {
            backBtn.setOnClickListener {
                closeSettingsScreen()
            }
            isUseFolderSettingsSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                settingsViewModel.applyUseFolder(isChecked)
            }
            useABehindBlurSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                settingsViewModel.applyUseBehindBlur(isChecked)
            }
            useABackgroundBlurSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                settingsViewModel.applyUseBackgroundBlur(isChecked)
            }

        }
        setStateCollapsed()
        setBackPressed()
    }

    private fun closeSettingsScreen() {
        activity().sheetController.closeSheet(activity(), this@SettingsSheet)
        activity().fragmentController.closeFragment(activity(), "SettingsFragment")
    }

    //onbackpressed нет, поэтому реализовал такую штуку
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