package com.skyd.imomoe.view.component.player.danmaku

enum class DanmakuMode {
    Scroll, Top, Bottom
}

fun DanmakuMode.string(): String {
    return when (this) {
        DanmakuMode.Scroll -> "scroll"
        DanmakuMode.Top -> "top"
        DanmakuMode.Bottom -> "bottom"
    }
}