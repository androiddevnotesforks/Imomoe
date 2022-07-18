package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.License1Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.route.processor.OpenBrowserProcessor
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.BackIcon


class LicenseActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentBase {
            LicenseScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LicenseScreen() {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AnimeTopBar(
                title = {
                    Text(text = stringResource(R.string.open_source_licenses))
                },
                navigationIcon = {
                    BackIcon(onClick = { context.activity.finish() })
                }
            )
        }
    ) {
        val dataList = remember { initData() }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it),
            contentPadding = WindowInsets.navigationBars.asPaddingValues()
        ) {
            item {
                LicenseHeader()
            }
            items(items = dataList) { item ->
                LicenseItem(item.title, item.license) {
                    item.route.route(context)
                }
            }
        }
    }
}

@Composable
fun LicenseHeader() {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f), text = stringResource(id = R.string.license_name),
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.weight(1f), text = stringResource(id = R.string.license),
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun LicenseItem(name: String, license: String, onClick: (() -> Unit)? = null) {
    Row(modifier = Modifier
        .run {
            if (onClick == null) this
            else clickable(onClick = onClick)
        }
        .padding(horizontal = 16.dp, vertical = 10.dp)) {
        Text(
            modifier = Modifier.weight(1f), text = name,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.weight(1f), text = license,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

private fun initData(): List<License1Bean> {
    val list: MutableList<License1Bean> = ArrayList()
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
    list += License1Bean(
        OpenBrowserProcessor.route.buildRouteUri {
            appendQueryParameter("url", "https://github.com/re-ovo/iwara4a")
        }.toString(),
        "Iwara4A",
        "Apache-2.0 License"
    )
    return list
}
