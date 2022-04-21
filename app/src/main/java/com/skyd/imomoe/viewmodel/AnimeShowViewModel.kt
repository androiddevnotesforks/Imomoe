package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IAnimeShowModel
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AnimeShowViewModel @Inject constructor(
    private val animeShowModel: IAnimeShowModel
) : ViewModel() {
    var partUrl: String = ""
    var mldAnimeShowList: MutableLiveData<List<Any>?> = MutableLiveData()
    var mldLoadMoreAnimeShowList: MutableLiveData<List<Any>?> = MutableLiveData()
    private var pageNumberBean: PageNumberBean? = null

    fun getAnimeShowData() {
        pageNumberBean = null
        request(request = { animeShowModel.getAnimeShowData(partUrl) }, success = {
            pageNumberBean = it.second
            mldAnimeShowList.postValue(it.first)
        }, error = {
            mldAnimeShowList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun loadMoreAnimeShowData() {
        val partUrl = pageNumberBean?.route
        if (partUrl == null) {
            mldLoadMoreAnimeShowList.postValue(emptyList())
            appContext.getString(R.string.no_more_info).showToast()
            return
        }
        request(request = { animeShowModel.getAnimeShowData(partUrl) }, success = {
            pageNumberBean = it.second
            mldLoadMoreAnimeShowList.postValue(it.first)
        }, error = {
            mldLoadMoreAnimeShowList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}