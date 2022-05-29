package com.skyd.imomoe

import android.app.Application
import android.content.Context
import com.efs.sdk.launch.LaunchManager
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.skyd.imomoe.ext.theme.getAttrColor
import com.skyd.imomoe.ext.theme.initDarkMode
import com.skyd.imomoe.util.CrashHandler
import com.skyd.imomoe.util.PushHelper
import com.skyd.imomoe.util.Util.getManifestMetaValue
import com.skyd.imomoe.util.release
import com.skyd.imomoe.view.component.player.PlayerCore
import com.umeng.commonsdk.UMConfigure
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {

    override fun attachBaseContext(base: Context?) {
        LaunchManager.onTraceApp(this, LaunchManager.APP_ATTACH_BASE_CONTEXT, true)
        super.attachBaseContext(base)
        LaunchManager.onTraceApp(this, LaunchManager.APP_ATTACH_BASE_CONTEXT, false)
    }

    override fun onCreate() {
        super.onCreate()
        appContext = this

        initDarkMode()

        release {
            // Crash提示
            CrashHandler.getInstance(this)

            // 友盟
            // 初始化组件化基础库, 所有友盟业务SDK都必须调用此初始化接口。
            UMConfigure.preInit(
                this,
                getManifestMetaValue("UMENG_APPKEY"),
                getManifestMetaValue("UMENG_CHANNEL")
            )

            UMConfigure.setLogEnabled(BuildConfig.DEBUG)

            PushHelper.preInit(applicationContext)
        }

        PlayerCore.onAppCreate()

        LaunchManager.onTraceApp(this, LaunchManager.APP_ON_CREATE, false)
    }

    companion object {
        init {
            // 防止内存泄漏
            // 设置全局默认配置（优先级最低，会被其他设置覆盖）
            SmartRefreshLayout.setDefaultRefreshInitializer { context, layout -> //开始设置全局的基本参数（可以被下面的DefaultRefreshHeaderCreator覆盖）
                layout.setReboundDuration(150)
                layout.setFooterHeight(100f)
                layout.setHeaderTriggerRate(0.5f)
                layout.setDisableContentWhenLoading(false)
                layout.setPrimaryColors(context.getAttrColor(R.attr.colorSurface))
            }

            // 全局设置默认的 Header
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout -> //开始设置全局的基本参数（这里设置的属性只跟下面的MaterialHeader绑定，其他Header不会生效，能覆盖DefaultRefreshInitializer的属性和Xml设置的属性）
                layout.setEnableHeaderTranslationContent(true)
                    .setHeaderHeight(70f)
                    .setDragRate(0.6f)
                MaterialHeader(context)
                    .setColorSchemeColors(context.getAttrColor(R.attr.colorPrimary))
                    .setShowBezierWave(true)
            }

            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
                layout.setEnableFooterTranslationContent(true)
                BallPulseFooter(context)
                    .setAnimatingColor(context.getAttrColor(R.attr.colorPrimary))
            }
        }
    }
}

lateinit var appContext: Context