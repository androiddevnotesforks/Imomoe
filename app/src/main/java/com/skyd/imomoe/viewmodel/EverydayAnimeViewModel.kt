package com.skyd.imomoe.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IEverydayAnimeModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.Util.getRealDayOfWeek
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.*
import javax.inject.Inject


@HiltViewModel
class EverydayAnimeViewModel @Inject constructor(
    private val everydayAnimeModel: IEverydayAnimeModel
) : ViewModel() {
    var selectedTabIndex = -1
    val header: MutableStateFlow<String> =
        MutableStateFlow(appContext.getString(R.string.everyday_anime_list))
    val everydayAnimeList: MutableStateFlow<DataState<List<List<Any>>>> =
        MutableStateFlow(DataState.Empty)
    val tabList: MutableStateFlow<DataState<List<TabBean>>> = MutableStateFlow(DataState.Empty)

    fun getEverydayAnimeData() {
        tabList.tryEmit(DataState.Refreshing)
        everydayAnimeList.tryEmit(DataState.Refreshing)
        request(request = {
            everydayAnimeModel.getEverydayAnimeData().apply {
                if (first.size != second.size) throw Exception("tabs count != tabList count")
            }
        }, success = {
            selectedTabIndex = getRealDayOfWeek(
                Calendar.getInstance(Locale.getDefault()).get(Calendar.DAY_OF_WEEK)
            ) - 1
            tabList.tryEmit(DataState.Success(it.first))
            everydayAnimeList.tryEmit(DataState.Success(it.second))
            header.tryEmit(it.third)
        }, error = {
            selectedTabIndex = -1
            everydayAnimeList.tryEmit(DataState.Empty)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast(Toast.LENGTH_LONG)
        })
    }
}