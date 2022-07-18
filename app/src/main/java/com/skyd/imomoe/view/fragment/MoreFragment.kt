package com.skyd.imomoe.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.More1Bean
import com.skyd.imomoe.ext.plus
import com.skyd.imomoe.ext.screenIsLand
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.processor.ConfigDataSourceActivityProcessor
import com.skyd.imomoe.route.processor.JumpByUrlProcessor
import com.skyd.imomoe.route.processor.StartActivityProcessor
import com.skyd.imomoe.view.activity.*
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import com.skyd.imomoe.view.adapter.compose.proxy.More1Proxy
import com.skyd.imomoe.view.component.compose.AnimeLazyVerticalGrid
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.TopBarIcon

class MoreFragment : BaseComposeFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return setContentBase {
            MoreScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MoreScreen() {
    val context = LocalContext.current
    Scaffold(
        topBar = {
            AnimeTopBar(
                title = {
                    Text(text = stringResource(R.string.more))
                },
                navigationIcon = {
                    TopBarIcon(
                        painter = painterResource(id = R.drawable.ic_beans_24),
                        contentDescription = null
                    )
                },
                contentPadding = {
                    if (LocalContext.current.screenIsLand) {
                        PaddingValues(
                            end = WindowInsets.navigationBars.asPaddingValues()
                                .calculateEndPadding(LocalLayoutDirection.current)
                        )
                    } else {
                        PaddingValues()
                    } + WindowInsets.statusBars.asPaddingValues()
                }
            )
        }
    ) {
        val adapter = remember { LazyGridAdapter(mutableListOf(More1Proxy())) }
        val dataList = remember { initData(context) }
        AnimeLazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            dataList = dataList,
            adapter = adapter,
            contentPadding = it + PaddingValues(vertical = 6.dp) + PaddingValues(
                end = WindowInsets.navigationBars.asPaddingValues()
                    .calculateEndPadding(LocalLayoutDirection.current)
            )
        )
    }
}

private fun initData(context: Context): List<More1Bean> {
    val list: MutableList<More1Bean> = ArrayList()
    list += More1Bean(
        StartActivityProcessor.route.buildRouteUri {
            appendQueryParameter("cls", HistoryActivity::class.qualifiedName)
        }.toString(),
        context.getString(R.string.watch_history),
        R.drawable.ic_history_24
    )
    list += More1Bean(
        JumpByUrlProcessor.route,
        context.getString(R.string.skip_by_website),
        R.drawable.ic_insert_link_24
    )
    list += More1Bean(
        StartActivityProcessor.route.buildRouteUri {
            appendQueryParameter("cls", SkinActivity::class.qualifiedName)
        }.toString(),
        context.getString(R.string.skin_center),
        R.drawable.ic_skin_32
    )
    list += More1Bean(
        StartActivityProcessor.route.buildRouteUri {
            appendQueryParameter("cls", DownloadManagerActivity::class.qualifiedName)
        }.toString(),
        context.getString(R.string.download_manager),
        R.drawable.ic_cloud_download_24
    )
    list += More1Bean(
        ConfigDataSourceActivityProcessor.route.buildRouteUri {
            appendQueryParameter("selectPageIndex", "1")
        }.toString(),
        context.getString(R.string.data_source_market),
        R.drawable.ic_plugin_24
    )
    list += More1Bean(
        StartActivityProcessor.route.buildRouteUri {
            appendQueryParameter("cls", SettingActivity::class.qualifiedName)
        }.toString(),
        context.getString(R.string.setting),
        R.drawable.ic_settings_24
    )
    list += More1Bean(
        StartActivityProcessor.route.buildRouteUri {
            appendQueryParameter("cls", BackupRestoreActivity::class.qualifiedName)
        }.toString(),
        context.getString(R.string.backup_and_restore),
        R.drawable.ic_cloud_done_24
    )
    list += More1Bean(
        StartActivityProcessor.route.buildRouteUri {
            appendQueryParameter("cls", AboutActivity::class.qualifiedName)
        }.toString(),
        context.getString(R.string.about),
        R.drawable.ic_info_24
    )
    return list
}