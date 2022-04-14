package com.skyd.imomoe.net.service

import com.skyd.imomoe.bean.danmaku.*
import com.skyd.imomoe.config.Api
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface DanmakuService {
    @Headers(value = ["Content-Type: application/json", "Accept: application/json"])
    @POST(Api.DANMAKU_URL)
    fun sendDanmaku(
        @Query("ac") ac: String,
        @Query("key") key: String,
        @Body json: RequestBody
    ): Call<AnimeSendDanmakuResultBean>

    @GET
    fun getCustomizeDanmaku(@Url url: String): Call<ResponseBody>
    
    // 查询弹幕
    @GET("${Api.DANMAKU_URL}/api/message/getSome")
    suspend fun getDanmaku(
        @Query("episodeId") episodeId: String,
    ): DanmakuWrapper<DanmakuData>

    // 查询单个集信息
    @GET("${Api.DANMAKU_URL}/api/episode/getOneByNumer")
    suspend fun getEpisodeDanmaku(
        @Query("number") number: String,
        @Query("goodsId") goodsId: String
    ): DanmakuWrapper<DanmakuEpisodeData>

    // 查询资源列表
    @GET("${Api.DANMAKU_URL}/api/goods/getSome")
    suspend fun getAnimeDanmakuId(
        @Query("name") name: String,
        @Query("type") type: Int = 1
    ): DanmakuWrapper<List<DanmakuAnimeData>>
}