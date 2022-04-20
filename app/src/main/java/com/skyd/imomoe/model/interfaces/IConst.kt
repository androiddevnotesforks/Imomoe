package com.skyd.imomoe.model.interfaces

/**
 * 获取MAIN_URL、版本、自定义数据ads包、关于等信息
 */
interface IConst : IBase {
    companion object {
        const val implName = "Const"
    }

    val MAIN_URL: String

    /**
     * @return jar包的关于信息
     */
    fun about(): String {
        return MAIN_URL
    }

    /**
     * @return jar包的版本名信息
     */
    fun versionName(): String? {
        return null
    }

    /**
     * @return jar包的版本号信息
     */
    fun versionCode(): Int {
        return 0
    }
}