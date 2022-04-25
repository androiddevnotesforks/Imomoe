package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.flow.MutableStateFlow


class FavoriteViewModel : ViewModel() {
    val favoriteList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)

    init {
        getFavoriteData()
    }

    fun getFavoriteData() {
        favoriteList.tryEmit(DataState.Refreshing)
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnimeList()
        }, success = {
            it.sortWith { o1, o2 ->
                // 负数表示按时间戳从大到小排列
                -o1.time.compareTo(o2.time)
            }
            favoriteList.tryEmit(DataState.Success(it))
        }, error = {
            favoriteList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}