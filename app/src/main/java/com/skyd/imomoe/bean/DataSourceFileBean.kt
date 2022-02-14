package com.skyd.imomoe.bean

import java.io.File

typealias DataSource1Bean = DataSourceFileBean

class DataSourceFileBean(
    override var actionUrl: String,
    var file: File,
    var selected: Boolean = false
) : BaseBean