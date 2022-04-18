package com.skyd.imomoe.view.component.player.danmaku.anime

import android.graphics.Color
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.data.DanmakuItemData.Companion.DANMAKU_STYLE_NONE
import com.skyd.imomoe.bean.danmaku.DanmakuData
import com.skyd.imomoe.util.Util.sp
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
                textColor = Color.parseColor(this.color ?: "#FFFFFF"),
                position = (this.time * 1000L).roundToLong(),
                textSize = 16.sp,
                danmakuStyle = danmakuStyle,
                mode = getMode(this.type),
            )
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