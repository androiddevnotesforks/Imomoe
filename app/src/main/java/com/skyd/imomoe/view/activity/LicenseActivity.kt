package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.bean.License1Bean
import com.skyd.imomoe.bean.LicenseHeader1Bean
import com.skyd.imomoe.databinding.ActivityLicenseBinding
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.processor.OpenBrowserProcessor
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
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://source.android.com/")
            }.toString(),
            "Android Open Source Project",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/google/accompanist")
            }.toString(),
            "Accompanist",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/jhy/jsoup")
            }.toString(),
            "jsoup",
            "MIT License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/coil-kt/coil")
            }.toString(),
            "Coil",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/CarGuo/GSYVideoPlayer")
            }.toString(),
            "GSYVideoPlayer",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/square/okhttp")
            }.toString(),
            "OkHttp",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/square/retrofit")
            }.toString(),
            "Retrofit",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/getActivity/XXPermissions")
            }.toString(),
            "XXPermissions",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/Kotlin/kotlinx.coroutines")
            }.toString(),
            "kotlinx.coroutines",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/AriaLyy/Aria")
            }.toString(),
            "Aria",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/4thline/cling")
            }.toString(),
            "Cling",
            "LGPL License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/eclipse/jetty.project")
            }.toString(),
            "Eclipse Jetty",
            "EPL-2.0, Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/NanoHttpd/nanohttpd")
            }.toString(),
            "NanoHTTPD",
            "BSD-3-Clause License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/greenrobot/EventBus")
            }.toString(),
            "EventBus",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/scwang90/SmartRefreshLayout")
            }.toString(),
            "SmartRefreshLayout",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/KwaiAppTeam/AkDanmaku")
            }.toString(),
            "AkDanmaku",
            "MIT License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/thegrizzlylabs/sardine-android")
            }.toString(),
            "sardine-android",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/apache/commons-text")
            }.toString(),
            "Apache Commons Text",
            "Apache-2.0 License"
        )
        list += License1Bean(
            OpenBrowserProcessor.route.buildRouteUri {
                appendQueryParameter("url", "https://github.com/vadiole/colorpicker")
            }.toString(),
            "Color Picker",
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
