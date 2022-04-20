package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff

class Banner1Bean(
    override var route: String,
    var animeCoverList: List<AnimeCover6Bean>
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is Banner1Bean -> false
        route == o.route && animeCoverList == o.animeCoverList -> true
        else -> false
    }
}