package com.skyd.imomoe.view.adapter.spansize

import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.ext.screenIsLand
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeShowSpanSize(
    val adapter: VarietyAdapter,
    val enableLandScape: Boolean = true
) : GridLayoutManager.SpanSizeLookup() {
    companion object {
        const val MAX_SPAN_SIZE = 60
    }

    override fun getSpanSize(position: Int): Int {
        return if (enableLandScape && appContext.screenIsLand) {
            when (adapter.dataList[position]) {
                is Header1Bean,
                is Banner1Bean,
                is AnimeDescribe1Bean,
                is AnimeInfo1Bean,
                is SearchHistory1Bean,
                is AnimeDownload1Bean,
                is HorizontalRecyclerView1Bean -> MAX_SPAN_SIZE
                is AnimeEpisode1Bean,
                is AnimeCover1Bean -> MAX_SPAN_SIZE / 5
                is AnimeCover3Bean,
                is AnimeCover5Bean,
                is AnimeCover11Bean -> MAX_SPAN_SIZE / 2
                else -> MAX_SPAN_SIZE / 3
            }
        } else {
            when (adapter.dataList[position]) {
                is Header1Bean,
                is Banner1Bean,
                is AnimeDescribe1Bean,
                is AnimeInfo1Bean,
                is AnimeCover3Bean,
                is AnimeCover5Bean,
                is AnimeCover11Bean,
                is SearchHistory1Bean,
                is AnimeDownload1Bean,
                is HorizontalRecyclerView1Bean -> MAX_SPAN_SIZE
                is AnimeEpisode1Bean,
                is AnimeCover1Bean -> MAX_SPAN_SIZE / 3
                is AnimeCover4Bean -> MAX_SPAN_SIZE / 2
                else -> MAX_SPAN_SIZE / 3
            }
        }
    }
}