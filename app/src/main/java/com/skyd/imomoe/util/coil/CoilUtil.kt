package com.skyd.imomoe.util.coil

import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.Coil
import coil.ImageLoader
import coil.annotation.ExperimentalCoilApi
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
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

    internal lateinit var imageLoader: ImageLoader

    init {
        setOkHttpClient(okhttpClient)
    }

    fun setOkHttpClient(okHttpClient: OkHttpClient) {
        imageLoaderBuilder.okHttpClient(
            okHttpClient.newBuilder().build()
        ).build().apply {
            imageLoader = this
            Coil.setImageLoader(this)
        }
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

@Composable
fun AnimeAsyncImage(
    url: String?,
    referer: String? = null,
    contentDescription: String? = null,
    imageLoader: ImageLoader = CoilUtil.imageLoader,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
    error: Painter? = painterResource(id = R.drawable.ic_warning_24),
    fallback: Painter? = error,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
    val context = LocalContext.current

    if (url.isNullOrBlank()) {
        logE("loadImage", "cover image url must not be null or empty")
        return
    }

    val newUrl = if (url.startsWith("//")) url.replaceFirst("//", "https://") else url

    // 是网络图片
    var amendReferer: String? = referer
    if (amendReferer?.startsWith(MAIN_URL) == false)
        amendReferer = MAIN_URL//"http://www.yhdm.io/"
    if (referer == MAIN_URL) amendReferer += "/"

    imageLoader.enqueue(ImageRequest.Builder(context).apply {
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
    }.build())

    AsyncImage(
        model = newUrl,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        placeholder = placeholder,
        error = fallback,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )
}

@Composable
fun AnimeAsyncImage(
    @DrawableRes res: Int,
    contentDescription: String?,
    imageLoader: ImageLoader = CoilUtil.imageLoader,
    modifier: Modifier = Modifier,
    placeholder: Painter? = null,
    error: Painter? = painterResource(id = R.drawable.ic_warning_24),
    fallback: Painter? = error,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {
    AsyncImage(
        model = res,
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        placeholder = placeholder,
        error = fallback,
        onLoading = onLoading,
        onSuccess = onSuccess,
        onError = onError,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
        filterQuality = filterQuality
    )
}