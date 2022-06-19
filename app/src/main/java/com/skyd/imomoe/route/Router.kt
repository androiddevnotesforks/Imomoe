package com.skyd.imomoe.route

import android.content.Context
import android.net.Uri
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.route.processor.*
import com.skyd.imomoe.util.showToast

object Router {
    fun String.buildRouteUri(block: Uri.Builder.() -> Unit = {}): Uri {
        return Uri.parse(this).buildUpon().apply { block() }.build()
    }

    fun String.route(context: Context?) = buildRouteUri().route(context)

    fun Uri.route(context: Context?) {
        try {
            // 数据源路由优先
            if (DataSourceManager.getRouter()?.route(this, context) == true) return
            val prefix = "$scheme://$authority"
            var consumed = false
            processorMap.forEach {
                if (it.key == prefix) {
                    it.value.process(this, context)
                    consumed = true
                    return@forEach
                }
            }
            if (!consumed) {
                appContext.getString(R.string.unknown_route, this.toString()).showToast()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            e.message?.showToast()
        }
    }

    val processorMap: HashMap<String, Processor> = hashMapOf(
        ClassifyActivityProcessor.route to ClassifyActivityProcessor,
        DetailActivityProcessor.route to DetailActivityProcessor,
        EpisodeDownloadProcessor.route to EpisodeDownloadProcessor,
        JumpByUrlProcessor.route to JumpByUrlProcessor,
        MonthAnimeActivityProcessor.route to MonthAnimeActivityProcessor,
        NoticeActivityProcessor.route to NoticeActivityProcessor,
        OpenBrowserProcessor.route to OpenBrowserProcessor,
        PlayActivityProcessor.route to PlayActivityProcessor,
        PlayDownloadProcessor.route to PlayDownloadProcessor,
        PlayDownloadM3U8Processor.route to PlayDownloadM3U8Processor,
        RankActivityProcessor.route to RankActivityProcessor,
        SearchActivityProcessor.route to SearchActivityProcessor,
        StartActivityProcessor.route to StartActivityProcessor,
        ConfigDataSourceActivityProcessor.route to ConfigDataSourceActivityProcessor,
        UrlMapActivityProcessor.route to UrlMapActivityProcessor,
    )

}