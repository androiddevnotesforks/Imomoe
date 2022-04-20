package com.skyd.imomoe.route.processor

import android.app.Activity
import android.content.Context
import android.net.Uri
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.showInputDialog
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.util.showToast
import java.net.URL

/**
 * 根据网址跳转
 */
object JumpByUrlProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        if (context !is Activity) error("JumpByUrlProcessor: context isn't Activity")
        var destinationUrl = uri.getQueryParameter("url").orEmpty()
        if (destinationUrl.isBlank() || destinationUrl == "/") {
            context.showInputDialog(
                hint = context.getString(R.string.input_a_website)
            ) { _, _, text ->
                try {
                    var url = text.toString()
                    if (!url.matches(Regex("^.+://.*"))) url = "http://$url"
                    URL(url).file.route(context)
                } catch (e: Exception) {
                    appContext.getString(R.string.website_format_error).showToast()
                    e.printStackTrace()
                }
            }
        } else {
            try {
                if (!destinationUrl.matches(Regex("^.+://.*"))) {
                    destinationUrl = "http://$destinationUrl"
                }
                URL(destinationUrl).file.route(context)
            } catch (e: Exception) {
                appContext.getString(R.string.website_format_error).showToast()
                e.printStackTrace()
            }
        }
    }

    /**
     * query:
     * url 网页url。可选，若不设置，则弹出对话框输入
     */
    override val route: String
        get() = "${Route.SCHEME}://jumpByUrl.anime.app"
}