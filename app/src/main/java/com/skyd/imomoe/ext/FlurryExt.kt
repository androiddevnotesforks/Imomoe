package com.skyd.imomoe.ext

import android.app.Application
import com.flurry.android.FlurryAgent
import com.skyd.imomoe.BuildConfig

var initializedFlurry: Boolean = false
    private set

fun initializeFlurry(context: Application) {
    if (initializedFlurry) return
    initializedFlurry = true
    if (BuildConfig.DEBUG) return
    FlurryAgent.Builder()
        .withCaptureUncaughtExceptions(true)
        .withLogEnabled(BuildConfig.DEBUG)
        .build(context, BuildConfig.FLURRY_API_KEY)
}