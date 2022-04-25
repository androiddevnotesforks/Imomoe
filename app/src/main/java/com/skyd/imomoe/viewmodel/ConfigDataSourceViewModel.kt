package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.DataSourceFileBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.util.Util
import kotlinx.coroutines.flow.MutableSharedFlow


class ConfigDataSourceViewModel : ViewModel() {
    var deleteSource: MutableSharedFlow<Boolean> = MutableSharedFlow(extraBufferCapacity = 1)

    fun resetDataSource() = setDataSource(DataSourceManager.DEFAULT_DATA_SOURCE)

    fun clearDataSourceCache() {
        DataSourceManager.clearCache()
        RetrofitManager.setInstanceNull()
    }

    fun setDataSource(name: String) {
        DataSourceManager.setDataSourceNameSynchronously(name)
        DataSourceManager.clearCache()
        RetrofitManager.setInstanceNull()
        Util.restartApp()
    }

    fun deleteDataSource(bean: DataSourceFileBean) {
        deleteSource.tryEmit(bean.file.delete())
    }
}