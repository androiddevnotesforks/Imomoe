package com.skyd.imomoe.model.util

import com.skyd.imomoe.config.Const
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.HtmlService
import com.skyd.imomoe.util.Util.toEncodedUrl
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import kotlin.random.Random

object JsoupUtil {
    /**
     * 获取没有运行js的html
     */
    suspend fun getDocument(url: String): Document {
        return runCatching {
            Jsoup.parse(
                RetrofitManager.get().create(HtmlService::class.java).getHtml(
                    url.toEncodedUrl(),
                    Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
                ).string()
            )
        }.getOrThrow()
    }

    fun getDocumentSynchronously(url: String): Document {
        return Jsoup.parse(
            RetrofitManager.get().create(HtmlService::class.java).getHtmlSynchronously(
                url.toEncodedUrl(),
                Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
            ).execute().body()?.string() ?: ""
        )
    }

    /**
     * 指定解析类型
     */
    suspend fun getDocument(url: String, mediaType: MediaType): Document {
        return runCatching {
            Jsoup.parse(
                RetrofitManager.get().create(HtmlService::class.java).getHtml(
                    url.toEncodedUrl(),
                    Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
                ).byteString().toResponseBody(mediaType).string()
            )
        }.getOrThrow()
    }

    /**
     * 解析GB2312字符编码
     */
    suspend fun getDocumentByGB2312Type(url: String): Document {
        val mediaType = "text/html; charset=gb2312".toMediaType()
        return getDocument(url, mediaType)
    }
}
