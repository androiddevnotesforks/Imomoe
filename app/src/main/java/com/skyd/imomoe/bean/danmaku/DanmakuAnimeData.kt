package com.skyd.imomoe.bean.danmaku

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DanmakuAnimeData(
    @SerializedName("name")
    val name: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("type")
    val type: Int,
    @SerializedName("id")
    val id: String,
) : Serializable