package com.skyd.imomoe.view.component.player.danmaku

import com.skyd.imomoe.bean.danmaku.DanmakuData

sealed class DanmakuType {
    class AnimeType(var data: DanmakuData? = null) : DanmakuType()
    object BilibiliType : DanmakuType()
}

