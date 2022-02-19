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
) : BaseBean {
    override fun equals(other: Any?): Boolean {
        return when {
            other !is ImageBean -> false
            actionUrl == other.actionUrl && url == other.url && referer == other.referer -> true
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = actionUrl.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + referer.hashCode()
        return result
    }
}