package com.skyd.imomoe.view.adapter.compose

import com.skyd.imomoe.bean.AnimeCover9Bean
import org.fourthline.cling.model.meta.Device

const val MAX_SPAN_SIZE = 60
fun animeShowSpan(data: Any): Int = when (data) {
    is AnimeCover9Bean -> MAX_SPAN_SIZE
    is Device<*, *, *> -> MAX_SPAN_SIZE
    else -> MAX_SPAN_SIZE / 3
}