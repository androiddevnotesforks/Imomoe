package com.skyd.imomoe.model.impls

import com.skyd.imomoe.model.interfaces.IConst

class Const : IConst {
    override fun versionName(): String = "1.1.0"

    override fun versionCode(): Int = 5

    override val MAIN_URL: String
        get() {
            val url = com.skyd.imomoe.config.Const.Common.GITHUB_URL
            return if (url.endsWith("/")) url
            else "$url/"
        }

    override fun about(): String {
        return "默认数据源，不提供任何数据，请在设置界面手动选择自定义数据源！"
    }
}
