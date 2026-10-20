package tech.unrealistic.cineflix.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

/**
 * Extracts a direct HLS/DASH manifest URL from an embed page.
 *
 * The page is a JavaScript player, so the only reliable way to learn its stream
 * URL is to let it run off-screen and intercept the media request. Upstream
 * players only start on a user gesture, so a synthetic tap is dispatched once
 * the page has loaded — that is what makes a headless load play at all.
 *
 * Must be called on the main thread. The returned WebView is the caller's to
 * destroy when resolution is abandoned.
 */
class VideoStreamResolver(private val context: Context) {

    interface StreamResolutionListener {
        fun onStreamResolved(streamUrl: String, referrer: String)
        fun onError(message: String)
    }

    @SuppressLint("SetJavaScriptEnabled")
    fun resolvedEmbedUrl(
        embedUrl: String,
        listener: StreamResolutionListener,
    ): WebView {
        val webView = WebView(context)
        val mainHandler = Handler(Looper.getMainLooper())

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            userAgentString = DESKTOP_USER_AGENT
            mediaPlaybackRequiresUserGesture = false
        }

        // A real viewport: responsive players refuse to start on a zero-sized surface.
        webView.measure(
            View.MeasureSpec.makeMeasureSpec(VIEWPORT_WIDTH, View.MeasureSpec.EXACTLY),
            View.MeasureSpec.makeMeasureSpec(VIEWPORT_HEIGHT, View.MeasureSpec.EXACTLY)
        )
        webView.layout(0, 0, VIEWPORT_WIDTH, VIEWPORT_HEIGHT)

        // Providers move domains constantly (vidsrc.me -> vidsrc.sh), so the
        // first cross-host hop is followed, then the page is pinned to it.
        var allowedHost: String? = embedUrl.toHttpHost()
        var followedRedirect = false
        // The manifest is signed for the page that requested it, so the final
        // URL (after redirects) is what has to be sent as Referer.
        var currentPageUrl = embedUrl

        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                if (url != null) currentPageUrl = url
                mainHandler.postDelayed({
                    view?.let { tapCentre(it) }
                }, GESTURE_DELAY_MS)
            }

            override fun shouldInterceptRequest(
                view: WebView?,
                request: WebResourceRequest?,
            ): WebResourceResponse? {
                val url = request?.url?.toString() ?: return null
                if (!isManifestUrl(url)) return super.shouldInterceptRequest(view, request)

                // Signed manifests are bound to the page that asked for them,
                // which is usually an iframe inside the embed page.
                val referer = request.requestHeaders.orEmpty().entries
                    .firstOrNull { it.key.equals("Referer", ignoreCase = true) }
                    ?.value
                    ?: currentPageUrl
                Log.d(TAG, "resolved manifest: $url (referer $referer)")
                listener.onStreamResolved(url, referer)
                mainHandler.post {
                    view?.stopLoading()
                    view?.destroy()
                }
                return super.shouldInterceptRequest(view, request)
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?,
            ) {
                if (request?.isForMainFrame == true) {
                    Log.w(TAG, "embed page failed: ${error?.description}")
                    listener.onError(error?.description?.toString() ?: "Embed page failed")
                    view?.destroy()
                }
            }

            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                // Keep the off-screen page on the embed provider's site so ad
                // redirects do not navigate it somewhere that never exposes a
                // manifest.
                val url = request?.url ?: return true
                if (!request.isForMainFrame) return false
                if (url.scheme != "https") return true

                val host = url.host ?: return true
                if (host == allowedHost) {
                    currentPageUrl = url.toString()
                    return false
                }
                if (!followedRedirect) {
                    followedRedirect = true
                    allowedHost = host
                    currentPageUrl = url.toString()
                    Log.d(TAG, "following provider redirect to $host")
                    return false
                }
                Log.d(TAG, "blocked navigation to $host")
                return true
            }
        }

        Log.d(TAG, "extracting from $embedUrl")
        webView.loadUrl(embedUrl)
        return webView
    }

    private fun String.toHttpHost(): String? = runCatching {
        java.net.URI(this).host
    }.getOrNull()

    private fun tapCentre(view: WebView) {
        val now = SystemClock.uptimeMillis()
        val x = VIEWPORT_WIDTH / 2f
        val y = VIEWPORT_HEIGHT / 2f
        val down = MotionEvent.obtain(now, now, MotionEvent.ACTION_DOWN, x, y, 0)
        val up = MotionEvent.obtain(now, now + 60, MotionEvent.ACTION_UP, x, y, 0)
        view.dispatchTouchEvent(down)
        view.dispatchTouchEvent(up)
        down.recycle()
        up.recycle()
        Log.d(TAG, "dispatched play gesture")
    }

    private fun isManifestUrl(url: String): Boolean {
        val path = url.substringBefore('?').lowercase()
        return path.endsWith(".m3u8") ||
            path.endsWith(".mpd") ||
            path.contains("/master.txt") ||
            path.contains("/playlist")
    }

    private companion object {
        const val TAG = "VideoStreamResolver"
        const val VIEWPORT_WIDTH = 1920
        const val VIEWPORT_HEIGHT = 1080
        const val GESTURE_DELAY_MS = 4_000L
        const val DESKTOP_USER_AGENT =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"
    }
}
