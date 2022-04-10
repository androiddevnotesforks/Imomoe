package com.skyd.imomoe.bean

import androidx.annotation.StyleRes
import com.skyd.imomoe.view.adapter.variety.Diff

class SkinCover1Bean(
    override var actionUrl: String,
    var cover: Any,         // Int颜色，或String图片链接
    var title: String,
    var using: Boolean,      // 正在使用
    @StyleRes
    var themeRes: Int
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is SkinCover1Bean -> false
        actionUrl == o.actionUrl && cover == o.cover && title == o.title && using == o.using &&
                themeRes == o.themeRes -> true
        else -> false
    }
}
