package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.HistoryBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow


class HistoryViewModel : ViewModel() {
    val historyList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)
    val deleteHistory: MutableSharedFlow<HistoryBean?> = MutableSharedFlow(extraBufferCapacity = 1)
    val deleteAllHistory: MutableSharedFlow<Boolean> = MutableSharedFlow(extraBufferCapacity = 1)

    init {
        getHistoryList()
    }

    fun getHistoryList() {
        historyList.tryEmit(DataState.Refreshing)
        request(request = { getAppDataBase().historyDao().getHistoryList() }, success = {
            it.sortWith { o1, o2 ->
                // 负数表示按时间戳从大到小排列
                -o1.time.compareTo(o2.time)
            }
            historyList.tryEmit(DataState.Success(it))
        }, error = {
            historyList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun deleteHistory(historyBean: HistoryBean) {
        request(request = {
            getAppDataBase().historyDao().deleteHistory(historyBean.animeUrl)
            getHistoryList()
        }, success = {
            deleteHistory.tryEmit(historyBean)
        }, error = {
            deleteHistory.tryEmit(null)
            "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }

    fun deleteAllHistory() {
        request(request = {
            getAppDataBase().historyDao().deleteAllHistory()
            getHistoryList()
        }, success = {
            deleteAllHistory.tryEmit(true)
        }, error = {
            deleteAllHistory.tryEmit(false)
            "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }
}