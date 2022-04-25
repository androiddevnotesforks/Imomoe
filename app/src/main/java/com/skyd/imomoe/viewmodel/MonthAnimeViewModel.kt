package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.ext.tryEmitError
import com.skyd.imomoe.ext.tryEmitLoadMore
import com.skyd.imomoe.model.interfaces.IMonthAnimeModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class MonthAnimeViewModel @Inject constructor(
    private val monthAnimeModel: IMonthAnimeModel
) : ViewModel() {
    var partUrl: String = ""
    val monthAnimeList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)
    private var pageNumberBean: PageNumberBean? = null

    fun getMonthAnimeData(partUrl: String) {
        monthAnimeList.tryEmit(DataState.Refreshing)
        request(request = { monthAnimeModel.getMonthAnimeData(partUrl) }, success = {
            pageNumberBean = it.second
            monthAnimeList.tryEmit(DataState.Success(it.first))
        }, error = {
            monthAnimeList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun loadMoreMonthAnimeData() {
        val partUrl = pageNumberBean?.route
        val oldData = monthAnimeList.value
        monthAnimeList.tryEmit(DataState.Loading)
        if (partUrl == null) {
            monthAnimeList.tryEmit(oldData)
            appContext.getString(R.string.no_more_info).showToast()
            return
        }
        request(request = { monthAnimeModel.getMonthAnimeData(partUrl) }, success = {
            pageNumberBean = it.second
            monthAnimeList.tryEmitLoadMore(oldData, it.first)
        }, error = {
            monthAnimeList.tryEmitError(oldData, it.message)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}