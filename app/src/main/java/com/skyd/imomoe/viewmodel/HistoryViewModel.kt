package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.HistoryBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.flow.MutableStateFlow


class HistoryViewModel : ViewModel() {
    val uiState: MutableStateFlow<HistoryUiState> = MutableStateFlow(HistoryUiState.Refreshing())

    init {
        getHistoryList()
    }

    fun getHistoryList() {
        val currentState = uiState.value
        val oldList = if (currentState is HistoryUiState.Success) currentState.dataList else null
        uiState.tryEmit(HistoryUiState.Refreshing(oldList))
        request(request = { getAppDataBase().historyDao().getHistoryList() }, success = {
            it.sortWith { o1, o2 ->
                // 负数表示按时间戳从大到小排列
                -o1.time.compareTo(o2.time)
            }
            uiState.tryEmit(HistoryUiState.Success(it))
        }, error = {
            uiState.tryEmit(HistoryUiState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun deleteHistory(historyBean: HistoryBean) {
        request(request = {
            getAppDataBase().historyDao().deleteHistory(historyBean.animeUrl)
            getHistoryList()
        }, error = {
            "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }

    fun deleteAllHistory() {
        val currentState = uiState.value
        if (currentState is HistoryUiState.Success && currentState.dataList.isEmpty()) return
        request(request = {
            getAppDataBase().historyDao().deleteAllHistory()
            getHistoryList()
        }, error = {
            "${appContext.getString(R.string.delete_failed)}\n${it.message}".showToast()
        })
    }
}

sealed interface HistoryUiState {
    data class Success(val dataList: List<Any>) : HistoryUiState
    data class Error(val message: String = "") : HistoryUiState
    data class Refreshing(val oldList: List<Any>? = null) : HistoryUiState

    fun readOrNull(): List<Any>? {
        return when (this) {
            is Success -> dataList
            is Refreshing -> oldList
            else -> null
        }
    }
}