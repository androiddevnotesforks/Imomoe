package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.ext.tryEmitError
import com.skyd.imomoe.ext.tryEmitLoadMore
import com.skyd.imomoe.model.interfaces.ISearchModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchModel: ISearchModel
) : ViewModel() {
    var keyword = ""
    val searchResultList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)
    val searchHistoryList: MutableStateFlow<DataState<List<Any>>> =
        MutableStateFlow(DataState.Empty)
    val deleteCompleted: MutableSharedFlow<SearchHistoryBean?> =
        MutableSharedFlow(extraBufferCapacity = 1)
    private var pageNumberBean: PageNumberBean? = null

    fun getSearchData(keyWord: String, partUrl: String = "") {
        searchResultList.tryEmit(DataState.Refreshing)
        request(request = { searchModel.getSearchData(keyWord, partUrl) }, success = {
            pageNumberBean = it.second
            this@SearchViewModel.keyword = keyWord
            searchResultList.tryEmit(DataState.Success(it.first))
        }, error = {
            searchResultList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun loadMoreSearchData() {
        val partUrl = pageNumberBean?.route
        val oldData = searchResultList.value
        searchResultList.tryEmit(DataState.Loading)
        if (partUrl == null) {
            appContext.getString(R.string.no_more_info).showToast()
            searchResultList.tryEmit(oldData)
            return
        }
        request(request = { searchModel.getSearchData(keyword, partUrl) }, success = {
            pageNumberBean = it.second
            searchResultList.tryEmitLoadMore(oldData, it.first)
        }, error = {
            searchResultList.tryEmitError(oldData, it.message)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun getSearchHistoryData() {
        request(request = {
            getAppDataBase().searchHistoryDao().getSearchHistoryList()
        }, success = {
            searchHistoryList.tryEmit(DataState.Success(it))
        }, error = {
            searchHistoryList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun insertSearchHistory(searchHistoryBean: SearchHistoryBean) {
        request(request = {
            val list = getAppDataBase().searchHistoryDao().getSearchHistoryList().toMutableList()
            val index = list.indexOf(searchHistoryBean)
            if (index != -1) {
                list.removeAt(index)
                list.add(0, searchHistoryBean)
                getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.title)
                getAppDataBase().searchHistoryDao().insertSearchHistory(searchHistoryBean)
            } else {
                list.add(0, searchHistoryBean)
                getAppDataBase().searchHistoryDao().insertSearchHistory(searchHistoryBean)
            }
            list
        }, success = {
            searchHistoryList.tryEmit(DataState.Success(it))
        }, error = {
            searchHistoryList.tryEmit(DataState.Error(it.message.orEmpty()))
        })
    }

    fun deleteSearchHistory(searchHistoryBean: SearchHistoryBean) {
        request(request = {
            getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.timeStamp)
        }, success = {
            searchHistoryList.tryEmit(
                DataState.Success(
                    searchHistoryList.value
                        .readOrNull()
                        .orEmpty()
                        .toMutableList()
                        .apply { remove(searchHistoryBean) }
                )
            )
            deleteCompleted.tryEmit(searchHistoryBean)
        }, error = {
            deleteCompleted.tryEmit(null)
        })
    }
}