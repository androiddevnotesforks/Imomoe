package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.efs.sdk.launch.LaunchManager
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.android.material.color.DynamicColors
import com.google.android.material.composethemeadapter3.Mdc3Theme
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.ext.initUM
import com.skyd.imomoe.ext.theme.appThemeRes
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.eventbus.EventBusSubscriber
import org.greenrobot.eventbus.EventBus

abstract class BaseComponentActivity : ComponentActivity() {
    protected open var activityThemeRes = appThemeRes.value

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(activityThemeRes)
        appThemeRes.collectWithLifecycle(this) {
            if (activityThemeRes != it) {
                // 壁纸取色
                if (it == R.style.Theme_Anime_Dynamic) {
                    DynamicColors.applyToActivityIfAvailable(this@BaseComponentActivity)
                }
                recreate()
            }
        }

        // 全屏
        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (Util.lastReadUserNoticeVersion() >= Const.Common.USER_NOTICE_VERSION) {
            initUM()
        }

        LaunchManager.onTraceApp(application, LaunchManager.PAGE_ON_CREATE, false)
    }

    fun setContentBase(content: @Composable () -> Unit) {
        setContent {
            Mdc3Theme(
                setTextColors = true,
                setDefaultFontFamily = true
            ) {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()

                SideEffect {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = useDarkIcons
                    )
                }

                content.invoke()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if (this is EventBusSubscriber) EventBus.getDefault().register(this)

        LaunchManager.onTracePage(this, LaunchManager.PAGE_ON_START, true)
    }

    override fun onStop() {
        super.onStop()
        if (this is EventBusSubscriber && EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)

        LaunchManager.onTracePage(this, LaunchManager.PAGE_ON_STOP, true)
    }

    override fun onRestart() {
        super.onRestart()
        LaunchManager.onTracePage(this, LaunchManager.PAGE_ON_RE_START, true)
    }

    override fun onResume() {
        super.onResume()
        LaunchManager.onTracePage(this, LaunchManager.PAGE_ON_RESUME, false)
    }
}