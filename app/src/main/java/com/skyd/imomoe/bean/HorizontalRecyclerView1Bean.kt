package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class HorizontalRecyclerView1Bean(
    override var actionUrl: String,
    var episodeList: List<AnimeEpisodeDataBean>
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is HorizontalRecyclerView1Bean -> false
        actionUrl == o.actionUrl && episodeList == o.episodeList -> true
        else -> false
    }
}