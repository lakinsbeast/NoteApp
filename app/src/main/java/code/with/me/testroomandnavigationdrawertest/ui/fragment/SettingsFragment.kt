package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.updateLayoutParams
import code.with.me.testroomandnavigationdrawertest.data.const.Const.Companion.SETTINGS_IMAGE_HEIGHT_WIDTH
import code.with.me.testroomandnavigationdrawertest.databinding.SettingsFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SettingsSheet

class SettingsFragment : BaseFragment<SettingsFragmentBinding>(SettingsFragmentBinding::inflate) {
    /**
     * Сделать:
     * -настройку синхронизации
     * -
     **/

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        initAnimation()
        openSettingsSheet()
    }

    private fun initAnimation() {
        binding.imageView.updateLayoutParams {
            height = SETTINGS_IMAGE_HEIGHT_WIDTH
            width = SETTINGS_IMAGE_HEIGHT_WIDTH
        }
        binding.imageView.playAnimation()
    }

    private fun openSettingsSheet() {
        val sheet =
            SettingsSheet {
                binding.frameLayout.alpha = 1.0f - it
                binding.imageView.rotation = it * 100
                binding.imageView.updateLayoutParams {
                    height = ((1.0f - it) * 650).toInt()
                    width = ((1.0f - it) * 650).toInt()
                }
            }.apply {
                canHide = false
                isBackNeedBeBlurred = false
                doSheetBackShadowed = false
                isSheetCancelable = false
            }
        activity().sheetController.showSheet(activity(), sheet)
    }
}
