package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.showToast


class FavoriteViewModel : ViewModel() {
    var mldFavoriteList: MutableLiveData<List<Any>?> = MutableLiveData()

    fun getFavoriteData() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnimeList()
        }, success = {
            it.sortWith { o1, o2 ->
                // 负数表示按时间戳从大到小排列
                -o1.time.compareTo(o2.time)
            }
            mldFavoriteList.postValue(it)
        }, error = {
            mldFavoriteList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}