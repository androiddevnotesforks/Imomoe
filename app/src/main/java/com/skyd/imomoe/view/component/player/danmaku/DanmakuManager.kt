package com.skyd.imomoe.view.component.player.danmaku

import android.graphics.Color
import androidx.annotation.IntRange
import com.google.gson.Gson
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.ecs.component.filter.*
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.view.fragment.dialog.DanmakuSettingDialogFragment
import com.skyd.imomoe.view.fragment.dialog.DanmakuSettingDialogFragment.ShowDanmakuType

class DanmakuManager(val danmakuView: DanmakuView) {
    companion object {
        var danmakuUrl: String = ""
        val colorFilter = TextColorFilter()
        var dataFilters = emptyMap<Int, DanmakuFilter>()
        val danmakuDataList: MutableList<DanmakuItemData> = mutableListOf()

        // 是否使用弹幕功能（直接不请求弹幕数据）（播放本地视频时可能禁止弹幕功能）
        var enableDanmaku: Boolean = true

        // 是否在显示弹幕（已经启用，并且会请求弹幕数据）
        var showDanmaku = true

        // 弹幕源类型
        var danmakuType: DanmakuType<*>? = null

        var showDanmakuType: ShowDanmakuType = Gson().fromJson(
            sharedPreferences().getString("showDanmakuType", null),
            ShowDanmakuType::class.java
        ) ?: ShowDanmakuType()
            private set(value) {
                sharedPreferences().editor { putString("showDanmakuType", Gson().toJson(value)) }
                field = value
            }

        var allowOverlap: Boolean = sharedPreferences().getBoolean("allowDanmakuOverlap", true)
            private set(value) {
                sharedPreferences().editor { putBoolean("allowDanmakuOverlap", value) }
                field = value
            }

        @IntRange(from = 0, to = 100)
        var alpha: Int = sharedPreferences().getInt("danmakuAlpha", 100)
            private set(value) {
                sharedPreferences().editor { putInt("danmakuAlpha", value) }
                field = value
            }

        @IntRange(from = DanmakuSettingDialogFragment.MIN_DANMAKU_SCALE.toLong())
        var danmakuScale: Int = sharedPreferences().getInt("danmakuScale", 130)
            private set(value) {
                sharedPreferences().editor { putInt("danmakuScale", value) }
                field = value
            }

        var danmakuBold: Boolean = sharedPreferences().getBoolean("danmakuBold", true)
            private set(value) {
                sharedPreferences().editor { putBoolean("danmakuBold", value) }
                field = value
            }

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

        private var config = DanmakuConfig(
            allowOverlap = allowOverlap,
            alpha = alpha / 100f,
            textSizeScale = danmakuScale / 100f,
            bold = danmakuBold
        ).apply {
            dataFilter = createDataFilters()
            dataFilters = dataFilter.associateBy { it.filterParams }
            layoutFilter = createLayoutFilters()
        }

        // 发送弹幕样式
        var sendDanmakuMode: DanmakuMode = DanmakuMode.Scroll

        // 发送弹幕颜色
        var sendDanmakuColor: Int = Color.WHITE
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

    fun playDanmaku() {
        danmakuPlayer.start(config)
    }

    fun danmakuBold(danmakuBold: Boolean) {
        DanmakuManager.danmakuBold = danmakuBold
        config = config.copy(bold = danmakuBold)
        danmakuPlayer.updateConfig(config)
    }

    fun danmakuScale(
        @IntRange(from = DanmakuSettingDialogFragment.MIN_DANMAKU_SCALE.toLong())
        danmakuScale: Int
    ) {
        DanmakuManager.danmakuScale = danmakuScale
        config = config.copy(textSizeScale = danmakuScale / 100f)
        danmakuPlayer.updateConfig(config)
    }

    fun danmakuAlpha(@IntRange(from = 0, to = 100) alpha: Int) {
        DanmakuManager.alpha = alpha
        config = config.copy(alpha = alpha / 100f)
        danmakuPlayer.updateConfig(config)
    }

    fun allowOverlap(allowOverlap: Boolean) {
        DanmakuManager.allowOverlap = allowOverlap
        config = config.copy(allowOverlap = allowOverlap)
        danmakuPlayer.updateConfig(config)
    }

    fun switchTypeFilter(filter: ShowDanmakuType) {
        showDanmakuType = filter
        (dataFilters[DanmakuFilters.FILTER_TYPE_TYPE] as? TypeFilter)?.let { typeFilter ->
            filter.toList().forEach {
                if (it.second != -1) {
                    if (it.first) typeFilter.removeFilterItem(it.second)
                    else typeFilter.addFilterItem(it.second)
                } else {
                    colorFilter.filterColor.clear()
                    if (!it.first) {
                        colorFilter.filterColor.add(0xFFFFFF)
                    }
                }
                config.updateFilter()
                danmakuPlayer.updateConfig(config)
            }
        }
    }

    fun setDanmakuVisibility(visible: Boolean) {
        config = config.copy(visibility = visible)
        danmakuPlayer.updateConfig(config)
    }

    init {
        switchTypeFilter(showDanmakuType)
    }
}