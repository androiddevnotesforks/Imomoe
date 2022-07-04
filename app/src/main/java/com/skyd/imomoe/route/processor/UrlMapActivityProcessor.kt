package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.UrlMapActivity

/**
 * 跳转到URL前缀替换页面
 */
object UrlMapActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        if (context !is Activity) error("context isn't Activity")
        val jsonData = uri.getQueryParameter(JSON_DATA)
        val autoAdd = uri.getQueryParameter(AUTO_ADD)
        val autoAddAndFinish = uri.getQueryParameter(AUTO_ADD_AND_FINISH)
        val enabled = uri.getQueryParameter(ENABLED)
        context.startActivity(
            Intent(context, UrlMapActivity::class.java)
                .putExtra(UrlMapActivity.JSON_DATA, jsonData)
                .putExtra(UrlMapActivity.AUTO_ADD, autoAdd?.toBooleanStrictOrNull() ?: false)
                .putExtra(UrlMapActivity.ENABLED, enabled?.toBooleanStrictOrNull() ?: false)
                .putExtra(
                    UrlMapActivity.AUTO_ADD_AND_FINISH,
                    autoAddAndFinish?.toBooleanStrictOrNull() ?: false
                )
        )
    }

    fun startActivityForResult() {

    }

    /**
     * query:
     * jsonData 批量添加URL替换的json数据
     */
    override val route: String
        get() = "${Route.SCHEME}://urlMap.anime.app"

    const val ENABLED = "enabled"
    const val JSON_DATA = "jsonData"
    const val AUTO_ADD = "autoAdd"
    const val AUTO_ADD_AND_FINISH = "autoAddAndFinish"
}