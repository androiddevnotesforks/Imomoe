package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.SearchActivity

/**
 * 跳转到搜索界面
 */
object SearchActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        if (context !is Activity) error("context isn't Activity")
        val keyword = uri.getQueryParameter("keyword")
        val pageNumber = uri.getQueryParameter("pageNumber")
        context.startActivity(
            Intent(context, SearchActivity::class.java)
                .putExtra("keyword", keyword)
                .putExtra("pageNumber", pageNumber)
        )
    }

    /**
     * query:
     * keyword 关键词
     * pageNumber 第几页
     */
    override val route: String
        get() = "${Route.SCHEME}://search.anime.app"
}