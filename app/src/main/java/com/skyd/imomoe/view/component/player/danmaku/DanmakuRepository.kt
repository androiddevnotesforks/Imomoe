package com.skyd.imomoe.view.component.player.danmaku

import com.kuaishou.akdanmaku.data.DanmakuItemData

interface DanmakuRepository {
    suspend fun parse(): List<DanmakuItemData>
}