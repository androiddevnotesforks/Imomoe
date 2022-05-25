package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class TabBean(
    override var route: String = "",
    var partUrl: String = "",
    var title: String = ""
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is TabBean -> false
        route == o.route && partUrl == o.partUrl && title == o.title -> true
        else -> false
    }
}
