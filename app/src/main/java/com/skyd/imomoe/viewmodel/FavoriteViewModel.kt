package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.showToast


class FavoriteViewModel : ViewModel() {
    var favoriteList: MutableList<Any> = ArrayList()
    var mldFavoriteList: MutableLiveData<Boolean> = MutableLiveData()

    fun getFavoriteData() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnimeList()
        }, success = {
            it.sortWith { o1, o2 ->
                // 负数表示按时间戳从大到小排列
                -o1.time.compareTo(o2.time)
            }
            favoriteList.clear()
            favoriteList.addAll(it)
            mldFavoriteList.postValue(true)
        }, error = {
            favoriteList.clear()
            mldFavoriteList.postValue(false)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}