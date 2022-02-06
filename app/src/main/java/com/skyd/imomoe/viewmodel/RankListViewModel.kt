package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.AnimeCoverBean
import com.skyd.imomoe.bean.ResponseDataType
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.RankListModel
import com.skyd.imomoe.model.interfaces.IRankListModel
import com.skyd.imomoe.util.showToast
import java.util.*
import kotlin.collections.ArrayList


class RankListViewModel : ViewModel() {
    private val rankModel: IRankListModel by lazy {
        DataSourceManager.create(IRankListModel::class.java) ?: RankListModel()
    }
    var isRequesting = false
    var rankList: MutableList<AnimeCoverBean> = Collections.synchronizedList(ArrayList())
    var pageNumberBean: PageNumberBean? = null
    var mldRankData: MutableLiveData<Pair<ResponseDataType, MutableList<AnimeCoverBean>>> =
        MutableLiveData()

    fun getRankListData(partUrl: String, isRefresh: Boolean = true) {
        if (isRequesting) return
        isRequesting = true
        request(request = { rankModel.getRankListData(partUrl) }, success = {
            pageNumberBean = it.second
            mldRankData.postValue(
                Pair(
                    if (isRefresh) ResponseDataType.REFRESH else ResponseDataType.LOAD_MORE,
                    it.first.toMutableList()
                )
            )
            isRequesting = false
        }, error = {
            mldRankData.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
            isRequesting = false
            it.message?.showToast(Toast.LENGTH_LONG)
        })
    }
}