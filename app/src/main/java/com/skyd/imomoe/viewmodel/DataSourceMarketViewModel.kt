package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DataSourceService
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.flow.MutableStateFlow


class DataSourceMarketViewModel : ViewModel() {
    var dataSourceMarketList: MutableStateFlow<DataState<List<Any>>> =
        MutableStateFlow(DataState.Empty)

    init {
        getDataSourceMarketList()
    }

    fun getDataSourceMarketList() {
        dataSourceMarketList.tryEmit(DataState.Refreshing)
        request(request = {
            RetrofitManager.get().create(DataSourceService::class.java).getDataSourceJson()
        }, success = {
            dataSourceMarketList.tryEmit(DataState.Success(it.dataSourceList))
        }, error = {
            dataSourceMarketList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}