package code.with.me.testroomandnavigationdrawertest.ui

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentImageToFullScreenBinding

class ImageToFullScreenFragment: Fragment() {

    private var _binding: FragmentImageToFullScreenBinding? = null
    // This property is only valid between onCreateView and
// onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImageToFullScreenBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString("imageUrl").apply {
            binding.imageView.setImageURI(Uri.parse(this))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}