package com.skyd.imomoe.view.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Process
import androidx.appcompat.app.AppCompatActivity
import com.skyd.imomoe.config.Const.Common.GITHUB_NEW_ISSUE_URL
import com.skyd.imomoe.ext.showMessageDialog
import com.skyd.imomoe.util.Util.openBrowser
import kotlin.system.exitProcess


/**
 * 调试用的异常activity，不要继承BaseActivity
 */
class CrashActivity : AppCompatActivity() {
    companion object {
        const val CRASH_INFO = "crashInfo"

        fun start(context: Context, crashInfo: String) {
            val intent = Intent(context, CrashActivity::class.java)
            intent.putExtra(CRASH_INFO, crashInfo)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val crashInfo = intent.getStringExtra(CRASH_INFO)

        val message = "CrashInfo:\n$crashInfo"
        showMessageDialog(
            title = "哦呼，樱花动漫崩溃了！快去GitHub提Issue吧",
            message = message,
            cancelable = false,
            positiveText = "复制信息打开GitHub",
            onPositive = { _, _ ->
                val cm = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                cm.setPrimaryClip(ClipData.newPlainText("exception trace stack", message))
                openBrowser(GITHUB_NEW_ISSUE_URL)
                finish()
                Process.killProcess(Process.myPid())
                exitProcess(1)
            },
            negativeText = "退出",
            onNegative = { _, _ ->
                finish()
                Process.killProcess(Process.myPid())
                exitProcess(1)
            }
        )
        setFinishOnTouchOutside(false)
    }
}