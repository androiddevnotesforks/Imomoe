package com.skyd.imomoe.view.component.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.skyd.imomoe.view.adapter.compose.AnimeItemSpace.animeItemSpace
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import com.skyd.imomoe.view.adapter.compose.MAX_SPAN_SIZE
import com.skyd.imomoe.view.adapter.compose.animeShowSpan

@Composable
fun AnimeLazyVerticalGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    dataList: List<Any>,
    adapter: LazyGridAdapter,
    enableLandScape: Boolean = true,     // 是否启用横屏使用另一套布局方案
    key: ((index: Int, item: Any) -> Any)? = null
) {
    val listState = rememberLazyGridState()
    val spanIndexArray: MutableList<Int> = remember { mutableListOf() }
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(MAX_SPAN_SIZE),
        state = listState,
        contentPadding = contentPadding
    ) {
        itemsIndexed(
            items = dataList,
            key = key,
            span = { index, item ->
                val spanIndex = maxLineSpan - maxCurrentLineSpan
                if (spanIndexArray.size > index) spanIndexArray[index] = spanIndex
                else spanIndexArray.add(spanIndex)
                GridItemSpan(animeShowSpan(item, enableLandScape))
            }
        ) { index, item ->
            adapter.draw(
                modifier = Modifier.animeItemSpace(
                    item = item,
                    spanSize = animeShowSpan(item),
                    spanIndex = spanIndexArray[index]
                ),
                index = index,
                data = item
            )
        }
    }
}