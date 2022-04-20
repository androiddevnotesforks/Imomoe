package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route

/**
 * 启动activity
 */
object StartActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        val cls = Class.forName(uri.getQueryParameter("cls").orEmpty())
        context ?: error("StartActivityProcessor: activity is null")
        if (context is Activity) {
            context.startActivity(Intent(context, cls))
        } else {
            context.startActivity(
                Intent(context, cls).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    /**
     * query:
     * cls 目标Activity qualifiedName
     */
    override val route: String
        get() = "${Route.SCHEME}://startActivity.anime.app"
}