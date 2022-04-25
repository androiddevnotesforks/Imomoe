package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IRankModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class RankViewModel @Inject constructor(
    private val rankModel: IRankModel
) : ViewModel() {
    var isRequesting = false
    val rankData: MutableStateFlow<DataState<List<TabBean>>> = MutableStateFlow(DataState.Empty)

    init {
        getRankTabData()
    }

    fun getRankTabData() {
        if (isRequesting) return
        isRequesting = true
        request(request = { rankModel.getRankTabData() }, success = {
            rankData.tryEmit(DataState.Success(it))
        }, error = {
            rankData.tryEmit(DataState.Error(it.message.orEmpty()))
            it.message?.showToast(Toast.LENGTH_LONG)
        }, finish = { isRequesting = false })
    }
}