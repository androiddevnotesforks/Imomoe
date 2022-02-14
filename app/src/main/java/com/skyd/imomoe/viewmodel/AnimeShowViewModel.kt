package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.AnimeShowModel
import com.skyd.imomoe.model.interfaces.IAnimeShowModel
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.SerializableRecycledViewPool


class AnimeShowViewModel : ViewModel() {
    private val animeShowModel: IAnimeShowModel by lazy {
        DataSourceManager.create(IAnimeShowModel::class.java) ?: AnimeShowModel()
    }
    var viewPool: SerializableRecycledViewPool? = null
    var animeShowList: MutableList<Any> = ArrayList()
    var mldGetAnimeShowList: MutableLiveData<Pair<ResponseDataType, MutableList<Any>>> =
        MutableLiveData()
    var pageNumberBean: PageNumberBean? = null

    private var isRequesting = false

    fun getAnimeShowData(partUrl: String, isRefresh: Boolean = true) {
        if (isRequesting) return
        isRequesting = true
        pageNumberBean = null
        request(request = { animeShowModel.getAnimeShowData(partUrl) }, success = {
            pageNumberBean = it.second
            mldGetAnimeShowList.postValue(
                Pair(
                    if (isRefresh) ResponseDataType.REFRESH else ResponseDataType.LOAD_MORE,
                    it.first
                )
            )
            isRequesting = false
        }, error = {
            mldGetAnimeShowList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
            isRequesting = false
            (App.context.getString(R.string.get_data_failed) + "\n" + it.message).showToast()
        })
    }
}