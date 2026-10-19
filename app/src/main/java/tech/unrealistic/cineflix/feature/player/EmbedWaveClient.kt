package tech.unrealistic.cineflix.feature.player

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * EmbedWave client wrapper that creates and configures a WebView for
 * EmbedWave embed URLs.
 *
 * The embed player is a JavaScript app that loads inside an iframe. This
 * client creates a WebView, attaches the EmbedWave URL, and waits for the
 * player to be created before triggering the callback.
 */
class EmbedWaveClient @JvmOverloads constructor(
    context: Context,
    private val onEmbeddedPlayerCreated: (WebView) -> Unit = {},
    private val onError: (String) -> Unit = {}
) : Loggable {
    private val webView = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    }

    private var embeddedPlayerCreated = false
    override val TAG = "EmbedWaveClient"

    fun attach(url: String, onError: (String) -> Unit = {}) {
        Log.d(TAG, "Attach called for URL: $url")
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                if (!embeddedPlayerCreated && EmbedWaveClient.isEmbedWaveUrl(url ?: "")) {
                    embeddedPlayerCreated = true
                    onEmbeddedPlayerCreated(view!!)
                }
            }
            
            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                onError("WebView error: $errorCode - $description")
            }
        }
        webView.loadUrl(url)
    }

    fun getWebView(): WebView = webView
    fun destroy() {
        webView.stopLoading()
        webView.destroy()
    }

    companion object {
        /** True if the given URL points to an EmbedWave embed page. */
        fun isEmbedWaveUrl(url: String): Boolean {
            return url.startsWith("https://embedwave.cc/embed/")
        }
    }
}
interface Loggable {
    val TAG: String
}
