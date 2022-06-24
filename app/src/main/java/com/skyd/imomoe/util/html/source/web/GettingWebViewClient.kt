package com.skyd.imomoe.util.html.source.web

import android.graphics.Bitmap
import android.net.http.SslError
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.SslErrorHandler
import android.webkit.WebView
import android.webkit.WebViewClient
import com.skyd.imomoe.util.html.source.GettingCallback
import com.skyd.imomoe.util.html.source.GettingUICallback
import com.skyd.imomoe.util.html.source.Util
import com.skyd.imomoe.util.logE
import com.skyd.imomoe.util.logI

@Deprecated("use WebSource instead!")
class GettingWebViewClient(
    private val mWebView: WebView?,
    private val mURL: String,
    private val mHeader: Map<String, String>,
    private val mCallback: GettingCallback?
) : WebViewClient() {
    private var isCompleteLoader = true
    private val mH = Handler(Looper.getMainLooper())
    private val mHtmlSource: String? = null
    private var mLastStartTime: Long = 0
    private var mLastEndTime = System.currentTimeMillis()
    private var mFinished: FinishedRunnable? = null
    private var mConnTimeout: TimeOutRunnable? = null
    private var mReadTimeout: TimeOutRunnable? = null
    private val mJSRunnable: ParserHtmlRunnable? = null
    private var mConnTimeOut = 20 * 1000L
    private var mReadTimeOut = 45 * 1000L
    private var mFinishedTimeOut: Long = 800
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        if (url.startsWith("http")) view.loadUrl(url, mHeader)
        return true
    }

    fun setConnTimeOut(connTimeOut: Long) {
        mConnTimeOut = connTimeOut
    }

    fun setFinishedTimeOut(mFinishedTimeOut: Long) {
        this.mFinishedTimeOut = mFinishedTimeOut
    }

    fun setReadTimeOut(readTimeOut: Long) {
        mReadTimeOut = readTimeOut
    }

    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        if (mLastEndTime - mLastStartTime <= 500 || !isCompleteLoader) { // 基本上是302 重定向才会走这段逻辑
            logE("GettingUtil", "onStart( 302 )  --> $url")
            mFinished?.let { mH.removeCallbacks(it) }
            return
        }
        mConnTimeout?.let { mH.removeCallbacks(it) }
        mH.postDelayed(
            TimeOutRunnable(view, url, TYPE_CONN).also { mConnTimeout = it }, mConnTimeOut
        )
        logE("GettingWebViewClient", "onStart(onPageStarted)  --> $url")
        onGettingStart(view, url)
    }

    override fun onPageFinished(view: WebView, url: String) {
        mLastEndTime = System.currentTimeMillis()
        mH.postDelayed(FinishedRunnable(view, url).also { mFinished = it }, mFinishedTimeOut)
    }

    override fun onReceivedError(
        view: WebView,
        errorCode: Int,
        description: String,
        failingUrl: String
    ) {
        logE("GettingWebViewClient", "onReceivedError(ReceivedError)  --> $failingUrl")
        onGettingError(view, failingUrl, RECEIVED_ERROR)
        onGettingFinish(view, failingUrl)
    }

    override fun onReceivedSslError(
        webView: WebView,
        sslErrorHandler: SslErrorHandler,
        sslError: SslError
    ) {
        sslErrorHandler.proceed() //證書不對的時候，繼續加載
    }

    fun onGettingStart(webView: View?, url: String?) {
        isCompleteLoader = false
        mLastStartTime = System.currentTimeMillis()
        if (mReadTimeout != null) {
            mH.removeCallbacks(mReadTimeout!!)
        }
        mH.postDelayed(
            TimeOutRunnable(
                webView as WebView,
                url,
                TYPE_READ
            ).also { mReadTimeout = it }, mReadTimeOut
        )
        if (mCallback is GettingUICallback) {
            mH.post { mCallback.onGettingStart(webView, url) }
        }
    }

    fun onGettingError(webView: View?, url: String?, errorCode: Int) {
        if (mCallback != null) {
            mH.post { mCallback.onGettingError(webView, url, errorCode) }
        }
    }

    fun onGettingFinish(webView: View?, url: String?) {
        isCompleteLoader = true
        mReadTimeout?.let {
            mH.removeCallbacks(it)
            mReadTimeout = null
        }
        if (mCallback is GettingUICallback) {
            mH.post { mCallback.onGettingFinish(webView, url) }
        }
    }

    private inner class ParserHtmlRunnable(private val view: WebView, private val method: String) :
        Runnable {
        override fun run() {
            Util.evalScript(view, method)
        }
    }

    // 一次网页加载结束
    private inner class FinishedRunnable(private val view: WebView, private val url: String) :
        Runnable {
        override fun run() {
            if (mConnTimeout == null) return
            mH.removeCallbacks(mConnTimeout!!)
            mConnTimeout = null
            logI("GettingWebViewClient", "一次网页加载结束 --> $url")
            onGettingFinish(view, url)
            Util.getHtmlSource(view)
        }
    }

    // 一次网页加载，解析超时
    private inner class TimeOutRunnable(
        private val view: WebView?,
        private val url: String?,
        private val type: Int
    ) :
        Runnable {
        override fun run() {
            //加载网页超时了
            if (type == TYPE_CONN) {
                logE(
                    "GettingWebViewClient",
                    "ConnTimeOutRunnable( postDelayed  【alert ，confirm】 )  --> $url"
                )
                mConnTimeout.let {
                    if (it == null) return
                    mH.removeCallbacks(it)
                    mConnTimeout = null
                }
                //                mH.postDelayed(new ParserHtmlRunnable(view, "alert"), 5000);
//                mH.postDelayed(mJSRunnable = new ParserHtmlRunnable(view, "confirm"), 8000);
            } else if (type == TYPE_READ) {
                logE("GettingWebViewClient", "ReadTimeOutRunnable(SUCCESS)  --> $url")
                onGettingFinish(view, url)
            }
        }

    }

    companion object {
        const val READ_TIME_OUT = 1
        const val RECEIVED_ERROR = 2
        const val NOT_FIND = 3
        const val CONNECTION_ERROR = 4
        const val CONTENT_ERROR = 5
        const val TYPE_CONN = 0
        const val TYPE_READ = 1
    }

}