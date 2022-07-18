package com.skyd.imomoe.view.adapter.compose

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.bean.AnimeCover7Bean
import com.skyd.imomoe.bean.AnimeEpisode1Bean
import com.skyd.imomoe.bean.HorizontalRecyclerView1Bean
import com.skyd.imomoe.bean.SearchHistory1Bean
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize

object AnimeItemSpace {
    val ITEM_SPACING = 12.dp
    val HORIZONTAL_PADDING = 16.dp

    fun Modifier.animeItemSpace(item: Any, spanSize: Int, spanIndex: Int) =
        this.padding(getItemSpace(item, spanSize, spanIndex))

    fun getItemSpace(item: Any, spanSize: Int, spanIndex: Int): PaddingValues {
        var top = 0.dp
        var bottom = 0.dp
        var start = 0.dp
        var end = 0.dp
        if (needVerticalMargin(item.javaClass)) {
            top = 10.dp
            bottom = 2.dp
        }
        if (spanSize == AnimeShowSpanSize.MAX_SPAN_SIZE) {
            /**
             * 只有一列
             */
            if (noHorizontalMargin(item.javaClass)) {
                return PaddingValues(top = top, bottom = bottom, start = start, end = end)
            }
            start = HORIZONTAL_PADDING
            end = HORIZONTAL_PADDING
        } else if (spanSize == AnimeShowSpanSize.MAX_SPAN_SIZE / 2) {
            /**
             * 只有两列，没有在中间的item
             * 2x = ITEM_SPACING
             */
            val x = ITEM_SPACING / 2f
            if (spanIndex == 0) {
                start = HORIZONTAL_PADDING
                end = x
            } else {
                start = x
                end = HORIZONTAL_PADDING
            }
        } else if (spanSize == AnimeShowSpanSize.MAX_SPAN_SIZE / 3) {
            /**
             * 只有三列，一个在中间的item
             * HORIZONTAL_PADDING + x = 2y
             * x + y = ITEM_SPACING
             */
            val y = (HORIZONTAL_PADDING + ITEM_SPACING) / 3f
            val x = ITEM_SPACING - y
            if (spanIndex == 0) {
                start = HORIZONTAL_PADDING
                end = x
            } else if (spanIndex + spanSize == AnimeShowSpanSize.MAX_SPAN_SIZE) {
                // 最右侧最后一个
                start = x
                end = HORIZONTAL_PADDING
            } else {
                start = y
                end = y
            }
        } else if (spanSize == AnimeShowSpanSize.MAX_SPAN_SIZE / 5) {
            /**
             * 只有五列
             * HORIZONTAL_PADDING + x = y + z
             * x + y = ITEM_SPACING
             * z + (HORIZONTAL_PADDING + x) / 2 = ITEM_SPACING
             */
            val x = (ITEM_SPACING * 4 - HORIZONTAL_PADDING * 3) / 5f
            val y = ITEM_SPACING - x
            val z = HORIZONTAL_PADDING + x - y
            if (spanIndex == 0) {
                // 最左侧第一个
                start = HORIZONTAL_PADDING
                end = x
            } else if (spanIndex + spanSize == AnimeShowSpanSize.MAX_SPAN_SIZE) {
                // 最右侧最后一个
                start = x
                end = HORIZONTAL_PADDING
            } else if (spanIndex == spanSize) {
                // 第二个
                start = y
                end = z
            } else if (spanIndex == AnimeShowSpanSize.MAX_SPAN_SIZE - 2 * spanSize) {
                // 倒数第二个
                start = z
                end = y
            } else {
                // 最中间的
                start = (HORIZONTAL_PADDING + x) / 2f
                end = (HORIZONTAL_PADDING + x) / 2f
            }
        } else {
            /**
             * 多于三列（不包括五列），有在中间的item
             */
            if ((AnimeShowSpanSize.MAX_SPAN_SIZE / spanSize) % 2 == 0) {
                /**
                 * 偶数个item
                 * HORIZONTAL_PADDING + x = y + ITEM_SPACING / 2
                 * x + y = ITEM_SPACING
                 */
                val y = (HORIZONTAL_PADDING + ITEM_SPACING / 2f) / 2f
                val x = ITEM_SPACING - y
                if (spanIndex == 0) {
                    // 最左侧第一个
                    start = HORIZONTAL_PADDING
                    end = x
                } else if (spanIndex + spanSize == AnimeShowSpanSize.MAX_SPAN_SIZE) {
                    // 最右侧最后一个
                    start = x
                    end = HORIZONTAL_PADDING
                } else {
                    // 中间的项目
                    if (spanIndex < AnimeShowSpanSize.MAX_SPAN_SIZE / 2) {
                        // 左侧部分
                        start = y
                        end = ITEM_SPACING / 2
                    } else {
                        // 右侧部分
                        start = ITEM_SPACING / 2
                        end = y
                    }
                }
            } else {
                /**
                 * 奇数个item，严格大于5的奇数（暂无需求，未实现）
                 */
            }
        }
        return PaddingValues(top = top, bottom = bottom, start = start, end = end)
    }

    private val noHorizontalMarginType: Set<Class<*>> = setOf(
        HorizontalRecyclerView1Bean::class.java,
        SearchHistory1Bean::class.java,
        AnimeCover7Bean::class.java,
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
