package com.skyd.imomoe.view.component.player.danmaku.anime

import android.widget.Toast
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.danmaku.DanmakuData
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DanmakuService
import com.skyd.imomoe.ext.shield
import com.skyd.imomoe.util.showToast

object AnimeDanmakuSender {
    suspend fun send(
        content: String,
        time: Long,     // 毫秒时间戳
        episodeId: String,
        type: String = "scroll",
        color: String = "#FFFFFF",
    ): DanmakuData.Data? {
        if (content.shield()) {
            appContext.getString(R.string.danmaku_exist_shield_content).showToast(Toast.LENGTH_LONG)
            return null
        }
        return RetrofitManager
            .get()
            .create(DanmakuService::class.java)
            .sendDanmaku(content, time / 1000.0, episodeId, type, color)
            .also {
                if (it.code != 200) {
                    it.msg.showToast()
                }
            }
            .data
    }
}