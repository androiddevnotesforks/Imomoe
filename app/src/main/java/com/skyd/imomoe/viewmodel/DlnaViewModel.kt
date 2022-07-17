package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.dlna.Utils.isLocalMediaAddress
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.flow.MutableStateFlow
import org.fourthline.cling.model.meta.Device


class DlnaViewModel : ViewModel() {
    val uiState: MutableStateFlow<DlnaUiState> = MutableStateFlow(DlnaUiState.None)

    fun initData(url: String, title: String) {
        uiState.tryEmit(DlnaUiState.Initializing(url = url, title = title))
        request(request = {
            // 视频不是本地文件
            if (!url.isLocalMediaAddress()) {
                Util.getRedirectUrl(url)
            } else url
        }, success = {
            uiState.tryEmit(DlnaUiState.Initialized(url = it, title = title))
        }, error = {
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun addDevice(device: Device<*, *, *>) {
        when (val currentState = uiState.value) {
            is DlnaUiState.Initialized -> {
                uiState.tryEmit(
                    DlnaUiState.Searching(
                        url = currentState.url,
                        title = currentState.title,
                        dataList = listOf(device)
                    )
                )
            }
            is DlnaUiState.Searching -> {
                uiState.tryEmit(
                    DlnaUiState.Searching(
                        url = currentState.url,
                        title = currentState.title,
                        dataList = currentState.dataList.toMutableList() + device
                    )
                )
            }
            else -> {}
        }
    }

    fun removeDevice(device: Device<*, *, *>) {
        when (val currentState = uiState.value) {
            is DlnaUiState.Searching -> {
                uiState.tryEmit(
                    DlnaUiState.Searching(
                        url = currentState.url,
                        title = currentState.title,
                        dataList = currentState.dataList.toMutableList() - device
                    )
                )
            }
            else -> {}
        }
    }
}

sealed class DlnaUiState(open val url: String, open val title: String) {
    object None : DlnaUiState("", "")

    data class Initializing(
        override val url: String, override val title: String
    ) : DlnaUiState(url, title)

    data class Initialized(
        override val url: String, override val title: String
    ) : DlnaUiState(url, title)

    data class Searching(
        override val url: String,
        override val title: String,
        val dataList: List<Any>
    ) : DlnaUiState(url, title)

    fun readOrNull(): List<Any>? = (this as? Searching)?.dataList
}