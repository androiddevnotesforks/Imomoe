package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.bean.License1Bean
import com.skyd.imomoe.bean.LicenseHeader1Bean
import com.skyd.imomoe.config.Const.ActionUrl
import com.skyd.imomoe.databinding.ActivityLicenseBinding
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.License1Proxy
import com.skyd.imomoe.view.adapter.variety.proxy.LicenseHeader1Proxy


class LicenseActivity : BaseActivity<ActivityLicenseBinding>() {
    private val list: MutableList<Any> = ArrayList()
    private val adapter: VarietyAdapter =
        VarietyAdapter(mutableListOf(LicenseHeader1Proxy(), License1Proxy()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        list += LicenseHeader1Bean()
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://source.android.com/",
            "Android Open Source Project",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/jhy/jsoup",
            "jsoup",
            "MIT License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/coil-kt/coil",
            "coil",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/CarGuo/GSYVideoPlayer",
            "GSYVideoPlayer",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/square/okhttp",
            "okhttp",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/square/retrofit",
            "retrofit",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/getActivity/XXPermissions",
            "XXPermissions",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/Kotlin/kotlinx.coroutines",
            "kotlinx.coroutines",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/lingochamp/FileDownloader",
            "FileDownloader",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/4thline/cling",
            "cling",
            "LGPL License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/eclipse/jetty.project",
            "jetty.project",
            "EPL-2.0, Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/NanoHttpd/nanohttpd",
            "nanohttpd",
            "BSD-3-Clause License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/greenrobot/EventBus",
            "EventBus",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/scwang90/SmartRefreshLayout",
            "SmartRefreshLayout",
            "Apache-2.0 License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/KwaiAppTeam/AkDanmaku",
            "AkDanmaku",
            "MIT License"
        )
        list += License1Bean(
            ActionUrl.ANIME_BROWSER,
            "https://github.com/thegrizzlylabs/sardine-android",
            "sardine-android",
            "Apache-2.0 License"
        )
        adapter.dataList = list

        mBinding.run {
            tbLicenseActivity.setNavigationOnClickListener { finish() }
            rvLicenseActivity.layoutManager = LinearLayoutManager(this@LicenseActivity)
            rvLicenseActivity.adapter = adapter
        }
    }

    override fun getBinding() = ActivityLicenseBinding.inflate(layoutInflater)
}
