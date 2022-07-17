package com.skyd.imomoe.view.adapter.compose

import androidx.compose.ui.unit.dp
import com.skyd.imomoe.bean.AnimeEpisode1Bean
import com.skyd.imomoe.bean.HorizontalRecyclerView1Bean
import com.skyd.imomoe.bean.SearchHistory1Bean

object ItemSpace {
    val ITEM_SPACING = 12.dp
    val HORIZONTAL_PADDING = 16.dp

    private val noHorizontalMarginType: Set<Class<*>> = setOf(
        HorizontalRecyclerView1Bean::class.java,
        SearchHistory1Bean::class.java,
    )

    fun noHorizontalMargin(clz: Class<*>?): Boolean {
        clz ?: return true
        if (clz in noHorizontalMarginType) return true
        return false
    }

    private val needVerticalMarginType: Set<Class<*>> = setOf(
        AnimeEpisode1Bean::class.java,
    )

    fun needVerticalMargin(clz: Class<*>?): Boolean {
        clz ?: return false
        if (clz in needVerticalMarginType) return true
        return false
    }
}
