package com.skyd.imomoe.util.download.downloadanime

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.skyd.imomoe.appContext
import com.skyd.imomoe.util.download.DownloadStatus
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.download.downloadanime.AnimeDownloadHelper.Companion.downloadHashMap


class AnimeDownloadNotificationReceiver : BroadcastReceiver() {
    companion object {
        const val DOWNLOAD_ANIME_NOTIFICATION_ID = "DownloadAnimeNotificationID"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action.orEmpty()

        val notificationId = intent?.getIntExtra(DOWNLOAD_ANIME_NOTIFICATION_ID, -1) ?: -1
        val key = intent?.getStringExtra("key").orEmpty()

        when (action) {
            "notification_canceled" -> {
                if (notificationId != -1) {
                    val notificationManager =
                        appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.cancel(notificationId)
                    downloadHashMap[key]?.postValue(DownloadStatus.CANCEL)
                    "取消下载".showToast()
                }
            }
        }
    }
}