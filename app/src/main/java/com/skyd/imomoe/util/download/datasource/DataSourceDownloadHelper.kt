package com.skyd.imomoe.util.download.datasource

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.requestManageExternalStorage
import com.skyd.imomoe.util.download.DownloadStatus
import com.skyd.imomoe.util.showToast


class DataSourceDownloadHelper private constructor() {

    companion object {
        val downloadHashMap: HashMap<String, MutableLiveData<DownloadStatus>> = HashMap()
        val instance: DataSourceDownloadHelper by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            DataSourceDownloadHelper()
        }
    }

    fun getDownloadStatus(key: String): LiveData<DownloadStatus>? = downloadHashMap[key]

    fun downloadAnime(
        activity: AppCompatActivity,
        url: String,
        key: String,
        folderAndFileName: String
    ) {
        if (activity.isFinishing) {
            appContext.getString(R.string.do_not_finish_the_page_when_parse_download_data)
                .showToast()
            return
        }
        activity.requestManageExternalStorage {
            onGranted {
                if (downloadHashMap[key]?.value == DownloadStatus.DOWNLOADING) {
                    "已经在下载啦...".showToast()
                    return@onGranted
                }
                val status = MutableLiveData<DownloadStatus>()
                status.value = DownloadStatus.DOWNLOADING
                downloadHashMap[key] = status
                activity.startService(
                    Intent(activity, DataSourceDownloadService::class.java)
                        .putExtra("url", url)
                        .putExtra("key", key)
                        .putExtra("folderAndFileName", folderAndFileName)
                )
            }
            onDenied { "未获取存储权限，无法下载".showToast() }
        }
    }
}