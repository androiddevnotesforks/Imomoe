package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class TabBean(
    override var actionUrl: String,
    var url: String,
    var title: String
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is TabBean -> false
        actionUrl == o.actionUrl && url == o.url && title == o.title -> true
        else -> false
    }
}
