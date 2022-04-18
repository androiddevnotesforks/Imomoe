package com.skyd.imomoe.net.service

import com.skyd.imomoe.bean.danmaku.*
import com.skyd.imomoe.config.Api
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface DanmakuService {
    @FormUrlEncoded
    @POST("${Api.DANMAKU_URL}/message/addOne")
    suspend fun sendDanmaku(
        @Field("content") content: String,
        @Field("time") time: Double,     // 秒时间戳
        @Field("episodeId") episodeId: String,
        @Field("type") type: String,
        @Field("color") color: String,
    ): DanmakuWrapper<DanmakuData.Data?>

    @GET
    suspend fun getCustomizeDanmaku(@Url url: String): ResponseBody

    // 查询弹幕
    @GET("${Api.DANMAKU_URL}/message/getSome")
    suspend fun getDanmaku(
        @Query("name") animeName: String,
        @Query("number") episode: String,
        @Query("type") type: String = "1",
    ): DanmakuWrapper<DanmakuData?>

}