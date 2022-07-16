package com.skyd.imomoe.view.adapter.compose

import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import com.skyd.imomoe.bean.AnimeCover9Bean

const val MAX_SPAN_SIZE = 60

fun animeShowSpan(list: List<Any>): (LazyGridItemSpanScope.(index: Int) -> GridItemSpan) = {
    when (list[it]) {
        is AnimeCover9Bean -> GridItemSpan(MAX_SPAN_SIZE)
        else -> GridItemSpan(MAX_SPAN_SIZE / 3)
    }
}