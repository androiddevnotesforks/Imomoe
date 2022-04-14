package com.skyd.imomoe.bean.danmaku

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class DanmakuWrapper<T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val data: T,
    @SerializedName("msg")
    val msg: String,
) : Serializable