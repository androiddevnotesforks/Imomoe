package com.skyd.imomoe.view.adapter.compose

import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.ext.screenIsLand
import org.fourthline.cling.model.meta.Device

const val MAX_SPAN_SIZE = 60
fun animeShowSpan(
    data: Any,
    enableLandScape: Boolean = true
): Int = if (enableLandScape && appContext.screenIsLand) {
    when (data) {
        is SkinCover1Bean,
        is More1Bean -> MAX_SPAN_SIZE / 3
        is AnimeCover7Bean,
        is AnimeCover9Bean,
        is Device<*, *, *> -> MAX_SPAN_SIZE
        is AnimeCover8Bean -> MAX_SPAN_SIZE / 5
        else -> MAX_SPAN_SIZE / 3
    }
} else {
    when (data) {
        is SkinCover1Bean,
        is More1Bean -> MAX_SPAN_SIZE / 2
        is AnimeCover7Bean,
        is AnimeCover9Bean,
        is Device<*, *, *> -> MAX_SPAN_SIZE
        is AnimeCover8Bean -> MAX_SPAN_SIZE / 3
        else -> MAX_SPAN_SIZE / 3
    }
}