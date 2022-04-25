package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IHomeModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeModel: IHomeModel
) : ViewModel() {
    val allTabList: MutableStateFlow<DataState<List<TabBean>>> = MutableStateFlow(DataState.Empty)
    var currentTab = -1

    init {
        getAllTabData()
    }

    fun getAllTabData() {
        allTabList.tryEmit(DataState.Refreshing)
        request(request = { homeModel.getAllTabData() }, success = {
            allTabList.tryEmit(DataState.Success(it))
        }, error = {
            allTabList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast(Toast.LENGTH_LONG)
        })
    }
}