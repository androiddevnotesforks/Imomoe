package com.skyd.imomoe.ext

import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.Duration

/**
 * 为空，执行nullAction，否则执行notNullAction
 */
inline fun <reified T> T?.notNull(notNullAction: (T) -> Unit, nullAction: () -> Unit = {}) {
    if (this != null) {
        notNullAction.invoke(this)
    } else {
        nullAction.invoke()
    }
}

fun Long.toTimeString(
    pattern: String = "yyyy-MM-dd HH:mm:ss",
    locale: Locale = Locale.getDefault()
): String = Date(this).toTimeString(pattern, locale)

fun Date.toTimeString(
    pattern: String = "yyyy-MM-dd HH:mm:ss",
    locale: Locale = Locale.getDefault()
): String {
    val format = SimpleDateFormat(pattern, locale)
    return format.format(this)
}

suspend fun doAfter(time: Duration, onFinish: () -> Unit) {
    delay(time)
    onFinish()
}