package com.skyd.imomoe.adsapi.websource

import okhttp3.MediaType
import org.jsoup.nodes.Document

object JsoupUtil {
    /**
     * 获取没有运行js的html
     */
    suspend fun getDocument(url: String): Document =
        com.skyd.imomoe.model.util.JsoupUtil.getDocument(url)

    fun getDocumentSynchronously(url: String): Document =
        com.skyd.imomoe.model.util.JsoupUtil.getDocumentSynchronously(url)

    /**
     * 指定解析类型
     */
    suspend fun getDocument(url: String, mediaType: MediaType): Document =
        com.skyd.imomoe.model.util.JsoupUtil.getDocument(url, mediaType)
}