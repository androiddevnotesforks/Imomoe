package com.skyd.imomoe.view.component.player.danmaku.anime

import android.graphics.Color
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.data.DanmakuItemData.Companion.DANMAKU_STYLE_NONE
import com.skyd.imomoe.bean.danmaku.DanmakuData
import com.skyd.imomoe.util.Util.sp
import com.skyd.imomoe.view.component.player.danmaku.anime.AnimeDanmakuParser.Companion.toDanmakuItemData
import kotlin.math.roundToLong

class AnimeDanmakuParser(private var data: List<DanmakuData.Data>) {

    fun parse(): List<DanmakuItemData> {
        val dataList: List<DanmakuItemData> = data.map {
            it.toDanmakuItemData()
        }
        return dataList
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
            if (s.startsWith("rgb")) {
                runCatching {
                    val rgb = s.replace("rgb", "")
                        .replace("(", "")
                        .replace(")", "")
                        .split(",")
                    return Color.rgb(
                        rgb[0].trim().toInt(),
                        rgb[1].trim().toInt(),
                        rgb[2].trim().toInt()
                    )
                }.onFailure {
                    it.printStackTrace()
                    return Color.WHITE
                }
            } else if (s.startsWith("#")) {
                return Color.parseColor(s)
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