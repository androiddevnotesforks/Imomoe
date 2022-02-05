package com.skyd.imomoe.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
