package com.skyd.imomoe.util.update

import android.app.Activity
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.formatSize
import com.skyd.imomoe.ext.showMessageDialog
import com.skyd.imomoe.ext.toHtml
import com.skyd.imomoe.model.AppUpdateModel
import com.skyd.imomoe.util.Util.openBrowser
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*

class AppUpdateHelper private constructor() {
    companion object {
        const val UPDATE_SERVER_SP_KEY = "updateServer"
        const val GITHUB = 0
        val serverName = arrayOf("Github")

        val instance: AppUpdateHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            AppUpdateHelper()
        }
    }

    fun getUpdateStatus(): StateFlow<AppUpdateStatus> = AppUpdateModel.status

    fun checkUpdate() {
        AppUpdateModel.checkUpdate()
    }

    fun noticeUpdate(activity: Activity) {
        listOf<Function<Unit>> { checkUpdate() }
        val updateBean = AppUpdateModel.updateBean ?: return
        activity.showMessageDialog(
            title = "发现新版本\n版本名：${updateBean.name}\n版本代号：${updateBean.tagName}",
            message = StringBuffer().run {
                val size = updateBean.assets[0].size
                if (size > 0) {
                    append("<p>大小：${size.toDouble().formatSize()}<br/>")
                }
                val updatedAt = updateBean.assets[0].updatedAt
                if (!updatedAt.isNullOrBlank()) {
                    try {
                        val format =
                            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
                        format.timeZone = TimeZone.getTimeZone("UTC")
                        val date = format.parse(updatedAt)
                        val s: String = if (date != null) {
                            SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(date)
                        } else {
                            updatedAt
                        }
                        append("发布于：${s}<br/>")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                val downloadCount = updateBean.assets[0].downloadCount
                if (!downloadCount.isNullOrBlank()) {
                    append("下载次数：${downloadCount}次<p/>")
                }
                append(updateBean.body)
                this.toString().toHtml()
            },
            onNegative = { dialog, _ ->
                dialog.dismiss()
                AppUpdateModel.status.value = AppUpdateStatus.LATER
            },
            positiveText = activity.getString(R.string.download_update)
        ) { _, _ ->
            openBrowser(
                AppUpdateModel.updateBean?.assets?.get(0)?.browserDownloadUrl
                    ?: return@showMessageDialog
            )
        }
    }
}