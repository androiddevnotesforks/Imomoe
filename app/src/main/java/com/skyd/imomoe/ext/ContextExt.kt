package com.skyd.imomoe.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

val Context.activity: Activity
    get() {
        var ctx = this
        while (ctx is ContextWrapper) {
            if (ctx is Activity) {
                return ctx
            }
            ctx = ctx.baseContext
        }
        error("can't find activity: $this")
    }