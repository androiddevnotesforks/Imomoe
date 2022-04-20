package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.ClassifyActivity

/**
 * 跳转到分类页面对应项目
 */
object ClassifyActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        if (context !is Activity) error("context isn't Activity")
        val partUrl = uri.getQueryParameter("partUrl")
        val classifyTitle = uri.getQueryParameter("classifyTitle")
        val classifyTabTitle = uri.getQueryParameter("classifyTabTitle")
        context.startActivity(
            Intent(context, ClassifyActivity::class.java)
                .putExtra("partUrl", partUrl)
                .putExtra("classifyTitle", classifyTitle)
                .putExtra("classifyTabTitle", classifyTabTitle)
        )
    }

    /**
     * query:
     * partUrl 目标网址
     * classifyTabTitle 分类标题，可选
     * classifyTitle 分类子项标题，可选
     */
    override val route: String
        get() = "${Route.SCHEME}://classify.anime.app"
}