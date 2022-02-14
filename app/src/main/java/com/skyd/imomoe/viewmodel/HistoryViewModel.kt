package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.HistoryBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.showToast


class HistoryViewModel : ViewModel() {
    var historyList: MutableList<Any> = ArrayList()
    var mldHistoryList: MutableLiveData<Boolean> = MutableLiveData()
    var mldDeleteHistory: MutableLiveData<Int> = MutableLiveData()
    var mldDeleteAllHistory: MutableLiveData<Int> = MutableLiveData()

    fun getHistoryList() {
        request(request = { getAppDataBase().historyDao().getHistoryList() }, success = {
            it.sortWith { o1, o2 ->
                // 负数表示按时间戳从大到小排列
                -o1.time.compareTo(o2.time)
            }
            historyList.clear()
            historyList.addAll(it)
            mldHistoryList.postValue(true)
        }, error = {
            historyList.clear()
            mldHistoryList.postValue(false)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun deleteHistory(historyBean: HistoryBean) {
        request(request = {
            getAppDataBase().historyDao().deleteHistory(historyBean.animeUrl)
        }, success = {
            val index = historyList.indexOf(historyBean)
            historyList.removeAt(index)
            mldDeleteHistory.postValue(index)
        }, error = {
            mldDeleteHistory.postValue(-1)
            "${App.context.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }

    fun deleteAllHistory() {
        request(request = { getAppDataBase().historyDao().deleteAllHistory() }, success = {
            val itemCount: Int = historyList.size
            historyList.clear()
            mldDeleteAllHistory.postValue(itemCount)
        }, error = {
            mldDeleteAllHistory.postValue(0)
            "${App.context.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }
}