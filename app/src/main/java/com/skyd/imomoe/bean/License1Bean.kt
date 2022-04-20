package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class License1Bean(
    override var route: String,
    var title: String,
    var license: String
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is License1Bean -> false
        route == o.route && title == o.title && license == o.license -> true
        else -> false
    }
}
