package com.skyd.imomoe.bean

import com.google.gson.annotations.SerializedName

class AnimeTypeBean(       //番剧类型：包括类型名和链接
    override var route: String = "",
    var url: String? = null,
    var title: String? = null
) : BaseBean

class AnimeAreaBean(       //番剧地区：包括地区名和链接
    override var route: String = "",
    var url: String? = null,
    var title: String? = null
) : BaseBean

class ImageBean(       //图片bean，带有referer信息
    @SerializedName("actionUrl")
    override var route: String = "",

    @SerializedName("url")
    var url: String? = null,

    @SerializedName("referer")
    var referer: String? = null
) : BaseBean {
    override fun equals(other: Any?): Boolean {
        return when {
            other !is ImageBean -> false
            route == other.route && url == other.url && referer == other.referer -> true
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = route.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + referer.hashCode()
        return result
    }
}