package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.flow.MutableStateFlow


class FavoriteViewModel : ViewModel() {
    val uiState: MutableStateFlow<FavoriteUiState> =
        MutableStateFlow(FavoriteUiState.Refreshing())

    init {
        getFavoriteData()
    }

    fun getFavoriteData() {
        uiState.tryEmit(FavoriteUiState.Refreshing())
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnimeList()
        }, success = {
            it.sortWith { o1, o2 ->
                // 负数表示按时间戳从大到小排列
                -o1.time.compareTo(o2.time)
            }
            uiState.tryEmit(FavoriteUiState.Success(it))
        }, error = {
            uiState.tryEmit(FavoriteUiState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}

sealed interface FavoriteUiState {
    data class Success(override val dataList: List<Any>) : WithData(dataList)
    data class Error(val message: String = "") : FavoriteUiState
    data class Refreshing(override val dataList: List<Any>? = null) : WithData(dataList)

    abstract class WithData(open val dataList: List<Any>? = null) : FavoriteUiState
}