package com.skyd.imomoe

import android.app.Application
import android.content.Context
import com.scwang.smart.refresh.footer.BallPulseFooter
import com.scwang.smart.refresh.header.MaterialHeader
import com.scwang.smart.refresh.layout.SmartRefreshLayout
import com.skyd.imomoe.ext.theme.getAttrColor
import com.skyd.imomoe.ext.theme.initDarkMode
import com.skyd.imomoe.util.CrashHandler
import com.skyd.imomoe.util.release
import com.skyd.imomoe.view.component.player.PlayerCore
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = this

        initDarkMode()

        release {
            // Crash提示
            CrashHandler.getInstance(this)
        }

        PlayerCore.onAppCreate()
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