package com.skyd.imomoe.util

import android.annotation.SuppressLint
import android.os.Build
import java.lang.reflect.Method


fun getOsInfo(): String {
    return if (isHarmonyOs()) {
        "Harmony " + getHarmonyVersion()
    } else {
        "Android " + Build.VERSION.RELEASE
    }
}

/**
 * 是否为鸿蒙系统
 */
fun isHarmonyOs(): Boolean {
    return try {
        val buildExClass = Class.forName("com.huawei.system.BuildEx")
        val osBrand: Any = buildExClass.getMethod("getOsBrand").invoke(buildExClass)!!
        "Harmony".equals(osBrand.toString(), ignoreCase = true)
    } catch (t: Throwable) {
        false
    }
}

/**
 * 获取鸿蒙系统版本号
 *
 * @return 版本号
 */
fun getHarmonyVersion(): String {
    return getProp("hw_sc.build.platform.version", "")
}

@Suppress("SameParameterValue")
@SuppressLint("PrivateApi")
private fun getProp(property: String, defaultValue: String): String {
    try {
        val spClz: Class<*> = Class.forName("android.os.SystemProperties")
        val method: Method = spClz.getDeclaredMethod("get", String::class.java)
        val value = method.invoke(spClz, property) as? String
        return if (value.isNullOrEmpty()) defaultValue else value
    } catch (e: Throwable) {
        e.printStackTrace()
    }
    return defaultValue
}