package com.skyd.imomoe.bean

class PlayBean(
    override var route: String,
    var title: AnimeTitleBean,
    var episode: AnimeEpisodeDataBean,
    var detailPartUrl: String,
    var data: List<Any>
) : BaseBean

//番剧详情下方信息rv数据
class AnimeTitleBean(
    override var route: String,
    var title: String
) : BaseBean