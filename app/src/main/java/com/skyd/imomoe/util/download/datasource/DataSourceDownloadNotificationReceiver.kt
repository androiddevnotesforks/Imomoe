package com.skyd.imomoe.util.download.datasource

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skyd.imomoe.App
import com.skyd.imomoe.util.download.DownloadStatus
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.download.downloadanime.AnimeDownloadHelper.Companion.downloadHashMap


class DataSourceDownloadNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val DOWNLOAD_DATA_SOURCE_NOTIFICATION_ID = "DownloadDataSourceNotificationID"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action.orEmpty()

        val notificationId = intent?.getIntExtra(DOWNLOAD_DATA_SOURCE_NOTIFICATION_ID, -1) ?: -1
        val key = intent?.getStringExtra("key").orEmpty()

        when (action) {
            "notification_canceled" -> {
                if (notificationId != -1) {
                    val notificationManager =
                        App.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)
                    downloadHashMap[key]?.postValue(DownloadStatus.CANCEL)
                    "取消下载".showToast()
                }
            }
        }
    }
}