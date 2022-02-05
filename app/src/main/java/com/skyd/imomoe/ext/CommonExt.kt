package com.skyd.imomoe.ext

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