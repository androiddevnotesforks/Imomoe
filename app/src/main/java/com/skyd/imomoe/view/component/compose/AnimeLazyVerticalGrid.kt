package com.skyd.imomoe.view.component.compose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.ext.plus
import com.skyd.imomoe.view.adapter.compose.ItemSpace
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import com.skyd.imomoe.view.adapter.compose.MAX_SPAN_SIZE
import com.skyd.imomoe.view.adapter.compose.animeShowSpan

@Composable
fun AnimeLazyVerticalGrid(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(
        vertical = 16.dp,
        horizontal = ItemSpace.HORIZONTAL_PADDING
    ) + WindowInsets.navigationBars.asPaddingValues(),
    dataList: List<Any>,
    adapter: LazyGridAdapter
) {
    val listState = rememberLazyGridState()
    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Fixed(MAX_SPAN_SIZE),
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(ItemSpace.ITEM_SPACING),
        verticalArrangement = Arrangement.spacedBy(ItemSpace.ITEM_SPACING),
        contentPadding = contentPadding
    ) {
        itemsIndexed(
            items = dataList,
            span = { _, item ->
                GridItemSpan(animeShowSpan(item))
            }
        ) { index, item ->
            adapter.draw(index = index, data = item)
        }
    }
}