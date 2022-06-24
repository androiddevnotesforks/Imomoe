package com.skyd.imomoe.util.html.source

import android.view.View

@Deprecated("use WebSource instead!")
interface GettingUICallback : GettingCallback {
    /**
     * 开始获取源代码
     * @param webView
     * @param url
     */
    fun onGettingStart(webView: View?, url: String?)

    /**
     * 结束获取源代码
     * @param webView
     * @param url
     */
    fun onGettingFinish(webView: View?, url: String?)
}