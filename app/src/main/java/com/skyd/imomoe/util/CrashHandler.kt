package com.skyd.imomoe.util

import android.annotation.SuppressLint
import android.content.Context
import com.skyd.imomoe.view.activity.CrashActivity
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.system.exitProcess


class CrashHandler private constructor(val context: Context) : Thread.UncaughtExceptionHandler {
    private val mDefaultHandler: Thread.UncaughtExceptionHandler? =
        Thread.getDefaultUncaughtExceptionHandler()

    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    override fun uncaughtException(thread: Thread, ex: Throwable) {
        try {
            // flurry使用UncaughtExceptionHandler捕获异常，与原有的冲突，因此要再调用一次flurry的uncaughtException
            com.flurry.sdk.n.a().f.uncaughtException(thread, ex)
            val stringWriter = StringWriter()
            val printWriter = PrintWriter(stringWriter)
            ex.printStackTrace(printWriter)
            var cause = ex.cause
            while (cause != null) {
                cause.printStackTrace(printWriter)
                cause = cause.cause
            }
            printWriter.close()
            val unCaughtException = stringWriter.toString()  //详细错误日志
            logE("crash info", unCaughtException)
            CrashActivity.start(context, unCaughtException)
            exitProcess(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mDefaultHandler?.uncaughtException(thread, ex)
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var instance: CrashHandler? = null

        /**
         * 单例
         */
        fun getInstance(context: Context): CrashHandler? {
            var inst = instance
            if (inst == null) {
                synchronized(CrashHandler::class.java) {
                    inst = instance
                    if (inst == null) {
                        inst = CrashHandler(context)
                        instance = inst
                    }
                }
            }
            return inst
        }
    }

    /**
     * 保证只有一个CrashHandler实例
     */
    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }
}