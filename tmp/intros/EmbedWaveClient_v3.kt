package tech.unrealistic.cineflix.feature.player

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
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
 *
 * Logging is added for debugging purposes to track WebView lifecycle events
 * and help diagnose issues with the embedded player.
 */
class EmbedWaveClient @JvmOverloads constructor(
    context: Context,
    private val onEmbeddedPlayerCreated: (WebView) -> Unit = {},
    private val onError: (String) -> Unit = {}
) {
    companion object {
        private const val TAG = "EmbedWaveClient"
    }

    private val webView = WebView(context).apply {
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        settings.userAgentString =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    }

    private var embeddedPlayerCreated = false
    private val handler = Handler(Looper.getMainLooper())

    fun attach(url: String, onError: (String) -> Unit = {}) {
        this.onError = onError
        Log.d(TAG, "Attaching to URL: $url")

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                Log.d(TAG, "Page finished: $url")
                if (!embeddedPlayerCreated && EmbedWaveClient.isEmbedWaveUrl(url ?: "")) {
                    embeddedPlayerCreated = true
                    Log.d(TAG, "EmbedWave player detected, triggering callback")
                    handler.post {
                        onEmbeddedPlayerCreated(view)
                    }
                }
            }

            override fun onReceivedError(view: WebView?, errorCode: Int, description: String?, failingUrl: String?) {
                super.onReceivedError(view, errorCode, description, failingUrl)
                Log.e(TAG, "WebView error $errorCode: $description (url: $failingUrl)")
                onError("WebView error: $errorCode - $description")
            }
        }
        webView.loadUrl(url)
    }

    fun getWebView(): WebView = webView

    fun destroy() {
        Log.d(TAG, "Destroying WebView")
        webView.stopLoading()
        webView.destroy()
    }

    fun isAttached(): Boolean = embeddedPlayerCreated

    companion object {
        /** True if the given URL points to an EmbedWave embed page. */
        fun isEmbedWaveUrl(url: String): Boolean {
            return url.startsWith("https://embedwave.cc/embed/")
        }
    }
}
