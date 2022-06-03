package com.skyd.imomoe.net.service

import com.skyd.imomoe.bean.danmaku.DanmakuData
import com.skyd.imomoe.bean.danmaku.DanmakuWrapper
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.util.Util.getAppVersionCode
import com.skyd.imomoe.util.Util.getAppVersionName
import com.skyd.imomoe.util.getOsInfo
import okhttp3.ResponseBody
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
        @Header("User-Agent") ua: String = "Imomoe ${getAppVersionName()}/${getAppVersionCode()} (${getOsInfo()})"
    ): DanmakuWrapper<DanmakuData.Data?>

    @GET
    suspend fun getCustomizeDanmaku(@Url url: String): ResponseBody

    // 查询弹幕
    @GET("${Api.DANMAKU_URL}/message/getSome")
    suspend fun getDanmaku(
        @Query("name") animeName: String,
        @Query("number") episode: String,
        @Query("type") type: String = "1",
        @Header("User-Agent") ua: String = "Imomoe ${getAppVersionName()}/${getAppVersionCode()} (${getOsInfo()})"
    ): DanmakuWrapper<DanmakuData?>

}