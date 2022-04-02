package com.skyd.imomoe.ext

import android.app.Activity
import android.content.Context
import com.skyd.imomoe.BuildConfig
import com.skyd.imomoe.util.PushHelper
import com.skyd.imomoe.util.Util
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import com.umeng.message.PushAgent
import com.umeng.message.UmengNotificationClickHandler
import com.umeng.message.entity.UMessage

var uMengInitialized: Boolean = false
    private set

fun Activity.initUM() {
    if (uMengInitialized || BuildConfig.DEBUG) return
    uMengInitialized = false
    UMConfigure.init(
        this,
        UMConfigure.DEVICE_TYPE_PHONE,
        BuildConfig.UMENG_MESSAGE_SECRET
    )

    // 选择AUTO页面采集模式，统计SDK基础指标无需手动埋点可自动采集。
    MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO)

    PushHelper.init(applicationContext)
    PushAgent.getInstance(applicationContext).apply {
        resourcePackageName = BuildConfig.APPLICATION_ID
        notificationClickHandler = object : UmengNotificationClickHandler() {
            override fun dealWithCustomAction(context: Context, msg: UMessage) {
                super.dealWithCustomAction(context, msg)
                Util.process(context, msg.custom)
            }
        }
    }

    PushAgent.getInstance(this).onAppStart()
}