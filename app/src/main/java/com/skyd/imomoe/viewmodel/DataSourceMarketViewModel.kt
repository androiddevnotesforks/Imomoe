package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.net.RetrofitManager
import com.skyd.imomoe.net.service.DataSourceService
import com.skyd.imomoe.util.showToast


class DataSourceMarketViewModel : ViewModel() {
    var mldDataSourceMarketList: MutableLiveData<List<Any>?> = MutableLiveData()

    fun getDataSourceMarketList() {
        request(request = {
            RetrofitManager.get().create(DataSourceService::class.java).getDataSourceJson()
        }, success = {
            mldDataSourceMarketList.postValue(it.dataSourceList)
        }, error = {
            mldDataSourceMarketList.postValue(null)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}