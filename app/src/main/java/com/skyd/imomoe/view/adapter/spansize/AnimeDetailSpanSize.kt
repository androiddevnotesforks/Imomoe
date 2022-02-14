package com.skyd.imomoe.view.adapter.spansize

import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.bean.AnimeDescribe1Bean
import com.skyd.imomoe.bean.AnimeInfo1Bean
import com.skyd.imomoe.bean.Header1Bean
import com.skyd.imomoe.bean.HorizontalRecyclerView1Bean
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeDetailSpanSize(val adapter: VarietyAdapter) :
    GridLayoutManager.SpanSizeLookup() {
    override fun getSpanSize(position: Int): Int {
        return when(adapter.dataList[position]) {
            is Header1Bean -> 4
            is HorizontalRecyclerView1Bean -> 4
            is AnimeDescribe1Bean -> 4
            is AnimeInfo1Bean -> 4
            else -> 1
        }
    }
}