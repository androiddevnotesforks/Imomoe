package com.skyd.imomoe.config

import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.interfaces.interfaceVersion

interface Api {
    companion object {
        val MAIN_URL
            get() = (DataSourceManager.getConst() ?: com.skyd.imomoe.model.impls.Const()).MAIN_URL

        // github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/SkyD666/Imomoe/releases/latest"

        // github获取目录API
        const val REPO_CONTENT_URL =
            "https://api.github.com/repos/SkyD666/DataSourceRepository/contents/"

        // github获取data_source_list.json
        fun dataSourceListJsonUrl(interfaceVersion: String) =
            "${DATA_SOURCE_PREFIX}/datasource/${interfaceVersion}/data_source_list.json"

        // 数据源仓库icon前缀地址
        val DATA_SOURCE_PREFIX =
            "https://raw.githubusercontent.com/SkyD666/DataSourceRepository/master"

        // 弹幕url
        const val DANMAKU_URL = "https://api.danmu.oyyds.top/api"
    }
}