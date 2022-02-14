package com.skyd.imomoe.bean

import com.google.gson.annotations.SerializedName

class AnimeTypeBean(       //番剧类型：包括类型名和链接
    override var actionUrl: String,
    var url: String,
    var title: String
) : BaseBean

class AnimeAreaBean(       //番剧地区：包括地区名和链接
    override var actionUrl: String,
    var url: String,
    var title: String
) : BaseBean

class ImageBean(       //图片bean，带有referer信息
    @SerializedName("actionUrl")
    override var actionUrl: String,

    @SerializedName("url")
    var url: String,

    @SerializedName("referer")
    var referer: String
) : BaseBean