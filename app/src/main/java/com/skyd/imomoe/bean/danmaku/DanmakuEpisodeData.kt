package com.skyd.imomoe.bean.danmaku

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DanmakuEpisodeData(
    @SerializedName("name")
    val name: String,
    @SerializedName("number")
    val number: String,
    @SerializedName("goodsId")
    val goodsId: String,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("id")
    val id: String,
) : Serializable