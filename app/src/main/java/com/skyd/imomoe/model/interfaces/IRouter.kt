package com.skyd.imomoe.model.interfaces

import android.content.Context
import android.net.Uri

/**
 * 界面跳转处理接口
 */
interface IRouter {
    /**
     * 根据Uri跳转。强烈建议处理“根据网址跳转”功能输入的网址url，否则该功能将不可用！！！
     *
     * @param uri   Uri
     * @param context   Context
     * @return 成功处理了跳转，则返回true，没处理则返回false
     */
    fun route(uri: Uri, context: Context?): Boolean = false

    companion object {
        const val implName = "Router"
    }
}