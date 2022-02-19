package com.skyd.imomoe.bean

import com.skyd.imomoe.view.adapter.variety.Diff
import java.io.File

typealias DataSource1Bean = DataSourceFileBean

class DataSourceFileBean(
    override var actionUrl: String,
    var file: File,
    var selected: Boolean = false
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is DataSourceFileBean -> false
        actionUrl == o.actionUrl && file == o.file && selected == o.selected -> true
        else -> false
    }
}