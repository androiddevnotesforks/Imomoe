package com.skyd.imomoe.view.adapter.spansize

import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeShowSpanSize(val adapter: VarietyAdapter) :
    GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return if (appContext.resources.getBoolean(R.bool.is_landscape)) {
            when (adapter.dataList[position]) {
                is Header1Bean -> 4
                is Banner1Bean -> 4
                is AnimeCover3Bean -> 2
                is AnimeCover5Bean -> 2
                is AnimeCover11Bean -> 2
                else -> 1
            }
        } else {
            when (adapter.dataList[position]) {
                is Header1Bean -> 4
                is Banner1Bean -> 4
                is AnimeCover3Bean -> 4
                is AnimeCover5Bean -> 4
                is AnimeCover11Bean -> 4
                else -> 1
            }
        }
    }
}