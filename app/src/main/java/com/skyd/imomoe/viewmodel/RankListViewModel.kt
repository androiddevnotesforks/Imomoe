package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IRankListModel
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RankListViewModel @Inject constructor(
    private val rankModel: IRankListModel
) : ViewModel() {
    var mldRankData: MutableLiveData<List<Any>?> = MutableLiveData()
    var mldLoadMoreRankData: MutableLiveData<List<Any>?> = MutableLiveData()
    private var pageNumberBean: PageNumberBean? = null

    fun getRankListData(partUrl: String) {
        request(request = { rankModel.getRankListData(partUrl) }, success = {
            pageNumberBean = it.second
            mldRankData.postValue(it.first)
        }, error = {
            mldRankData.postValue(null)
            it.message?.showToast(Toast.LENGTH_LONG)
        })
    }

    fun loadMoreRankListData() {
        val partUrl = pageNumberBean?.route
        if (partUrl == null) {
            mldLoadMoreRankData.postValue(emptyList())
            appContext.getString(R.string.no_more_info).showToast()
            return
        }
        request(request = { rankModel.getRankListData(partUrl) }, success = {
            pageNumberBean = it.second
            mldLoadMoreRankData.postValue(it.first)
        }, error = {
            mldRankData.postValue(null)
            it.message?.showToast(Toast.LENGTH_LONG)
        })
    }
}