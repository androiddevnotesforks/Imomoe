package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.SearchModel
import com.skyd.imomoe.model.interfaces.ISearchModel
import com.skyd.imomoe.util.showToast


class SearchViewModel : ViewModel() {
    private val searchModel: ISearchModel by lazy {
        DataSourceManager.create(ISearchModel::class.java) ?: SearchModel()
    }

    var searchResultList: MutableList<AnimeCoverBean> = ArrayList()
    var mldSearchResultList: MutableLiveData<Pair<ResponseDataType, MutableList<AnimeCoverBean>>> =
        MutableLiveData()
    var keyWord = ""
    var searchHistoryList: MutableList<SearchHistoryBean> = ArrayList()
    var mldSearchHistoryList: MutableLiveData<Boolean> = MutableLiveData()
    var mldInsertCompleted: MutableLiveData<Boolean> = MutableLiveData()
    var mldUpdateCompleted: MutableLiveData<Int> = MutableLiveData()
    var mldDeleteCompleted: MutableLiveData<Int> = MutableLiveData()
    var pageNumberBean: PageNumberBean? = null

    fun getSearchData(keyWord: String, isRefresh: Boolean = true, partUrl: String = "") {
        request(request = { searchModel.getSearchData(keyWord, partUrl) }, success = {
            pageNumberBean = it.second
            this@SearchViewModel.keyWord = keyWord
            mldSearchResultList.postValue(
                Pair(
                    if (isRefresh) ResponseDataType.REFRESH
                    else ResponseDataType.LOAD_MORE, it.first
                )
            )
        }, error = {
            mldSearchResultList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun getSearchHistoryData() {
        request(request = {
            getAppDataBase().searchHistoryDao().getSearchHistoryList()
        }, success = {
            searchHistoryList.clear()
            searchHistoryList.addAll(it)
        }, error = {
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        }, finish = { mldSearchHistoryList.postValue(true) })
    }

    fun insertSearchHistory(searchHistoryBean: SearchHistoryBean) {
        request(request = {
            if (searchHistoryList.isEmpty()) searchHistoryList.addAll(
                getAppDataBase().searchHistoryDao().getSearchHistoryList()
            )
            val index = searchHistoryList.indexOf(searchHistoryBean)
            if (index != -1) {
                searchHistoryList.removeAt(index)
                searchHistoryList.add(0, searchHistoryBean)
                getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.title)
                getAppDataBase().searchHistoryDao().insertSearchHistory(searchHistoryBean)
            } else {
                searchHistoryList.add(0, searchHistoryBean)
                getAppDataBase().searchHistoryDao().insertSearchHistory(searchHistoryBean)
            }
        }, finish = { mldInsertCompleted.postValue(true) })
    }

    fun updateSearchHistory(searchHistoryBean: SearchHistoryBean, itemPosition: Int) {
        request(request = {
            searchHistoryList[itemPosition] = searchHistoryBean
            getAppDataBase().searchHistoryDao().updateSearchHistory(searchHistoryBean)
        }, finish = {
            mldUpdateCompleted.postValue(itemPosition)
        })
    }

    fun deleteSearchHistory(itemPosition: Int) {
        request(request = {
            val searchHistoryBean = searchHistoryList.removeAt(itemPosition)
            getAppDataBase().searchHistoryDao().deleteSearchHistory(searchHistoryBean.timeStamp)
        }, finish = { mldDeleteCompleted.postValue(itemPosition) })
    }
}