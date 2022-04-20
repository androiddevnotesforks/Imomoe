package com.skyd.imomoe.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.ClassifyModel
import com.skyd.imomoe.model.interfaces.IClassifyModel
import com.skyd.imomoe.util.showToast


class ClassifyViewModel : ViewModel() {
    private val classifyModel: IClassifyModel by lazy {
        DataSourceManager.create(IClassifyModel::class.java) ?: ClassifyModel()
    }
    var classifyTabTitle: String = ""       //如 地区
    var classifyTitle: String = ""          //如 大陆
    var currentPartUrl: String = ""
    private var isRequesting = false
    var mldClassifyTabList: MutableLiveData<List<ClassifyBean>?> = MutableLiveData()
    var mldClassifyList: MutableLiveData<List<Any>?> = MutableLiveData()
    var mldLoadMoreClassifyList: MutableLiveData<List<Any>?> = MutableLiveData()
    private var pageNumberBean: PageNumberBean? = null

    fun setActivity(activity: Activity) {
        classifyModel.setActivity(activity)
    }

    fun clearActivity() {
        classifyModel.clearActivity()
    }

    fun getClassifyTabData() {
        request(request = { classifyModel.getClassifyTabData() }, success = {
            mldClassifyTabList.postValue(it)
        }, error = {
            mldClassifyTabList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun getClassifyData(partUrl: String) {
        if (isRequesting) return
        isRequesting = true
        request(request = { classifyModel.getClassifyData(partUrl) }, success = {
            pageNumberBean = it.second
            mldClassifyList.postValue(it.first)
        }, error = {
            pageNumberBean = null
            mldClassifyList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        }, finish = { isRequesting = false })
    }

    fun loadMoreClassifyData() {
        if (isRequesting) return
        isRequesting = true
        val partUrl = pageNumberBean?.route
        if (partUrl == null) {
            mldLoadMoreClassifyList.postValue(ArrayList())
            appContext.getString(R.string.no_more_info).showToast()
            isRequesting = false
            return
        }
        request(request = { classifyModel.getClassifyData(partUrl) }, success = {
            pageNumberBean = it.second
            mldLoadMoreClassifyList.postValue(it.first)
        }, error = {
            pageNumberBean = null
            mldLoadMoreClassifyList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        }, finish = { isRequesting = false })
    }
}