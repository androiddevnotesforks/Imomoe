package com.skyd.imomoe.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.state.DataState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

fun <T> ViewModel.request(
    request: suspend () -> T,
    success: ((T) -> Unit)? = null,
    error: ((Throwable) -> Unit)? = null,
    // request结束后回调，不论成功还是失败，在success和error后调用。
    // 注意，finish与finally不同，在runCatching中return会导致finish不执行
    finish: (() -> Unit)? = null,
    coroutineContext: CoroutineContext = Dispatchers.IO
): Job {
    return viewModelScope.launch(coroutineContext) {
        runCatching {
            request.invoke()
        }.onSuccess {
            success?.invoke(it)
        }.onFailure {
            it.printStackTrace()
            error?.invoke(it)
        }.also {
            finish?.invoke()
        }
    }
}

fun <T> MutableStateFlow<DataState<List<T>>>.tryEmitLoadMore(
    oldData: DataState<List<T>>,
    newData: List<T>
) {
    tryEmit(
        DataState.Success(
            oldData.readOrNull()
                .orEmpty()
                .toMutableList()
                .apply { addAll(newData) }
        )
    )
}

fun <T> MutableStateFlow<DataState<T>>.tryEmitError(
    oldData: DataState<T>,
    errorMessage: String? = ""
) {
    tryEmit(
        // 之前有旧数据，则不变化
        if (oldData.readOrNull() == null) {
            DataState.Error(errorMessage.orEmpty())
        } else {
            oldData
        }
    )
}