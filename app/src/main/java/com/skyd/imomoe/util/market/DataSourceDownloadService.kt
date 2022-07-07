package com.skyd.imomoe.util.market

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.common.HttpOption
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.task.DownloadTask
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow


class DataSourceDownloadService : LifecycleService() {
    companion object {
        val stopTaskEvent: MutableSharedFlow<Long> =
            MutableSharedFlow(extraBufferCapacity = 1)
        val cancelTaskEvent: MutableSharedFlow<Pair<Long, String>> =
            MutableSharedFlow(extraBufferCapacity = 1)
        val resumeTaskEvent: MutableSharedFlow<Long> =
            MutableSharedFlow(extraBufferCapacity = 1)

        const val DOWNLOAD_URL_KEY = "downloadUrl"
        const val DATA_SOURCE_TITLE = "dataSourceTitle"
    }

    private val coroutineScope by lazy(LazyThreadSafetyMode.NONE) {
        CoroutineScope(Dispatchers.IO)
    }

    private val onTaskPreEvent: MutableSharedFlow<DownloadTask> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private val onTaskStartEvent: MutableSharedFlow<DownloadTask> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private val onTaskCompleteEvent: MutableSharedFlow<DownloadTask> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private val onTaskRunningEvent: MutableSharedFlow<DownloadTask> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private val onTaskStopEvent: MutableSharedFlow<DownloadTask> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private val onTaskCancelEvent: MutableSharedFlow<DownloadTask> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private val onTaskFailEvent: MutableSharedFlow<DownloadTask> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private val onTaskResumeEvent: MutableSharedFlow<DownloadTask> =
        MutableSharedFlow(extraBufferCapacity = 1)

    private val notifyMap = hashMapOf<String, DataSourceDownloadNotification>()
    private val dataSourceTitleMap = hashMapOf<String, String>()

    inner class DataSourceDownloadBinder : Binder() {
        val service: DataSourceDownloadService
            get() = this@DataSourceDownloadService
        val dataSourceTitleMap: HashMap<String, String>
            get() = this@DataSourceDownloadService.dataSourceTitleMap
        val notCompleteList: List<DownloadEntity>
            get() = Aria.download(this).allNotCompleteTask.orEmpty()

        val onTaskPreEvent: MutableSharedFlow<DownloadTask>
            get() = this@DataSourceDownloadService.onTaskPreEvent
        val onTaskStartEvent: MutableSharedFlow<DownloadTask>
            get() = this@DataSourceDownloadService.onTaskStartEvent
        val onTaskCompleteEvent: MutableSharedFlow<DownloadTask>
            get() = this@DataSourceDownloadService.onTaskCompleteEvent
        val onTaskRunningEvent: MutableSharedFlow<DownloadTask>
            get() = this@DataSourceDownloadService.onTaskRunningEvent
        val onTaskStopEvent: MutableSharedFlow<DownloadTask>
            get() = this@DataSourceDownloadService.onTaskStopEvent
        val onTaskResumeEvent: MutableSharedFlow<DownloadTask>
            get() = this@DataSourceDownloadService.onTaskResumeEvent
        val onTaskCancelEvent: MutableSharedFlow<DownloadTask>
            get() = this@DataSourceDownloadService.onTaskCancelEvent
        val onTaskFailEvent: MutableSharedFlow<DownloadTask>
            get() = this@DataSourceDownloadService.onTaskFailEvent
    }

    private val dataSourceDownloadBinder: Binder = DataSourceDownloadBinder()

    fun stopTask(id: Long) {
        if (id == -1L) return
        Aria.download(this).load(id).stop()
    }

    fun resumeTask(id: Long) {
        if (id == -1L) return
        Aria.download(this).load(id).resume()
    }

    fun cancelTask(id: Long, url: String?) {
        if (id == -1L) return
        Aria.download(this).load(id).cancel()
        if (url.isNullOrEmpty()) notifyMap.remove(url)
    }

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return dataSourceDownloadBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent ?: return START_NOT_STICKY

