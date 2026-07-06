package tech.unrealistic.cineflix.data.remote

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient

class VideoStreamResolver (private val context: Context){

    //A user agent to spoof client as a browser and bypass basic automated script blocker
    private val desktopUserAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

    interface  StreamResolutionListener {
        fun onStreamResolved(streamUrl: String, referrer: String)
        fun onError(message:String)

    }

    @SuppressLint("SetJavaScriptEnabled")
    fun resolvedEmbedUrl(embedUrl:String, listener: StreamResolutionListener){
        //webview initializer -= by default the streaming url expects an iframe
        Handler(Looper.getMainLooper()).post {
            val webview = WebView(context)
            //config webView settings
            webview.settings.javaScriptEnabled=  true
            webview.settings.domStorageEnabled=true
            webview.settings.userAgentString= desktopUserAgent

            webview.webViewClient= object : WebViewClient(){
                override fun shouldInterceptRequest(
                    view: WebView?,
                 request: WebResourceRequest?
                ): WebResourceResponse? {
                    val url = request?.url?.toString() ?: return null

                    //strategy 1- Intercept standard dynamic HLS playlist (.m3u8) or DASH manifests  (.mpd)
                     if (url.contains(".m3u8") || url.contains(".mpd") || url.contains("/video/source")){
                         // return success we found the url yay!
                         listener.onStreamResolved(
                             url,
                             embedUrl //handover the parent gateway as the  hotlink referrer verification
                         )
                         // clean up headless instance on a main loop to free device memory
                         Handler(Looper.getMainLooper()).post {
                             webview.stopLoading()
                             webview.destroy()
                         }
                     }
                    return  super.shouldInterceptRequest(view, request)
                }

                @Deprecated("Deprecated in Java")
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    url: String?
                ): Boolean {

                    //Ad protection - force webview to stay locked in the domain

                    return !url?.all { embedUrl.contains(it) }!!
                }
            }
            //fire headless gateway routine
            webview.loadUrl(embedUrl)

        }
    }
}