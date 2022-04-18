package com.skyd.imomoe.view.component.player.danmaku.bili

import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DanmakuService
import com.skyd.imomoe.util.Util.toEncodedUrl
import java.io.InputStream

object BilibiliDanmakuRequester {
    suspend fun request(url: String): InputStream {
        return RetrofitManager.get().create(DanmakuService::class.java)
            .getCustomizeDanmaku(url.toEncodedUrl()).byteStream()
    }
}