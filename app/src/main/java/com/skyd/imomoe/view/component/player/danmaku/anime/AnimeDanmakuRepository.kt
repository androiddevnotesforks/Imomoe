package com.skyd.imomoe.view.component.player.danmaku.anime

import android.graphics.Color
import android.widget.Toast
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.data.DanmakuItemData.Companion.DANMAKU_STYLE_NONE
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.danmaku.DanmakuData
import com.skyd.imomoe.ext.shield
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DanmakuService
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.component.player.danmaku.DanmakuMode
import com.skyd.imomoe.view.component.player.danmaku.DanmakuRepository
import com.skyd.imomoe.view.component.player.danmaku.string
import kotlin.math.roundToLong

class AnimeDanmakuRepository(
    private val animeName: String,
    val episode: String,
) : DanmakuRepository {
    var data: DanmakuData? = null

    override suspend fun parse(): List<DanmakuItemData> {
        return RetrofitManager
            .get()
            .create(DanmakuService::class.java)
            .getDanmaku(animeName = animeName, episode = episode)
            .also {
                if (it.code != 200) {
                    it.msg.showToast()
                }
            }.data
            .also { this.data = it }
            ?.data.orEmpty()
            .map { it.toDanmakuItemData() }
    }

    suspend fun send(
        content: String,
        time: Long,     // 毫秒时间戳
        episodeId: String = data?.episode?.id.orEmpty(),
        mode: DanmakuMode = DanmakuMode.Scroll,
        color: Int = Color.WHITE,
    ): DanmakuData.Data? {
        if (episodeId.isBlank()) {
            "invalid episodeId: $episodeId, send failed!".showToast(Toast.LENGTH_LONG)
            return null
        }
        if (content.shield()) {
            appContext.getString(R.string.danmaku_exist_shield_content).showToast(Toast.LENGTH_LONG)
            return null
        }
        return RetrofitManager
            .get()
            .create(DanmakuService::class.java)
            .sendDanmaku(
                content,
                time / 1000.0,
                episodeId,
                mode.string(),
                String.format("#%06X", 0xFFFFFF and color)
            )
            .also {
                if (it.code != 200) {
                    it.msg.showToast()
                }
            }
            .data
            ?.apply {
                data?.data?.add(this)
            }
    }

    companion object {
        fun DanmakuData.Data.toDanmakuItemData(danmakuStyle: Int = DANMAKU_STYLE_NONE): DanmakuItemData {
            return DanmakuItemData(
                content = this.content,
                danmakuId = this.id.hashCode().toLong(),
                textColor = parseColor(this.color ?: "#FFFFFF"),
                position = (this.time * 1000L).roundToLong(),
                textSize = 20,
                danmakuStyle = danmakuStyle,
                mode = getMode(this.type),
            )
        }

        private fun parseColor(s: String): Int {
            runCatching {
                if (s.startsWith("rgb")) {
                    val rgb = s.replace("rgb", "")
                        .replace("(", "")
                        .replace(")", "")
                        .split(",")
                    return Color.rgb(
                        rgb[0].trim().toInt(),
                        rgb[1].trim().toInt(),
                        rgb[2].trim().toInt()
                    )
                } else if (s.startsWith("#")) {
                    return Color.parseColor(s)
                }
            }.onFailure {
                it.printStackTrace()
            }
            return Color.WHITE
        }

        private fun getMode(s: String): Int {
            return when (s) {
                "scroll" -> DanmakuItemData.DANMAKU_MODE_ROLLING
                "top" -> DanmakuItemData.DANMAKU_MODE_CENTER_TOP
                "bottom" -> DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
                else -> DanmakuItemData.DANMAKU_MODE_ROLLING
            }
        }
    }
}