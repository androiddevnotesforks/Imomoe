package com.skyd.imomoe.bean

class AnimeInfo1Bean(
    override var actionUrl: String,
    var title: String,
    var cover: ImageBean,
    var alias: String,
    var area: String,
    var year: String,
    var index: String,
    var animeType: List<AnimeTypeBean>,
    var tag: List<AnimeTypeBean>,
    var info: String
) : BaseBean

//每一集
class AnimeEpisodeDataBean(
    override var actionUrl: String,
    var title: String,
    var videoUrl: String = ""
) : BaseBean