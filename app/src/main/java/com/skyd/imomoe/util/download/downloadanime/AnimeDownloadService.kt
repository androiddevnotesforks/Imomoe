package com.skyd.imomoe.util.download.downloadanime

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.MutableLiveData
import com.arialyy.annotations.Download
import com.arialyy.aria.core.Aria
import com.arialyy.aria.core.common.HttpOption
import com.arialyy.aria.core.download.DownloadEntity
import com.arialyy.aria.core.download.m3u8.M3U8VodOption
import com.arialyy.aria.core.task.DownloadTask
import com.skyd.imomoe.R
import com.skyd.imomoe.database.entity.AnimeDownloadEntity
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.toMD5
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.HtmlService
import com.skyd.imomoe.util.download.downloadanime.AnimeDownloadHelper.save2Xml
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File


class AnimeDownloadService : LifecycleService() {
    companion object {
        val mldStopTask: MutableLiveData<Long> = MutableLiveData(-1L)
        val mldCancelTask: MutableLiveData<Pair<Long, String>> = MutableLiveData(-1L to "")
        val mldResumeTask: MutableLiveData<Long> = MutableLiveData(-1L)

        const val DOWNLOAD_URL_KEY = "downloadUrl"
        const val STORE_DIRECTORY_PATH_KEY = "storeFilePath"
        const val ANIME_TITLE = "animeTitle"
        const val ANIME_EPISODE = "animeEpisode"

        const val M3U8_CONTENT_TYPE = "application/vnd.apple.mpegurl"
    }

    private val coroutineScope by lazy(LazyThreadSafetyMode.NONE) {
        CoroutineScope(Dispatchers.IO)
    }

    private val mldOnTaskStart: MutableLiveData<DownloadTask> = MutableLiveData()
    private val mldOnTaskComplete: MutableLiveData<DownloadTask> = MutableLiveData()
    private val mldOnTaskRunning: MutableLiveData<DownloadTask> = MutableLiveData()
    private val mldOnTaskStop: MutableLiveData<DownloadTask> = MutableLiveData()
    private val mldOnTaskCancel: MutableLiveData<DownloadTask> = MutableLiveData()
    private val mldOnTaskFail: MutableLiveData<DownloadTask> = MutableLiveData()

    private val notifyMap = hashMapOf<String, AnimeDownloadNotification>()
    private val animeTitleEpisodeMap = hashMapOf<String, Pair<String, String>>()

    inner class AnimeDownloadBinder : Binder() {
        val service: AnimeDownloadService
            get() = this@AnimeDownloadService
        val animeTitleEpisodeMap: HashMap<String, Pair<String, String>>
            get() = this@AnimeDownloadService.animeTitleEpisodeMap
        val notCompleteList: List<DownloadEntity>
            get() = Aria.download(this).allNotCompleteTask

        val mldOnTaskStart: MutableLiveData<DownloadTask>
            get() = this@AnimeDownloadService.mldOnTaskStart
        val mldOnTaskComplete: MutableLiveData<DownloadTask>
            get() = this@AnimeDownloadService.mldOnTaskComplete
        val mldOnTaskRunning: MutableLiveData<DownloadTask>
            get() = this@AnimeDownloadService.mldOnTaskRunning
        val mldOnTaskStop: MutableLiveData<DownloadTask>
            get() = this@AnimeDownloadService.mldOnTaskStop
        val mldOnTaskCancel: MutableLiveData<DownloadTask>
            get() = this@AnimeDownloadService.mldOnTaskCancel
        val mldOnTaskFail: MutableLiveData<DownloadTask>
            get() = this@AnimeDownloadService.mldOnTaskFail
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

        mldStopTask.observe(this) { stopTask(it) }
        mldCancelTask.observe(this) { cancelTask(it.first, it.second) }
        mldResumeTask.observe(this) { resumeTask(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        Aria.download(this).unRegister()
    }

    @Download.onTaskStart
    fun onTaskStart(task: DownloadTask) {
        animeTitleEpisodeMap[task.downloadEntity.url]?.run {
            getString(
                R.string.anime_download_service_start_download,
                "$first - $second"
            ).showToast()
        }
        mldOnTaskStart.postValue(task)
    }

    @Download.onTaskStop
    fun onTaskStop(task: DownloadTask) {
        mldOnTaskStop.postValue(task)
    }

    @Download.onTaskCancel
    fun onTaskCancel(task: DownloadTask) {
        mldOnTaskCancel.postValue(task)
    }

    @Download.onTaskFail
    fun onTaskFail(task: DownloadTask) {
        animeTitleEpisodeMap[task.downloadEntity.url]?.run {
            getString(
                R.string.anime_download_service_download_failed,
                "$first - $second"
            ).showToast()
        }
        mldOnTaskFail.postValue(task)
    }

    @Download.onTaskComplete
    fun onTaskComplete(task: DownloadTask) {
        notifyMap[task.downloadEntity.url]?.cancel()
        val (title, episode) = animeTitleEpisodeMap[task.downloadEntity.url]!!
        coroutineScope.launch {
            val file = File(task.downloadEntity.m3U8Entity.filePath)
            file.toMD5()?.let {
                val entity = AnimeDownloadEntity(it, episode, file.name)
                getAppDataBase().animeDownloadDao().insertAnimeDownload(entity)
                save2Xml((file.parent ?: title).substringAfterLast("/"), entity)
            }
        }
        mldOnTaskComplete.postValue(task)
    }

    @Download.onTaskRunning
    fun onTaskRunning(task: DownloadTask) {
        val m3U8Entity = task.downloadEntity.m3U8Entity
        if (m3U8Entity == null) {
            val len: Long = task.fileSize
            val p = (task.currentProgress * 100.0 / len).toInt()
            notifyMap[task.downloadEntity.url]?.upload(p)
        } else {
            val p = ((m3U8Entity.peerIndex + 1) * 100.0 / m3U8Entity.peerNum).toInt()
            notifyMap[task.downloadEntity.url]?.upload(p)
        }
        mldOnTaskRunning.postValue(task)
    }
}
