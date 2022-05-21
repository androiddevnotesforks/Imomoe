package com.skyd.imomoe.bean

import java.io.Serializable

interface BaseBean : Serializable {
    var route: String
}

class BaseBeanImpl(override var route: String = "") : BaseBean