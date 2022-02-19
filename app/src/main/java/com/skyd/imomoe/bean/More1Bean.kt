package com.skyd.imomoe.bean

import androidx.annotation.DrawableRes
import com.skyd.imomoe.view.adapter.variety.Diff

class More1Bean(
    override var actionUrl: String,
    var title: String,
    @DrawableRes
    var image: Int
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is More1Bean -> false
        actionUrl == o.actionUrl && title == o.title && image == o.image -> true
        else -> false
    }
}
