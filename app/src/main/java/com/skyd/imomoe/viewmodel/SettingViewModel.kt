package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import coil.util.CoilUtils
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.database.getOfflineDatabase
import com.skyd.imomoe.ext.formatSize
import com.skyd.imomoe.ext.request
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
    fun getCacheSize() {
        request(request = {
            CoilUtils.createDefaultCache(appContext).directory.formatSize()
        }, success = {
            cacheSize.tryEmit(it)
        }, error = {
            cacheSize.tryEmit(appContext.getString(R.string.get_cache_size_failed))
        })
    }


    fun clearAllCache() {
        request(request = { CoilUtil.clearMemoryDiskCache() }, success = {
            clearAllCache.tryEmit(true to appContext.getString(R.string.clear_cache_succeed))
        }, error = {
            clearAllCache.tryEmit(false to appContext.getString(R.string.clear_cache_failed))
            "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
        }, finish = {
            request(request = {
                delay(1000)
                getCacheSize()
            })
        })
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