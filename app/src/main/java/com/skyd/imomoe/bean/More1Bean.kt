package com.skyd.imomoe.bean

import androidx.annotation.DrawableRes

class More1Bean(
    override var actionUrl: String,
    var title: String,
    @DrawableRes
    var image: Int
) : BaseBean
