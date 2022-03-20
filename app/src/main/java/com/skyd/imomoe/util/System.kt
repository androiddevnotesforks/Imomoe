package com.skyd.imomoe.util

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import com.skyd.imomoe.ext.toTimeString
import java.util.*
import kotlin.system.exitProcess


fun currentTimeSecond() = System.currentTimeMillis() / 1000

fun currentDate(
    pattern: String = "yyyy-MM-dd HH:mm:ss",
    locale: Locale = Locale.getDefault()
) = System.currentTimeMillis().toTimeString(pattern, locale)

fun Context.killApplicationProcess() {
    // 注意：不能先杀掉主进程，否则逻辑代码无法继续执行，需先杀掉相关进程最后杀掉主进程
    val mActivityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val mList: List<ActivityManager.RunningAppProcessInfo> = mActivityManager.runningAppProcesses
    for (runningAppProcessInfo in mList) {
        if (runningAppProcessInfo.pid != Process.myPid()) {
            Process.killProcess(runningAppProcessInfo.pid)
        }
    }
    Process.killProcess(Process.myPid())
    exitProcess(0)
}