package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.ConfigDataSourceActivity

/**
 * 跳转到配置数据源
 */
object ConfigDataSourceActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        if (context !is Activity) error("context isn't Activity")
        val selectPageIndex = uri.getQueryParameter("selectPageIndex")?.toIntOrNull() ?: 0
        context.startActivity(
            Intent(context, ConfigDataSourceActivity::class.java)
                .putExtra(ConfigDataSourceActivity.SELECT_PAGE_INDEX, selectPageIndex)
        )
    }

    /**
     * query:
     * selectPageIndex 0表示默认显示本地数据页面；1表示显示数据源商店页面
     */
    override val route: String
        get() = "${Route.SCHEME}://datasource.anime.app"
}