package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IRankModel
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class RankViewModel @Inject constructor(
    private val rankModel: IRankModel
) : ViewModel() {
    var isRequesting = false
    var mldRankData: MutableLiveData<List<TabBean>?> = MutableLiveData()

    fun getRankTabData() {
        if (isRequesting) return
        isRequesting = true
        request(request = { rankModel.getRankTabData() }, success = {
            mldRankData.postValue(it)
        }, error = {
            mldRankData.postValue(null)
            it.message?.showToast(Toast.LENGTH_LONG)
        }, finish = { isRequesting = false })
    }
}