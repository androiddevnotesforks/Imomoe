package com.skyd.imomoe.bean

import com.skyd.imomoe.util.compare.EpisodeTitleCompareUtil
import com.skyd.imomoe.view.adapter.variety.Diff

class AnimeInfo1Bean(
    override var route: String,
    var title: String?,
    var cover: ImageBean?,
    var alias: String?,
    var area: String?,
    var year: String?,
    var index: String?,
    var animeType: List<AnimeTypeBean>?,
    var tag: List<AnimeTypeBean>?,
    var info: String?
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeInfo1Bean -> false
        route == o.route && title == o.title && cover == o.cover &&
                alias == o.alias && area == o.area && year == o.year && index == o.index &&
                animeType == o.animeType && tag == o.tag && info == o.info -> true
        else -> false
    }
}

//每一集
class AnimeEpisodeDataBean(
    override var route: String,
    var title: String,
    var videoUrl: String = ""
) : BaseBean, Diff, Comparable<AnimeEpisodeDataBean> {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeEpisodeDataBean -> false
        route == o.route && title == o.title && videoUrl == o.videoUrl -> true
        else -> false
    }

    // actionUrl相同的排序到一块
    override fun compareTo(other: AnimeEpisodeDataBean): Int {
        val actionUrlComp = EpisodeTitleCompareUtil.compare(route, other.route)
        if (actionUrlComp != 0) return actionUrlComp
        return EpisodeTitleCompareUtil.compare(title, other.title)
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is AnimeEpisodeDataBean) false
        else route == other.route && title == other.title && videoUrl == other.videoUrl
    }

    override fun hashCode(): Int {
        var result = route.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + videoUrl.hashCode()
        return result
    }
}