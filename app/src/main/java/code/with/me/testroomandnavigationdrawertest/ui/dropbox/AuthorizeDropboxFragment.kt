package code.with.me.testroomandnavigationdrawertest.ui.dropbox

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import code.with.me.testroomandnavigationdrawertest.R
import code.with.me.testroomandnavigationdrawertest.data.data_classes.DropboxInfo
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentAuthorizeDropboxBinding
import code.with.me.testroomandnavigationdrawertest.databinding.FragmentDetailBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthorizeDropboxFragment : Fragment() {

    private var _binding: FragmentAuthorizeDropboxBinding? = null
    private val binding get() = _binding!!

    private lateinit var webViewClient: WebViewClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAuthorizeDropboxBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.webView.loadUrl("https://www.dropbox.com/oauth2/authorize?client_id=${DropboxInfo.CLIENT_APP}&response_type=code")
        binding.webView.settings.apply {
            javaScriptEnabled = true
            userAgentString = System.getProperty("http.agent");
        }
        webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                Log.d("url:", request!!.url.toString())
                if (request.url.toString() == "https://www.dropbox.com/1/oauth2/authorize_submit") {
                    redirectAfterAuthorize()
                }

                view?.loadUrl(request.url.toString())
                return true
            }
        }
        binding.webView.webViewClient = webViewClient
    }

    fun redirectAfterAuthorize() {
        var count = 5
        lifecycleScope.launch {
            while (true) {
                for (i in count downTo 0) {
                    Toast.makeText(
                        requireContext(),
                        "Скопируйте код, перенаправление через $i",
                        Toast.LENGTH_SHORT
                    ).show()
                    delay(1000)
                }
                requireActivity().supportFragmentManager.beginTransaction()
                    .remove(this@AuthorizeDropboxFragment).commit()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}