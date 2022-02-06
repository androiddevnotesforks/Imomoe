package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import coil.util.CoilUtils
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.database.getOfflineDatabase
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.coil.CoilUtil
import com.skyd.imomoe.ext.formatSize
import com.skyd.imomoe.ext.request


class SettingViewModel : ViewModel() {
    var mldAllHistoryCount: MutableLiveData<Long> = MutableLiveData()
    var mldDeleteAllHistory: MutableLiveData<Boolean> = MutableLiveData()
    var mldClearAllCache: MutableLiveData<Boolean> = MutableLiveData()
    var mldCacheSize: MutableLiveData<String> = MutableLiveData()

    fun deleteAllHistory() {
        request(request = {
            getAppDataBase().historyDao().deleteAllHistory()
            getAppDataBase().searchHistoryDao().deleteAllSearchHistory()
            getOfflineDatabase().playRecordDao().deleteAll()
        }, success = {
            mldDeleteAllHistory.postValue(true)
            getAllHistoryCount()
        }, error = {
            mldDeleteAllHistory.postValue(false)
            "${App.context.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }

    // 获取Coil磁盘缓存大小
    fun getCacheSize() {
        request(request = {
            CoilUtils.createDefaultCache(App.context).directory.formatSize()
        }, success = {
            mldCacheSize.postValue(it)
        }, error = {
            mldCacheSize.postValue(App.context.getString(R.string.get_cache_size_failed))
        })
    }


    fun clearAllCache() {
        request(request = { CoilUtil.clearMemoryDiskCache() }, success = {
            mldClearAllCache.postValue(true)
        }, error = {
            mldClearAllCache.postValue(false)
            "${App.context.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }

    fun getAllHistoryCount() {
        request(request = {
            getAppDataBase().historyDao().getHistoryCount() +
                    getAppDataBase().searchHistoryDao().getSearchHistoryCount() +
                    getOfflineDatabase().playRecordDao().getPlayRecordCount()
        }, success = { mldAllHistoryCount.postValue(it) }, error = {
            mldAllHistoryCount.postValue(-1)
        })
    }
}