package com.skyd.imomoe.config

import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.interfaces.interfaceVersion

interface Api {
    companion object {
        val MAIN_URL
            get() = (DataSourceManager.getConst() ?: com.skyd.imomoe.model.impls.Const()).MAIN_URL

        // github
        const val CHECK_UPDATE_URL = "https://api.github.com/repos/SkyD666/Imomoe/releases/latest"

        // 数据源仓库raw地址
        val DATA_SOURCE_DOWNLOAD_URL_PREFIX =
            "https://raw.githubusercontent.com/SkyD666/DataSourceRepository/master/datasource/$interfaceVersion"

        // 当前数据源接口版本的json
        val DATA_SOURCE_JSON_URL = "$DATA_SOURCE_DOWNLOAD_URL_PREFIX/data_source_list.json"

        // 数据源仓库icon前缀地址
        val DATA_SOURCE_IMAGE_PREFIX = "https://github.com/SkyD666/DataSourceRepository/raw/master"

        // 弹幕url
        const val DANMAKU_URL = "https://api.danmu.oyyds.top/api"
    }
}