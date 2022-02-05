package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.RankModel
import com.skyd.imomoe.model.interfaces.IRankModel
import com.skyd.imomoe.util.showToast
import java.util.*


class RankViewModel : ViewModel() {
    private val rankModel: IRankModel by lazy {
        DataSourceManager.create(IRankModel::class.java) ?: RankModel()
    }
    var isRequesting = false
    var tabList: MutableList<TabBean> = Collections.synchronizedList(ArrayList())
    var mldRankData: MutableLiveData<Boolean> = MutableLiveData()

    fun getRankTabData() {
        if (isRequesting) return
        isRequesting = true
        request(request = { rankModel.getRankTabData() }, success = {
            tabList.clear()
            tabList.addAll(it)
            mldRankData.postValue(true)
        }, error = {
            mldRankData.postValue(false)
            tabList.clear()
            it.message?.showToast(Toast.LENGTH_LONG)
        }, finish = { isRequesting = false })
    }

    companion object {
        const val TAG = "RankViewModel"
    }
}