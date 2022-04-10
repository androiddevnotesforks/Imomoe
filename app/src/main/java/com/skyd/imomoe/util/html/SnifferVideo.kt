package com.skyd.imomoe.util.html


object SnifferVideo {
    const val PARSE_URL_ERROR = -100
    const val KEY = "key"
    const val AC = "ac"
    const val VIDEO_ID = "id"
    const val SERVER_API = "api"
    const val DANMU_URL = "danmuUrl"
    const val REFEREER_URL = "referer"
    private val sniffingUrlList: MutableList<String> by lazy { ArrayList() }
    private var serverApi: String = "https://yuan.cuan.la/barrage"
    private var serverKey: String = "mao"
    private var videoId: String = ""
    private var referer: String = "http://tup.yhdm.so/"
}