package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.HistoryBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.showToast


class HistoryViewModel : ViewModel() {
    var mldHistoryList: MutableLiveData<List<Any>?> = MutableLiveData()
    var mldDeleteHistory: MutableLiveData<HistoryBean?> = MutableLiveData()
    var mldDeleteAllHistory: MutableLiveData<Boolean> = MutableLiveData()

    fun getHistoryList() {
        request(request = { getAppDataBase().historyDao().getHistoryList() }, success = {
            it.sortWith { o1, o2 ->
                // 负数表示按时间戳从大到小排列
                -o1.time.compareTo(o2.time)
            }
            mldHistoryList.postValue(it)
        }, error = {
            mldHistoryList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun deleteHistory(historyBean: HistoryBean) {
        request(request = {
            getAppDataBase().historyDao().deleteHistory(historyBean.animeUrl)
        }, success = {
            mldDeleteHistory.postValue(historyBean)
        }, error = {
            mldDeleteHistory.postValue(null)
            "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }

    fun deleteAllHistory() {
        request(request = { getAppDataBase().historyDao().deleteAllHistory() }, success = {
            mldDeleteAllHistory.postValue(true)
        }, error = {
            mldDeleteAllHistory.postValue(false)
            "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }
}