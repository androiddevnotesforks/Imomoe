package com.skyd.imomoe.bean

class AnimeCover1Bean(
    override var actionUrl: String,
    // 网页地址
    var url: String,
    var title: String,
    var cover: ImageBean,
    var episode: String
) : BaseBean

class AnimeCover2Bean(
    override var actionUrl: String,
    var title: String
) : BaseBean

class AnimeCover3Bean(
    override var actionUrl: String,
    // 网页地址
    var url: String,
    var title: String,
    var cover: ImageBean,
    var episode: String,
    var describe: String,
    var animeType: List<AnimeTypeBean>
) : BaseBean

class AnimeCover4Bean(
    override var actionUrl: String,
    // 网页地址
    var url: String,
    var title: String,
    var cover: ImageBean
) : BaseBean

class AnimeCover5Bean(
    override var actionUrl: String,
    // 网页地址
    var url: String,
    var title: String,
    var area: AnimeAreaBean,
    var date: String,
    var episodeClickable: AnimeEpisodeDataBean
) : BaseBean

class AnimeCover6Bean(
    override var actionUrl: String,
    var title: String,
    var cover: ImageBean,
    var describe: String,
    var episodeClickable: AnimeEpisodeDataBean?
) : BaseBean

class AnimeCover7Bean(
    override var actionUrl: String,
    var title: String,
    var size: String? = null,           //视频大小，如300M
    var episodeCount: String? = null,    //集数
    // 0：/storage/emulated/0/Android/data/packagename/files
    // 1：/storage/emulated/0/
    var path: Int = 0
) : BaseBean

typealias AnimeCover8Bean = FavoriteAnimeBean

typealias AnimeCover9Bean = HistoryBean

class AnimeCover10Bean(
    override var actionUrl: String,
    var url: String,
    var title: String,
    var episodeClickable: AnimeEpisodeDataBean?
) : BaseBean

class AnimeCover11Bean(
    override var actionUrl: String,
    // 网页地址
    var url: String,
    var title: String
) : BaseBean

class AnimeCover12Bean(
    override var actionUrl: String,
    // 网页地址
    var url: String,
    var title: String,
    var episodeClickable: AnimeEpisodeDataBean
) : BaseBean