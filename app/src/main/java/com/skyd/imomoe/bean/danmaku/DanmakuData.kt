package com.skyd.imomoe.bean.danmaku

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DanmakuData(
    @SerializedName("episodeId")
    val episodeId: String,
    @SerializedName("data")
    val data: List<Data>,
    @SerializedName("total")
    val total: Int,
) : Serializable {
    class Data(
        @SerializedName("content")
        val content: String,
        @SerializedName("time")
        val time: Double,
        @SerializedName("color")
        val color: String?,
        @SerializedName("type")
        val type: String,
        @SerializedName("episodeId")
        val episodeId: String,
        @SerializedName("ip")
        val ip: String,
        @SerializedName("createdAt")
        val createdAt: String,
        @SerializedName("updatedAt")
        val updatedAt: String,
        @SerializedName("id")
        val id: String,
    ) : Serializable
}

