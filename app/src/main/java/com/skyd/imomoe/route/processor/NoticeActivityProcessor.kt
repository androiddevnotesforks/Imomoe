package com.skyd.imomoe.route.processor

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.activity.NoticeActivity

/**
 * 显示通知
 */
object NoticeActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        context ?: error("context is null")
        val paramString: String =uri.encodedQuery.orEmpty()
        if (paramString.isBlank()) {
            appContext.getString(R.string.notice_activity_error_param).showToast()
            return
        }
        context.startActivity(
            Intent(context, NoticeActivity::class.java)
                .putExtra(NoticeActivity.PARAM, paramString)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    override val route: String
        get() = "${Route.SCHEME}://notice.anime.app"
}