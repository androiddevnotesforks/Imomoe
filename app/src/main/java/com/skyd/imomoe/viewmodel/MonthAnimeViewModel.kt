package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.MonthAnimeModel
import com.skyd.imomoe.model.interfaces.IMonthAnimeModel
import com.skyd.imomoe.util.showToast


class MonthAnimeViewModel : ViewModel() {
    private val monthAnimeModel: IMonthAnimeModel by lazy {
        DataSourceManager.create(IMonthAnimeModel::class.java) ?: MonthAnimeModel()
    }
    var monthAnimeList: MutableList<AnimeCoverBean> = ArrayList()
    var mldMonthAnimeList: MutableLiveData<Boolean> = MutableLiveData()
    var pageNumberBean: PageNumberBean? = null
    var newPageIndex: Pair<Int, Int>? = null

    fun getMonthAnimeData(partUrl: String, isRefresh: Boolean = true) {
        request(request = { monthAnimeModel.getMonthAnimeData(partUrl) }, success = {
            if (isRefresh) monthAnimeList.clear()
            val positionStart = monthAnimeList.size
            monthAnimeList.addAll(it.first)
            pageNumberBean = it.second
            newPageIndex = Pair(positionStart, monthAnimeList.size - positionStart)
            mldMonthAnimeList.postValue(true)
        }, error = {
            monthAnimeList.clear()
            mldMonthAnimeList.postValue(false)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}