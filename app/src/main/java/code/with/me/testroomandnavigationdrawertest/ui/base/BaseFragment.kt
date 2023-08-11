package code.with.me.testroomandnavigationdrawertest.ui.base

import android.R
import android.os.Bundle
import android.transition.Fade
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import code.with.me.testroomandnavigationdrawertest.Utils.findActivity


abstract class BaseFragment<VB : ViewBinding>(val get: ((LayoutInflater, ViewGroup?, Boolean) -> VB)) :
    Fragment() {

    private var _binding: VB? = null
    val binding get() = _binding!!

    fun activity() = findActivity(requireContext())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = get(inflater, container, false)
        return binding.root
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}