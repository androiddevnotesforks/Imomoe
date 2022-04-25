package com.skyd.imomoe.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

inline fun <T> Flow<T>.collectWithLifecycle(
    lifecycleOwner: LifecycleOwner,
    crossinline block: suspend CoroutineScope.(data: T) -> Unit
): Job {
    return lifecycleOwner.lifecycleScope.launch {
        this@collectWithLifecycle.collect {
            block(it)
        }
    }
}