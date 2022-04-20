package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.MonthAnimeActivity

/**
 * 跳转到搜索界面
 */
object MonthAnimeActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        if (context !is Activity) error("context isn't Activity")
        val partUrl = uri.getQueryParameter("partUrl")
        context.startActivity(
            Intent(context, MonthAnimeActivity::class.java)
                .putExtra("partUrl", partUrl)
        )
    }

    /**
     * query:
     * partUrl 目标网址
     */
    override val route: String
        get() = "${Route.SCHEME}://monthAnime.anime.app"
}