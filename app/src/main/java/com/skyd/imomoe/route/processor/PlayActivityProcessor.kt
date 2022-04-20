package com.skyd.imomoe.route.processor

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.PlayActivity

/**
 * 跳转到播放界面
 */
object PlayActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        context ?: error("context is null")
        val partUrl = uri.getQueryParameter("partUrl")
        val detailPartUrl = uri.getQueryParameter("detailPartUrl")
        context.startActivity(
            Intent(context, PlayActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("partUrl",partUrl)
                .putExtra("detailPartUrl", detailPartUrl)
        )
    }

    /**
     * query:
     * partUrl 目标网址。
     * detailPartUrl 目标网址。可选
     */
    override val route: String
        get() = "${Route.SCHEME}://play.anime.app"
}