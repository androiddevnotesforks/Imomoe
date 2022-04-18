package com.skyd.imomoe.view.component.player.danmaku.anime

import com.skyd.imomoe.bean.danmaku.DanmakuData
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DanmakuService
import com.skyd.imomoe.util.showToast

object AnimeDanmakuRequester {
    suspend fun request(
        animeName: String,
        episode: String,
    ): DanmakuData? {
        return RetrofitManager
            .get()
            .create(DanmakuService::class.java)
            .getDanmaku(animeName = animeName, episode = episode)
            .also {
                if (it.code != 200) {
                    it.msg.showToast()
                }
            }
            .data
    }
}