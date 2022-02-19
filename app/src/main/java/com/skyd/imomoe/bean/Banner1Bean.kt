package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class Banner1Bean(
    override var actionUrl: String,
    var animeCoverList: List<AnimeCover6Bean>
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is Banner1Bean -> false
        actionUrl == o.actionUrl && animeCoverList == o.animeCoverList -> true
        else -> false
    }
}