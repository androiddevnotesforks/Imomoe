package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.DataSourceFileBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.util.Util


class ConfigDataSourceViewModel : ViewModel() {
    var mldDeleteSource: MutableLiveData<Boolean> = MutableLiveData()

    fun resetDataSource() = setDataSource(DataSourceManager.DEFAULT_DATA_SOURCE)

    fun clearDataSourceCache() {
        DataSourceManager.clearCache()
        RetrofitManager.setInstanceNull()
    }

    fun setDataSource(name: String) {
        DataSourceManager.dataSourceName = name
        DataSourceManager.clearCache()
        RetrofitManager.setInstanceNull()
        Util.restartApp()
    }

    fun deleteDataSource(bean: DataSourceFileBean) {
        mldDeleteSource.postValue(bean.file.delete())
    }
}