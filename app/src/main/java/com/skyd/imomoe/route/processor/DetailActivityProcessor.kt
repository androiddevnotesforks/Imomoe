package com.skyd.imomoe.route.processor

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.AnimeDetailActivity

/**
 * 跳转到分类页面对应项目
 */
object DetailActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        val partUrl = uri.getQueryParameter("partUrl")
        context?.startActivity(
            Intent(context, AnimeDetailActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra("partUrl", partUrl)
        )
    }

    /**
     * query:
     * partUrl 目标网址
     */
    override val route: String
        get() = "${Route.SCHEME}://detail.anime.app"
}