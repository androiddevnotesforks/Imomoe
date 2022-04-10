package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.EverydayAnimeModel
import com.skyd.imomoe.model.interfaces.IEverydayAnimeModel
import com.skyd.imomoe.util.Util.getRealDayOfWeek
import com.skyd.imomoe.util.showToast
import java.util.*


class EverydayAnimeViewModel : ViewModel() {
    private val everydayAnimeModel: IEverydayAnimeModel by lazy {
        DataSourceManager.create(IEverydayAnimeModel::class.java) ?: EverydayAnimeModel()
    }
    var header: String = ""
    var selectedTabIndex = -1
    var mldHeader: MutableLiveData<String> = MutableLiveData()
    var mldTabList: MutableLiveData<List<TabBean>> = MutableLiveData()
    var mldEverydayAnimeList: MutableLiveData<List<List<Any>>?> = MutableLiveData()

    fun getEverydayAnimeData() {
        request(request = {
            everydayAnimeModel.getEverydayAnimeData().apply {
                if (first.size != second.size) throw Exception("tabs count != tabList count")
            }
        }, success = {
            selectedTabIndex = getRealDayOfWeek(
                Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_WEEK)
            ) - 1
            header = it.third
            mldTabList.postValue(it.first)
            mldEverydayAnimeList.postValue(it.second)
            mldHeader.postValue(header)
        }, error = {
            selectedTabIndex = -1
            mldEverydayAnimeList.postValue(null)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast(Toast.LENGTH_LONG)
        })
    }
}