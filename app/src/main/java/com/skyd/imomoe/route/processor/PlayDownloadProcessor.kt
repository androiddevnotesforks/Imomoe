package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.SimplePlayActivity

/**
 * 播放缓存的视频（非m3u8格式）
 */
object PlayDownloadProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        if (context !is Activity) error("context isn't Activity")
        val filePath = uri.getQueryParameter("filePath")
        val animeTitle = uri.getQueryParameter("animeTitle")
        val episodeTitle = uri.getQueryParameter("episodeTitle")
        context.startActivity(
            Intent(context, SimplePlayActivity::class.java)
                .putExtra(SimplePlayActivity.URL, "file://$filePath")
                .putExtra(SimplePlayActivity.ANIME_TITLE, animeTitle)
                .putExtra(SimplePlayActivity.EPISODE_TITLE, episodeTitle)
        )
    }

    /**
     * query:
     * filePath 视频文件路径
     * animeTitle 动漫标题
     * episodeTitle 当前集标题
     */
    override val route: String
        get() = "${Route.SCHEME}://play.download.anime.app"
}