package com.skyd.imomoe.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.ext.tryEmitError
import com.skyd.imomoe.ext.tryEmitLoadMore
import com.skyd.imomoe.model.interfaces.IClassifyModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class ClassifyViewModel @Inject constructor(
    private val classifyModel: IClassifyModel
) : ViewModel() {
    var classifyTabTitle: String = ""       //如 地区
    var classifyTitle: String = ""          //如 大陆
    var currentPartUrl: String = ""
    private var isRequesting = false
    val classifyTabList: MutableStateFlow<DataState<List<ClassifyBean>>> =
        MutableStateFlow(DataState.Empty)
    val classifyList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)
    private var pageNumberBean: PageNumberBean? = null

    fun setActivity(activity: Activity) {
        classifyModel.setActivity(activity)
    }

    fun clearActivity() {
        classifyModel.clearActivity()
    }

    fun getClassifyTabData() {
        classifyTabList.tryEmit(DataState.Refreshing)
        request(request = { classifyModel.getClassifyTabData() }, success = {
            classifyTabList.tryEmit(DataState.Success(it))
        }, error = {
            classifyTabList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun getClassifyData(partUrl: String) {
        if (isRequesting) return
        isRequesting = true
        classifyList.tryEmit(DataState.Refreshing)
        request(request = { classifyModel.getClassifyData(partUrl) }, success = {
            pageNumberBean = it.second
            classifyList.tryEmit(DataState.Success(it.first))
        }, error = {
            pageNumberBean = null
            classifyList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        }, finish = { isRequesting = false })
    }

    fun loadMoreClassifyData() {
        if (isRequesting) return
        isRequesting = true
        val oldData = classifyList.value
        classifyList.tryEmit(DataState.Loading)
        val partUrl = pageNumberBean?.route
        if (partUrl == null) {
            classifyList.tryEmit(oldData)
            appContext.getString(R.string.no_more_info).showToast()
            isRequesting = false
            return
        }
        request(request = { classifyModel.getClassifyData(partUrl) }, success = {
            pageNumberBean = it.second
            classifyList.tryEmitLoadMore(oldData = oldData, newData = it.first)
        }, error = {
            pageNumberBean = null
            classifyList.tryEmitError(oldData, it.message)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        }, finish = { isRequesting = false })
    }
}