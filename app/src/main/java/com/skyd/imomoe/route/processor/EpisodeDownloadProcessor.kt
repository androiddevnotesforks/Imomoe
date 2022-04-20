package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.AnimeDownloadActivity

/**
 * 转到缓存的番剧页面（显示每一部）
 */
object EpisodeDownloadProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        if (context !is Activity) error("context isn't Activity")
        val type = uri.getQueryParameter("type")?.toInt()
        val animeTitle = uri.getQueryParameter("animeTitle")
        val directoryName = uri.getQueryParameter("directoryName")
        context.startActivity(
            Intent(context, AnimeDownloadActivity::class.java)
                .putExtra("mode", 1)
                .putExtra("actionBarTitle", animeTitle)
                .putExtra("directoryName", directoryName)
                .putExtra("path", type)
        )
    }

    /**
     * query:
     * type 0存储在内部 or 1存储在外部
     * animeTitle 动漫标题
     * directoryName 动漫文件夹（不是完整路径）名称
     */
    override val route: String
        get() = "${Route.SCHEME}://episode.download.anime.app"
}