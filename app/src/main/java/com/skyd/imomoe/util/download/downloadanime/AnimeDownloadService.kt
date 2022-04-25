package com.skyd.imomoe.util.download.downloadanime

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.common.HttpOption
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.download.m3u8.M3U8VodOption
import com.arialyy.aria.core.task.DownloadTask
import com.skyd.imomoe.R
import com.skyd.imomoe.database.entity.AnimeDownloadEntity
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.ext.toMD5
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.HtmlService
import com.skyd.imomoe.util.download.downloadanime.AnimeDownloadHelper.save2Xml
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class AnimeDownloadService : LifecycleService() {
    companion object {
        val stopTaskEvent: MutableSharedFlow<Long> =
            MutableSharedFlow(extraBufferCapacity = 1)
        val cancelTaskEvent: MutableSharedFlow<Pair<Long, String>> =
            MutableSharedFlow(extraBufferCapacity = 1)
        val resumeTaskEvent: MutableSharedFlow<Long> =
            MutableSharedFlow(extraBufferCapacity = 1)

        const val DOWNLOAD_URL_KEY = "downloadUrl"
        const val STORE_DIRECTORY_PATH_KEY = "storeFilePath"
        const val ANIME_TITLE = "animeTitle"
        const val ANIME_EPISODE = "animeEpisode"

        const val M3U8_CONTENT_TYPE = "application/vnd.apple.mpegurl"
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

    private val notifyMap = hashMapOf<String, AnimeDownloadNotification>()
    private val animeTitleEpisodeMap = hashMapOf<String, Pair<String, String>>()

    inner class AnimeDownloadBinder : Binder() {
        val service: AnimeDownloadService
            get() = this@AnimeDownloadService
        val animeTitleEpisodeMap: HashMap<String, Pair<String, String>>
            get() = this@AnimeDownloadService.animeTitleEpisodeMap
        val notCompleteList: List<DownloadEntity>
            get() = Aria.download(this).allNotCompleteTask.orEmpty()

        val onTaskPreEvent: MutableSharedFlow<DownloadTask>
            get() = this@AnimeDownloadService.onTaskPreEvent
        val onTaskStartEvent: MutableSharedFlow<DownloadTask>
            get() = this@AnimeDownloadService.onTaskStartEvent
        val onTaskCompleteEvent: MutableSharedFlow<DownloadTask>
            get() = this@AnimeDownloadService.onTaskCompleteEvent
        val onTaskRunningEvent: MutableSharedFlow<DownloadTask>
            get() = this@AnimeDownloadService.onTaskRunningEvent
        val onTaskStopEvent: MutableSharedFlow<DownloadTask>
            get() = this@AnimeDownloadService.onTaskStopEvent
        val onTaskCancelEvent: MutableSharedFlow<DownloadTask>
            get() = this@AnimeDownloadService.onTaskCancelEvent
        val onTaskFailEvent: MutableSharedFlow<DownloadTask>
            get() = this@AnimeDownloadService.onTaskFailEvent
    }

    private val animeDownloadBinder: Binder = AnimeDownloadBinder()

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
        return animeDownloadBinder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        intent ?: return START_NOT_STICKY

        val downloadUrl = intent.getStringExtra(DOWNLOAD_URL_KEY).orEmpty()
        val storeDirectoryPath = intent.getStringExtra(STORE_DIRECTORY_PATH_KEY).orEmpty()
        val animeTitle = intent.getStringExtra(ANIME_TITLE).orEmpty()
        val animeEpisode = intent.getStringExtra(ANIME_EPISODE).orEmpty()
        val fileName = downloadUrl.substringAfterLast("/", animeEpisode)

        coroutineScope.launch {
            val contentType = RetrofitManager
                .get()
                .create(HtmlService::class.java)
                .getResponseHeader(downloadUrl)
                .headers()["Content-Type"]
            withContext(Dispatchers.Main) {
                addTask(
                    downloadUrl = downloadUrl,
                    filePath = "$storeDirectoryPath/$fileName",
                    animeTitle = animeTitle,
                    animeEpisode = animeEpisode,
                    isM3u8 = contentType.equals(M3U8_CONTENT_TYPE, ignoreCase = true)
                )
            }
        }

        return START_NOT_STICKY
    }

    private fun addTask(
        downloadUrl: String,
        filePath: String,
        animeTitle: String,
        animeEpisode: String,
        isM3u8: Boolean = false
    ) {
        val id = Aria.download(this)
            .load(downloadUrl)
            .option(HttpOption().apply {
                useServerFileName(true)
            })
            .setFilePath(
                if (isM3u8 && filePath.endsWith(".m3u8", ignoreCase = true)) {
                    filePath.substringBeforeLast(".m3u8")
                } else {
                    filePath
                }
            )
            .apply {
                if (isM3u8) {
                    val option = M3U8VodOption()
                    option.setVodTsUrlConvert(MyVodTsUrlConverter())
                    option.setBandWidthUrlConverter(MyBandWidthUrlConverter())
                    option.setUseDefConvert(false)
                    m3u8VodOption(option)
                }
            }
            .create()
        animeTitleEpisodeMap[downloadUrl] = animeTitle to animeEpisode

        notifyMap[downloadUrl]?.cancel()
        notifyMap[downloadUrl] = AnimeDownloadNotification(
            applicationContext,
            taskId = id,
            url = downloadUrl,
            title = "$animeTitle - $animeEpisode"
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
        animeTitleEpisodeMap[task.downloadEntity.url]?.run {
            getString(
                R.string.anime_download_service_start_download,
                "$first - $second"
            ).showToast()
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
        animeTitleEpisodeMap[task.downloadEntity?.url]?.run {
            getString(
                R.string.anime_download_service_download_failed,
                "$first - $second"
            ).showToast()
        }
        onTaskFailEvent.tryEmit(task)
    }

    @Download.onTaskComplete
    fun onTaskComplete(task: DownloadTask?) {
        task ?: return
        notifyMap[task.downloadEntity?.url]?.cancel()
        val p = animeTitleEpisodeMap[task.downloadEntity?.url]
        if (p != null) {
            runCatching {
                coroutineScope.launch {
                    val file = File(task.downloadEntity.m3U8Entity.filePath)
                    file.toMD5()?.let {
                        val entity = AnimeDownloadEntity(it, p.second, file.name)
                        getAppDataBase().animeDownloadDao().insertAnimeDownload(entity)
                        save2Xml((file.parent ?: p.first).substringAfterLast("/"), entity)
                    }
                }
            }.onFailure {
                it.printStackTrace()
                it.message?.showToast()
            }
        } else {
            getString(R.string.anime_download_service_get_title_failed).showToast()
        }
        onTaskCompleteEvent.tryEmit(task)
    }

    @Download.onTaskRunning
    fun onTaskRunning(task: DownloadTask?) {
        task ?: return
        val m3U8Entity = task.downloadEntity?.m3U8Entity
        if (m3U8Entity == null) {
            val len: Long = task.fileSize
            val p = (task.currentProgress * 100.0 / len).toInt()
            notifyMap[task.downloadEntity.url]?.upload(p)
        } else {
            val p = ((m3U8Entity.peerIndex + 1) * 100.0 / m3U8Entity.peerNum).toInt()
            notifyMap[task.downloadEntity.url]?.upload(p)
        }
        onTaskRunningEvent.tryEmit(task)
    }
}
