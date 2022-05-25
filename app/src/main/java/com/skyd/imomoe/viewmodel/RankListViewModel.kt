package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.ext.tryEmitError
import com.skyd.imomoe.ext.tryEmitLoadMore
import com.skyd.imomoe.model.interfaces.IRankListModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class RankListViewModel @Inject constructor(
    private val rankModel: IRankListModel
) : ViewModel() {
    var partUrl: String = ""
    val mldRankData: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)
    private var pageNumberBean: PageNumberBean? = null

    fun getRankListData() {
        request(request = { rankModel.getRankListData(partUrl) }, success = {
            pageNumberBean = it.second
            mldRankData.tryEmit(DataState.Success(it.first.toMutableList()))
        }, error = {
            mldRankData.tryEmit(DataState.Error(it.message.orEmpty()))
            it.message?.showToast(Toast.LENGTH_LONG)
        })
    }

    fun loadMoreRankListData() {
        val partUrl = pageNumberBean?.route
        val oldData = mldRankData.value
        mldRankData.tryEmit(DataState.Loading)
        if (partUrl == null) {
            mldRankData.tryEmit(oldData)
            appContext.getString(R.string.no_more_info).showToast()
            return
        }
        request(request = { rankModel.getRankListData(partUrl) }, success = {
            pageNumberBean = it.second
            mldRankData.tryEmitLoadMore(oldData, it.first.toMutableList())
        }, error = {
            mldRankData.tryEmitError(oldData, it.message)
            it.message?.showToast(Toast.LENGTH_LONG)
        })
    }
}