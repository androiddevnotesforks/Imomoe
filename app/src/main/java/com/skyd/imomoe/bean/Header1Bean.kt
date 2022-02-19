package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class Header1Bean(
    override var actionUrl: String,
    var title: String
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is Header1Bean -> false
        actionUrl == o.actionUrl && title == o.title -> true
        else -> false
    }
}