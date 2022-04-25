package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.DataSourceFileBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.state.DataState
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File


class LocalDataSourceViewModel : ViewModel() {
    var dataSourceList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)

    init {
        getDataSourceList()
    }

    fun getDataSourceList(directoryPath: String = DataSourceManager.getJarDirectory()) {
        request(request = {
            val directory = File(directoryPath)
            dataSourceList.tryEmit(DataState.Refreshing)
            if (!directory.isDirectory) {
                dataSourceList.tryEmit(DataState.Success(emptyList()))
            } else {
                val jarList = directory.listFiles { _, name ->
                    name.endsWith(".ads", true) ||
                            name.endsWith(".jar", true)
                }
                dataSourceList.tryEmit(
                    DataState.Success(
                        jarList.orEmpty().map {
                            DataSourceFileBean(
                                "", it, it.name == DataSourceManager.dataSourceName
                            )
                        }.toList()
                    )
                )
            }
        }, error = { dataSourceList.tryEmit(DataState.Error(it.message.orEmpty())) })
    }
}