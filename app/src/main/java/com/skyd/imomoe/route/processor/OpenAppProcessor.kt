package com.skyd.imomoe.route.processor

import android.content.Context
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.route.Router.route

/**
 * 启动app
 */
object OpenAppProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        uri.getQueryParameter("route")?.route(context)
    }

    /**
     * query:
     * route app内route 可选
     */
    override val route: String
        get() = "${Route.SCHEME}://open.anime.app"
}