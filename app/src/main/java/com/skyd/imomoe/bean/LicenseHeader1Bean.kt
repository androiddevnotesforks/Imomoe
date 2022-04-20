package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class LicenseHeader1Bean(
    override var route: String = "",
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is LicenseHeader1Bean -> false
        route == o.route -> true
        else -> false
    }
}
