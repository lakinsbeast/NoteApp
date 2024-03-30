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
    private val newBinding by lazy {
        _binding
    }

    private var _binding: VB? = null

    //    val binding get() = _binding!!
    val binding by lazy {
        _binding!!
    }

    //this is not work throw Caused by: java.lang.IllegalStateException: Fragment MainScreenFragment{9b37977} (167b9b83-1103-419d-b22c-9c4e81e04d59) not attached to a context
//    val activity = findActivity(requireContext())

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
