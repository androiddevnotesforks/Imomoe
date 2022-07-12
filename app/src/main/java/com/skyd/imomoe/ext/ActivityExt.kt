package com.skyd.imomoe.ext

import android.view.WindowManager
import androidx.core.app.ComponentActivity
import com.skyd.imomoe.ext.theme.appThemeRes
import kotlinx.coroutines.flow.MutableSharedFlow

val recreateAllBaseActivity: MutableSharedFlow<Unit> = MutableSharedFlow(extraBufferCapacity = 1)

var disableScreenshot: Boolean = sharedPreferences().getBoolean("disableScreenshot", false)
    set(value) {
        if (value == field) return
        sharedPreferences().editor { putBoolean("disableScreenshot", value) }
        field = value
        recreateAllBaseActivity.tryEmit(Unit)
    }

fun ComponentActivity.beforeSetContentView() {
    recreateAllBaseActivity.collectWithLifecycle(this) { recreate() }

    // 设置主题
    setTheme(appThemeRes)

    // 是否禁止截图
    if (disableScreenshot) {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    } else {
        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}