package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.DataSourceFileBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import java.io.File


class LocalDataSourceViewModel : ViewModel() {
    var mldDataSourceList: MutableLiveData<List<Any>?> = MutableLiveData()

    fun getDataSourceList(directoryPath: String = DataSourceManager.getJarDirectory()) {
        request(request = {
            val directory = File(directoryPath)
            if (!directory.isDirectory) {
                mldDataSourceList.postValue(ArrayList())
            } else {
                val jarList = directory.listFiles { _, name ->
                    name.endsWith(".ads", true) ||
                            name.endsWith(".jar", true)
                }
                mldDataSourceList.postValue((jarList ?: emptyArray())
                    .map {
                        DataSourceFileBean(
                            "", it, it.name == DataSourceManager.dataSourceName
                        )
                    }.toList()
                )
            }
        }, error = { mldDataSourceList.postValue(null) })
    }
}