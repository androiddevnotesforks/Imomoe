package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.state.DataState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow


class LocalDataSourceViewModel : ViewModel() {
    var dataSourceList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)
    var customMainUrl: MutableSharedFlow<String?> = MutableSharedFlow(extraBufferCapacity = 1)

    init {
        getDataSourceList()
    }

    fun getDataSourceList(directoryPath: String = DataSourceManager.getJarDirectory()) {
        request(request = {
            dataSourceList.tryEmit(
                DataState.Success(DataSourceManager.getDataSourceList(directoryPath))
            )
        }, error = { dataSourceList.tryEmit(DataState.Error(it.message.orEmpty())) })
    }
}