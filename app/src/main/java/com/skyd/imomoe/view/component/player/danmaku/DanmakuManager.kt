package com.skyd.imomoe.view.component.player.danmaku

import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ecs.component.filter.*
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView

class DanmakuManager(val danmakuView: DanmakuView) {
    companion object {
        var danmakuUrl: String = ""
        val colorFilter = TextColorFilter()
        var dataFilters = emptyMap<Int, DanmakuFilter>()
        var config = DanmakuConfig().apply {
            dataFilter = createDataFilters()
            dataFilters = dataFilter.associateBy { it.filterParams }
            layoutFilter = createLayoutFilters()
        }
        val danmakuDataList: MutableList<DanmakuItemData> = mutableListOf()

        // 弹幕源类型
        var danmakuType: DanmakuType<*>? = null

        private fun createDataFilters(): List<DanmakuDataFilter> =
            listOf(
                TypeFilter(),
                colorFilter,
                UserIdFilter(),
                GuestFilter(),
                BlockedTextFilter { it == 0L },
                DuplicateMergedFilter()
            )

        private fun createLayoutFilters(): List<DanmakuLayoutFilter> = emptyList()
    }

    var danmakuPlayer: DanmakuPlayer = DanmakuPlayer(SimpleRenderer()).apply {
        bindView(danmakuView)
    }
        private set

    fun recreatePlayer() {
        danmakuPlayer.release()
        danmakuPlayer = DanmakuPlayer(SimpleRenderer()).apply {
            bindView(danmakuView)
        }
    }

}