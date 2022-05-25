package com.skyd.imomoe.bean

import com.skyd.imomoe.util.compare.EpisodeTitleCompareUtil
import com.skyd.imomoe.view.adapter.variety.Diff


class AnimeCover1Bean(
    override var route: String,
    // 网页地址
    var url: String,
    var title: String,
    var cover: ImageBean,
    var episode: String
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover1Bean -> false
        route == o.route && url == o.url && title == o.title &&
                cover == o.cover && episode == o.episode -> true
        else -> false
    }
}

class AnimeCover2Bean(
    override var route: String,
    var title: String
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover2Bean -> false
        route == o.route && title == o.title -> true
        else -> false
    }
}

class AnimeCover3Bean(
    override var route: String = "",
    // 网页地址
    var url: String? = null,
    var title: String? = null,
    var cover: ImageBean? = null,
    var episode: String? = null,
    var describe: String? = null,
    var animeType: List<AnimeTypeBean>? = null
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover3Bean -> false
        route == o.route && url == o.url && title == o.title && cover == o.cover &&
                episode == o.episode && describe == o.describe && animeType == o.animeType -> true
        else -> false
    }
}

class AnimeCover4Bean(
    override var route: String,
    // 网页地址
    var url: String,
    var title: String,
    var cover: ImageBean
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover4Bean -> false
        route == o.route && url == o.url && title == o.title && cover == o.cover -> true
        else -> false
    }
}

class AnimeCover5Bean(
    override var route: String,
    // 网页地址
    var url: String,
    var title: String,
    var area: AnimeAreaBean,
    var date: String,
    var episodeClickable: AnimeEpisodeDataBean
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover5Bean -> false
        route == o.route && url == o.url && title == o.title && area == o.area &&
                date == o.date && episodeClickable == o.episodeClickable -> true
        else -> false
    }
}

class AnimeCover6Bean(
    override var route: String = "",
    var title: String? = null,
    var cover: ImageBean? = null,
    var describe: String? = null,
    var episodeClickable: AnimeEpisodeDataBean? = null
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover6Bean -> false
        route == o.route && title == o.title && cover == o.cover &&
                describe == o.describe && episodeClickable == o.episodeClickable -> true
        else -> false
    }
}

class AnimeCover7Bean(
    override var route: String,
    var title: String,
    var size: String? = null,           //视频大小，如300M
    var episodeCount: String? = null,    //集数
    var path: String,
    // 0：/storage/emulated/0/Android/data/packagename/files
    // 1：/storage/emulated/0/
    var pathType: Int = 0,
) : BaseBean, Diff, Comparable<AnimeCover7Bean> {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover7Bean -> false
        route == o.route && title == o.title && size == o.size &&
                episodeCount == o.episodeCount && path == o.path && pathType == o.pathType -> true
        else -> false
    }

    override fun compareTo(other: AnimeCover7Bean): Int =
        EpisodeTitleCompareUtil.compare(title, other.title)
}

typealias AnimeCover8Bean = FavoriteAnimeBean

typealias AnimeCover9Bean = HistoryBean

class AnimeCover10Bean(
    override var route: String,
    var url: String,
    var title: String,
    var episodeClickable: AnimeEpisodeDataBean?
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover10Bean -> false
        route == o.route && url == o.url && title == o.title &&
                episodeClickable == o.episodeClickable -> true
        else -> false
    }
}

class AnimeCover11Bean(
    override var route: String,
    // 网页地址
    var url: String,
    var title: String
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover11Bean -> false
        route == o.route && url == o.url && title == o.title -> true
        else -> false
    }
}

class AnimeCover12Bean(
    override var route: String,
    // 网页地址
    var url: String,
    var title: String,
    var episodeClickable: AnimeEpisodeDataBean
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is AnimeCover12Bean -> false
        route == o.route && url == o.url && title == o.title &&
                episodeClickable == o.episodeClickable -> true
        else -> false
    }
}