        val downloadUrl = intent.getStringExtra(DOWNLOAD_URL_KEY).orEmpty().let {
            if (it.startsWith("/")) Api.DATA_SOURCE_PREFIX + it
            else it
        }
        val dataSourceTitle = intent.getStringExtra(DATA_SOURCE_TITLE).orEmpty()

        addTask(
            downloadUrl = downloadUrl,
            filePath = "${DataSourceManager.getJarDirectory()}/$dataSourceTitle.ads",
            dataSourceTitle = dataSourceTitle,
        )

        return START_NOT_STICKY
    }

    private fun addTask(
        downloadUrl: String,
        filePath: String,
        dataSourceTitle: String
    ) {
        val id = Aria.download(this)
            .load(downloadUrl)
            .ignoreCheckPermissions()
            .option(HttpOption().apply {
                useServerFileName(true)
            })
            .setFilePath(filePath)
            .ignoreFilePathOccupy()     // 强制下载
            .create()
        dataSourceTitleMap[downloadUrl] = dataSourceTitle

        notifyMap[downloadUrl]?.cancel()
        notifyMap[downloadUrl] = DataSourceDownloadNotification(
            applicationContext,
            taskId = id,
            url = downloadUrl,
            title = dataSourceTitle
        )
    }

    override fun onCreate() {
        super.onCreate()
        Aria.download(this).register()

        stopTaskEvent.collectWithLifecycle(this) { stopTask(it) }
        cancelTaskEvent.collectWithLifecycle(this) { cancelTask(it.first, it.second) }
        resumeTaskEvent.collectWithLifecycle(this) { resumeTask(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        Aria.download(this).unRegister()
    }

    @Download.onPre
    fun onPre(task: DownloadTask?) {
        task ?: return
        onTaskPreEvent.tryEmit(task)
    }

    @Download.onTaskPre
    fun onTaskPre(task: DownloadTask?) {
        task ?: return
        onTaskPreEvent.tryEmit(task)
    }

    @Download.onTaskStart
    fun onTaskStart(task: DownloadTask?) {
        task ?: return
        dataSourceTitleMap[task.downloadEntity.url]?.also {
            getString(R.string.start_download, it).showToast()
        }
        onTaskStartEvent.tryEmit(task)
    }

    @Download.onTaskStop
    fun onTaskStop(task: DownloadTask?) {
        task ?: return
        onTaskStopEvent.tryEmit(task)
    }

    @Download.onTaskCancel
    fun onTaskCancel(task: DownloadTask?) {
        task ?: return
        onTaskCancelEvent.tryEmit(task)
        notifyMap[task.downloadEntity?.url]?.cancel()
    }

    @Download.onTaskFail
    fun onTaskFail(task: DownloadTask?) {
        task ?: return
        notifyMap[task.downloadEntity?.url]?.cancel()
        dataSourceTitleMap[task.downloadEntity?.url]?.also {
            getString(R.string.download_failed, it).showToast()
        }
        onTaskFailEvent.tryEmit(task)
    }

    @Download.onTaskComplete
    fun onTaskComplete(task: DownloadTask?) {
        task ?: return
        notifyMap[task.downloadEntity?.url]?.cancel()
        onTaskCompleteEvent.tryEmit(task)
    }

    @Download.onTaskRunning
    fun onTaskRunning(task: DownloadTask?) {
        task ?: return
        val len: Long = task.fileSize
        val p = (task.currentProgress * 100.0 / len).toInt()
        notifyMap[task.downloadEntity.url]?.upload(p)

        onTaskRunningEvent.tryEmit(task)
    }

    @Download.onTaskResume
    fun onTaskResume(task: DownloadTask?) {
        task ?: return
        onTaskResumeEvent.tryEmit(task)
    }
}
