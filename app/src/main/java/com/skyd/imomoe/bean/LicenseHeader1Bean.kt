package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class LicenseHeader1Bean(
    override var actionUrl: String = "",
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is LicenseHeader1Bean -> false
        actionUrl == o.actionUrl -> true
        else -> false
    }
}
