package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.ext.tryEmitError
import com.skyd.imomoe.ext.tryEmitLoadMore
import com.skyd.imomoe.model.interfaces.IAnimeShowModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class AnimeShowViewModel @Inject constructor(
    private val animeShowModel: IAnimeShowModel
) : ViewModel() {
    var partUrl: String = ""
    val animeShowList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)
    private var pageNumberBean: PageNumberBean? = null

    fun getAnimeShowData() {
        pageNumberBean = null
        animeShowList.tryEmit(DataState.Refreshing)
        request(request = { animeShowModel.getAnimeShowData(partUrl) }, success = {
            pageNumberBean = it.second
            animeShowList.tryEmit(DataState.Success(it.first))
        }, error = {
            animeShowList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun loadMoreAnimeShowData() {
        val partUrl = pageNumberBean?.route
        val oldData = animeShowList.value
        animeShowList.tryEmit(DataState.Loading)
        if (partUrl == null) {
            animeShowList.tryEmit(oldData)
            appContext.getString(R.string.no_more_info).showToast()
            return
        }
        request(request = { animeShowModel.getAnimeShowData(partUrl) }, success = {
            pageNumberBean = it.second
            animeShowList.tryEmitLoadMore(oldData, it.first)
        }, error = {
            animeShowList.tryEmitError(oldData, it.message)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}