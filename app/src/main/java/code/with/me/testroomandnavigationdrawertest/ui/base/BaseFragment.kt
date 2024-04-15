package code.with.me.testroomandnavigationdrawertest.ui.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import code.with.me.testroomandnavigationdrawertest.data.utils.findActivity

abstract class BaseFragment<VB : ViewBinding>(val get: ((LayoutInflater, ViewGroup?, Boolean) -> VB)) :
    Fragment() {

    private var _binding: VB? = null

    val binding by lazy {
        _binding!!
    }

    fun activity() = findActivity(requireContext())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = get(inflater, container, false)
        return binding.root
    }

    fun closeFragment() {
        parentFragmentManager.beginTransaction().remove(this).commit()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
