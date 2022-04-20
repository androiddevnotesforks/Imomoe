package com.skyd.imomoe.route.processor

import android.content.Context
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.util.Util

/**
 * 打开浏览器
 */
object OpenBrowserProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        val url = uri.getQueryParameter("url")
            ?: error("can't get \"url\" parameter from Route.ROUTE_OPEN_BROWSER")
        Util.openBrowser(url)
    }

    /**
     * query:
     * url 目标网址
     */
    override val route: String
        get() = "${Route.SCHEME}://openBrowser.anime.app"
}