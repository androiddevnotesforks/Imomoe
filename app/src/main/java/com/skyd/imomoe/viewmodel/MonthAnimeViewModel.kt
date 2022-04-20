package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IMonthAnimeModel
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class MonthAnimeViewModel @Inject constructor(
    private val monthAnimeModel: IMonthAnimeModel
) : ViewModel() {
    var partUrl: String = ""
    var mldMonthAnimeList: MutableLiveData<List<Any>?> = MutableLiveData()
    var mldLoadMoreMonthAnimeList: MutableLiveData<List<Any>?> = MutableLiveData()
    private var pageNumberBean: PageNumberBean? = null

    fun getMonthAnimeData(partUrl: String) {
        request(request = { monthAnimeModel.getMonthAnimeData(partUrl) }, success = {
            pageNumberBean = it.second
            mldMonthAnimeList.postValue(it.first)
        }, error = {
            mldMonthAnimeList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun loadMoreMonthAnimeData() {
        val partUrl = pageNumberBean?.route
        if (partUrl == null) {
            mldLoadMoreMonthAnimeList.postValue(ArrayList())
            appContext.getString(R.string.no_more_info).showToast()
            return
        }
        request(request = { monthAnimeModel.getMonthAnimeData(partUrl) }, success = {
            pageNumberBean = it.second
            mldLoadMoreMonthAnimeList.postValue(it.first)
        }, error = {
            mldLoadMoreMonthAnimeList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}