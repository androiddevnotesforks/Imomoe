package com.skyd.imomoe.util.coil

import android.widget.ImageView
import androidx.annotation.DrawableRes
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import coil.load
import coil.request.ImageRequest
import coil.util.DebugLogger
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.config.Api.Companion.MAIN_URL
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.net.okhttpClient
import com.skyd.imomoe.util.Util.toEncodedUrl
import com.skyd.imomoe.util.debug
import com.skyd.imomoe.util.logE
import okhttp3.OkHttpClient
import java.net.URL
import kotlin.random.Random


object CoilUtil {
    private val imageLoaderBuilder = ImageLoader.Builder(appContext)
        .crossfade(400)
        .apply { debug { logger(DebugLogger()) } }

    init {
        setOkHttpClient(okhttpClient)
    }

    fun setOkHttpClient(okHttpClient: OkHttpClient) {
        imageLoaderBuilder.okHttpClient(
            okHttpClient.newBuilder().build()
        ).build().apply { Coil.setImageLoader(this) }
    }

    fun ImageView.loadImage(
        url: String?,
        builder: ImageRequest.Builder.() -> Unit = {},
    ) {
        if (url.isNullOrBlank()) {
            logE("loadImage", "cover image url must not be null or empty")
            return
        }

        val newUrl = if (url.startsWith("//")) url.replaceFirst("//", "https://") else url

        this.load(newUrl, builder = builder)
    }

    fun ImageView.loadImage(
        res: Int,
    ) {
        loadImage(res.toString(), referer = null)
    }

    fun ImageView.loadImage(
        url: String?,
        referer: String? = null,
        @DrawableRes placeholder: Int = 0,
        @DrawableRes error: Int = R.drawable.ic_warning_24
    ) {
        if (url.isNullOrBlank()) {
            logE("loadImage", "cover image url must not be null or empty")
            return
        }

        // 是本地drawable
        url.toIntOrNull()?.let { drawableResId ->
            load(drawableResId) {
                placeholder(placeholder)
                error(error)
            }
            return
        }

        val newUrl = if (url.startsWith("//")) url.replaceFirst("//", "https://") else url

        // 是网络图片
        var amendReferer: String? = referer
        if (amendReferer?.startsWith(MAIN_URL) == false)
            amendReferer = MAIN_URL//"http://www.yhdm.io/"
        if (referer == MAIN_URL) amendReferer += "/"

        runCatching {
            loadImage(newUrl) {
                placeholder(placeholder)
                error(error)
                amendReferer?.let { ref ->
                    addHeader("Referer", ref.toEncodedUrl())
                }
                addHeader("Host", URL(newUrl).host)
                addHeader("Accept", "*/*")
                addHeader("Accept-Encoding", "gzip, deflate")
                addHeader("Connection", "keep-alive")
                addHeader(
                    "User-Agent",
                    Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
                )
            }
        }.onFailure {
            it.printStackTrace()
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    fun clearMemoryDiskCache() {
        appContext.imageLoader.memoryCache?.clear()
        Coil.imageLoader(appContext).diskCache?.clear()
    }
}