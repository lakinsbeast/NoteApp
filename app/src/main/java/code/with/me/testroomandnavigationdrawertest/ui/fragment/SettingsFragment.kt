package code.with.me.testroomandnavigationdrawertest.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.core.view.size
import androidx.core.view.updateLayoutParams
import code.with.me.testroomandnavigationdrawertest.databinding.SettingsFragmentBinding
import code.with.me.testroomandnavigationdrawertest.ui.base.BaseFragment
import code.with.me.testroomandnavigationdrawertest.ui.sheet.SettingsSheet

class SettingsFragment : BaseFragment<SettingsFragmentBinding>(SettingsFragmentBinding::inflate) {
    /**
     * Сделать:
     * -настройку синхронизации
     * -
     **/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.imageView.updateLayoutParams {
            height = ((1.0f) * 650).toInt()
            width = ((1.0f) * 650).toInt()
        }
        val sheet = SettingsSheet() {
            binding.frameLayout.alpha = 1.0f - it
            binding.imageView.rotation = it * 100
            binding.imageView.updateLayoutParams {
                height = ((1.0f - it) * 650).toInt()
                width = ((1.0f - it) * 650).toInt()
            }
        }
        binding.imageView.playAnimation()
        sheet.apply {
            canHide = false
            isBackNeedBeBlurred = false
            doSheetBackShadowed = false
            isSheetCancelable = false
        }
        activity().sheetController.showSheet(activity(), sheet)
    }

}