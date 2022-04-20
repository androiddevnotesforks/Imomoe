package com.skyd.imomoe.route.processor

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.skyd.imomoe.route.Route
import com.skyd.imomoe.view.activity.RankActivity

/**
 * 跳转到排行榜界面
 */
object RankActivityProcessor : Processor() {
    override fun process(uri: Uri, context: Context?) {
        context!!
        context.startActivity(Intent(context, RankActivity::class.java))
    }

    /**
     * query:
     */
    override val route: String
        get() = "${Route.SCHEME}://rank.anime.app"
}