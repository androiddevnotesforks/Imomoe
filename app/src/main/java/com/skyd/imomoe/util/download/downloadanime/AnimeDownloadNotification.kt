package com.skyd.imomoe.util.download.downloadanime

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.skyd.imomoe.R


class AnimeDownloadNotification(
    val context: Context,
    val taskId: Long,
    val url: String,
    var title: String
) {
    companion object {
        var MAX_NOTIFY_ID = 1000

        const val CHANNEL_ID = "download_anime"
        const val CHANNEL_NAME = "download_message"
    }

    private val manager: NotificationManager by lazy {
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
    private val builder: NotificationCompat.Builder by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
            NotificationCompat.Builder(context, CHANNEL_ID)
        } else {
            NotificationCompat.Builder(context)
        }
    }
    private val notifyId = ++MAX_NOTIFY_ID

    init {
        val intent = Intent(context, AnimeDownloadReceiver::class.java).apply {
            action = AnimeDownloadReceiver.CANCEL_ACTION
            putExtra(AnimeDownloadReceiver.NOTIFY_ID, notifyId)
            putExtra(AnimeDownloadReceiver.TASK_ID, taskId)
            putExtra(AnimeDownloadReceiver.TASK_URL, url)
        }
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(
            context,
            notifyId,
            intent,
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) PendingIntent.FLAG_CANCEL_CURRENT
            else PendingIntent.FLAG_MUTABLE
        )
        builder
            .setContentTitle(context.getString(R.string.anime_download_notification_title, title))
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText("0%")
            .setProgress(100, 0, false)
            .setAutoCancel(false)
            .addAction(
                R.drawable.ic_close_24,
                context.getString(R.string.cancel),
                pendingIntent
            )
        manager.notify(notifyId, builder.build())
    }

    fun upload(progress: Int) {
        builder.setProgress(100, progress, false)
            .setContentText("$progress%")
        manager.notify(notifyId, builder.build())
    }

    fun cancel() {
        manager.cancel(notifyId)
    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val channel =
            NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_LOW)
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}