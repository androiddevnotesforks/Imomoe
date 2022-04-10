package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.AnimeShowModel
import com.skyd.imomoe.model.interfaces.IAnimeShowModel
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.showToast


class AnimeShowViewModel : ViewModel() {
    private val animeShowModel: IAnimeShowModel by lazy {
        DataSourceManager.create(IAnimeShowModel::class.java) ?: AnimeShowModel()
    }
    var partUrl: String = ""
    var mldAnimeShowList: MutableLiveData<List<Any>?> = MutableLiveData()
    var mldLoadMoreAnimeShowList: MutableLiveData<List<Any>?> = MutableLiveData()
    private var pageNumberBean: PageNumberBean? = null

    fun getAnimeShowData(partUrl: String) {
        pageNumberBean = null
        request(request = { animeShowModel.getAnimeShowData(partUrl) }, success = {
            pageNumberBean = it.second
            mldAnimeShowList.postValue(it.first)
        }, error = {
            mldAnimeShowList.postValue(null)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun loadMoreAnimeShowData() {
        val partUrl = pageNumberBean?.actionUrl
        if (partUrl == null) {
            mldLoadMoreAnimeShowList.postValue(emptyList())
            App.context.getString(R.string.no_more_info).showToast()
            return
        }
        request(request = { animeShowModel.getAnimeShowData(partUrl) }, success = {
            pageNumberBean = it.second
            mldLoadMoreAnimeShowList.postValue(it.first)
        }, error = {
            mldLoadMoreAnimeShowList.postValue(null)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}