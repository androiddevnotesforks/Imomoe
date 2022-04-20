package com.skyd.imomoe.route.processor

import android.content.Context
import android.net.Uri
import android.widget.Toast
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.util.showToast

/**
 * 播放缓存的视频（m3u8格式）
 */
object PlayDownloadM3U8Processor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        "暂不支持m3u8格式 :(".showToast(Toast.LENGTH_LONG)
    }

    override val route: String
        get() = "${Route.SCHEME}://m3u8.play.download.anime.app"
}