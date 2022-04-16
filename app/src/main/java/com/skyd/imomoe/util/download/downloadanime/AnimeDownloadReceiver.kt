package com.skyd.imomoe.util.download.downloadanime

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AnimeDownloadReceiver : BroadcastReceiver() {
    companion object {
        const val NOTIFY_ID = "notifyID"
        const val CANCEL_ACTION = "cancelAction"
        const val TASK_ID = "taskId"
        const val TASK_URL = "taskUrl"
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val action = intent?.action.orEmpty()

        val notificationId = intent?.getIntExtra(NOTIFY_ID, -1) ?: return
        if (notificationId == -1) return

        when (action) {
            CANCEL_ACTION -> {
                val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(notificationId)
                val taskId = intent.getLongExtra(TASK_ID, -1)
                val taskUrl = intent.getStringExtra(TASK_URL).orEmpty()
                if (taskId != -1L) {
                    AnimeDownloadService.mldCancelTask.postValue(taskId to taskUrl)
                }
            }
        }
    }
}