package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.model.interfaces.IHomeModel

class HomeModel : IHomeModel {
    override suspend fun getAllTabData(): ArrayList<TabBean> {
        return arrayListOf(
            TabBean("/market", "", "使用方式一：在数据源商店下载数据源"),
            TabBean("/manual", "", "使用方式二：手动导入数据源")
        )
    }
}