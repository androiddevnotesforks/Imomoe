package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class Header1Bean(
    override var route: String,
    var title: String
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is Header1Bean -> false
        route == o.route && title == o.title -> true
        else -> false
    }
}