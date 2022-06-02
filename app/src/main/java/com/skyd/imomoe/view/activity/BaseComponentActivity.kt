package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.efs.sdk.launch.LaunchManager
import com.google.android.material.color.DynamicColors
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

        if (Util.lastReadUserNoticeVersion() >= Const.Common.USER_NOTICE_VERSION) {
            initUM()
        }

        LaunchManager.onTraceApp(application, LaunchManager.PAGE_ON_CREATE, false)
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