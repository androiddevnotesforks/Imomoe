package com.skyd.imomoe.util.html.source

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.*
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.containIn
import kotlinx.coroutines.*
import org.apache.commons.text.StringEscapeUtils
import java.io.ByteArrayInputStream
import java.net.SocketTimeoutException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@SuppressLint("SetJavaScriptEnabled")
object WebSource {
    private val webView by lazy {
        WebView(appContext).apply {
            settings.apply {
                // Sets whether the WebView should not load image resources from the network.
                // Note that this method has no effect unless getLoadsImagesAutomatically() returns true.
                blockNetworkImage = true
                // Sets whether the WebView should load image resources.
                // Note that this method controls loading of all images,
                // including those embedded using the data URI scheme.
                loadsImagesAutomatically = false
                javaScriptEnabled = true
                // Tells JavaScript to op en windows automatically.
                // This applies to the JavaScript function window.open().
                javaScriptCanOpenWindowsAutomatically = false
                // Sets whether cross-origin requests in the context of a file scheme URL
                // should be allowed to access content from other file scheme URLs.
                allowFileAccessFromFileURLs = true
                // Sets whether cross-origin requests in the context of a file scheme URL
                // should be allowed to access content from any origin.
                allowUniversalAccessFromFileURLs = true
                // Sets whether the DOM storage API is enabled. The default value is false.
                domStorageEnabled = true
                // The default value is false.
                databaseEnabled = true
                // Configures the WebView's behavior when a secure origin attempts to
                // load a resource from an insecure origin.
                mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                // Overrides the way the cache is used.
                // The way the cache is used is based on the navigation type.
                cacheMode = WebSettings.LOAD_DEFAULT
                setSupportZoom(true)
                // Enables or disables content URL access within WebView.
                // Content URL access allows WebView to load content from
                // a content provider installed in the system. The default is enabled.
                allowContentAccess = true
                // Sets whether the WebView whether supports multiple windows.
                // If set to true, WebChromeClient#onCreateWindow must be implemented
                // by the host application. The default is false.
                setSupportMultipleWindows(true)
            }
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
        }
    }

    suspend fun getWebSource(
        url: String,
        encoding: String = "UTF-8",
        userAgent: String? = null,
        timeout: Long = 10000L
    ): String = withContext(Dispatchers.Main) {
        suspendCoroutine { con ->
            webView.settings.apply {
                userAgent?.let { userAgentString = it }
                defaultTextEncodingName = encoding
            }
            webView.webViewClient = WebSourceWebViewClientImpl(timeout) { con.resume(it) }
            webView.resumeTimers()
            webView.loadUrl(url)
        }
    }

    class WebSourceWebViewClientImpl(
        private val timeout: Long,
        private val onResult: WebView.(String) -> Unit
    ) : WebSourceWebViewClient() {
        private var isFinished = false

        private fun finished(web: WebView) {
            isFinished = true
            web.evaluateJavascript("(function() { return document.documentElement.outerHTML })()") {
                web.apply {
                    onResult(StringEscapeUtils.unescapeEcmaScript(it))
                    stopLoading()
                    pauseTimers()
                }
            }
        }

        override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)
            view.postDelayed({
                if (!isFinished) throw SocketTimeoutException("timeout")
            }, timeout)
        }

        override fun onPageFinished(view: WebView, url: String?) {
            super.onPageFinished(view, url)
            if (!isFinished) finished(view)
        }

        override fun onLoadResource(view: WebView, url: String) {
            super.onLoadResource(view, url)
        }
    }

    abstract class WebSourceWebViewClient(
        // 若路径中有这些后缀，则不加载
        private val preventRequestSuffix: Array<String> = arrayOf(
            ".css",
            ".png",
            ".jpg",
            ".jpeg",
            ".webp",
            ".ico",
            ".bmp",
            ".gif",
            ".tiff",
            ".mp4",
            ".ts",
            ".mp3",
            ".m4a",
            ".flv",
        )
    ) : WebViewClient() {
        @SuppressLint("WebViewClientOnReceivedSslError")
        override fun onReceivedSslError(
            view: WebView?,
            handler: SslErrorHandler?,
            error: SslError?
        ) {
            handler?.proceed()
        }

        // 只加载指定的文件资源
        override fun shouldInterceptRequest(
            view: WebView,
            request: WebResourceRequest?
        ): WebResourceResponse? {
            request ?: return super.shouldInterceptRequest(view, request)
            // 只加载指定类型的数据
            return if (request.url?.path?.containIn(preventRequestSuffix) == true) {
                // 不需要加载任何资源，因此不需要任何数据
                WebResourceResponse(
                    "text/html",
                    "UTF-8",
                    ByteArrayInputStream(ByteArray(0))
                )
            } else {
                super.shouldInterceptRequest(view, request)
            }
        }
    }
}