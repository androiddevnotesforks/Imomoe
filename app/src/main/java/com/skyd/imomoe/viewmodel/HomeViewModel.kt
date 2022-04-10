package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.HomeModel
import com.skyd.imomoe.model.interfaces.IHomeModel
import com.skyd.imomoe.util.showToast


class HomeViewModel : ViewModel() {
    private val homeModel: IHomeModel by lazy {
        DataSourceManager.create(IHomeModel::class.java) ?: HomeModel()
    }
    var mldAllTabList: MutableLiveData<List<TabBean>?> = MutableLiveData()
    var currentTab = -1

    fun getAllTabData() {
        request(request = { homeModel.getAllTabData() }, success = {
            mldAllTabList.postValue(it)
        }, error = {
            mldAllTabList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast(Toast.LENGTH_LONG)
        })
    }
}