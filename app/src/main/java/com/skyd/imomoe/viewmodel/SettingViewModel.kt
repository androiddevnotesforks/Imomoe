package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import coil.Coil
import coil.annotation.ExperimentalCoilApi
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.database.getOfflineDatabase
import com.skyd.imomoe.ext.directorySize
import com.skyd.imomoe.ext.formatSize
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.net.okhttpClient
import com.skyd.imomoe.util.coil.CoilUtil
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow


class SettingViewModel : ViewModel() {
    val allHistoryCount: MutableStateFlow<Long> = MutableStateFlow(-1L)
    val cacheSize: MutableStateFlow<String> = MutableStateFlow("")

    val deleteAllHistory: MutableSharedFlow<Pair<Boolean, String>> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val clearAllCache: MutableSharedFlow<Pair<Boolean, String>> =
        MutableSharedFlow(extraBufferCapacity = 1)

    init {
        getAllHistoryCount()
        getCacheSize()
    }

    fun deleteAllHistory() {
        request(request = {
            getAppDataBase().historyDao().deleteAllHistory()
            getAppDataBase().searchHistoryDao().deleteAllSearchHistory()
            getOfflineDatabase().playRecordDao().deleteAll()
        }, success = {
            deleteAllHistory.tryEmit(true to appContext.getString(R.string.delete_all_history_succeed))
            getAllHistoryCount()
        }, error = {
            deleteAllHistory.tryEmit(false to appContext.getString(R.string.clear_cache_failed))
            "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }

    // 获取Coil磁盘缓存大小
    @OptIn(ExperimentalCoilApi::class)
    fun getCacheSize() {
        Thread {
            runCatching {
                ((Coil.imageLoader(appContext).diskCache?.size ?: 0)
                        + (okhttpClient.cache?.directory?.directorySize() ?: 0)).formatSize()
            }.onSuccess {
                cacheSize.tryEmit(it)
            }.onFailure {
                it.printStackTrace()
                cacheSize.tryEmit(appContext.getString(R.string.get_cache_size_failed))
            }
        }.start()
    }

    fun clearAllCache() {
        Thread {
            runCatching {
                CoilUtil.clearMemoryDiskCache()
                if (okhttpClient.cache?.directory?.exists() == true) {
                    okhttpClient.cache?.delete()
                }
            }.onSuccess {
                clearAllCache.tryEmit(true to appContext.getString(R.string.clear_cache_succeed))
            }.onFailure {
                it.printStackTrace()
                clearAllCache.tryEmit(false to appContext.getString(R.string.clear_cache_failed))
                "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
            }.also {
                request(request = {
                    delay(1000)
                    getCacheSize()
                })
            }
        }.start()
    }

    fun getAllHistoryCount() {
        request(request = {
            getAppDataBase().historyDao().getHistoryCount() +
                    getAppDataBase().searchHistoryDao().getSearchHistoryCount() +
                    getOfflineDatabase().playRecordDao().getPlayRecordCount()
        }, success = { allHistoryCount.tryEmit(it) }, error = {
            allHistoryCount.tryEmit(-1)
        })
    }
}