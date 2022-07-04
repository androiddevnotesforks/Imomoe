package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.showMessageDialog
import com.skyd.imomoe.ext.toHtml
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.route.processor.StartActivityProcessor
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.Util.openBrowser
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.AnimeTopBarStyle
import com.skyd.imomoe.view.component.compose.BackIcon
import com.skyd.imomoe.view.component.compose.MessageDialog
import java.net.URL
import java.util.*

class AboutActivity : BaseComponentActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentBase {
            AboutScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun AboutScreen() {
    val context = LocalContext.current
    val isLand = calculateWindowSizeClass(LocalContext.current.activity).run {
        widthSizeClass != WindowWidthSizeClass.Compact
    }
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec = rememberSplineBasedDecay(),
    )
    Scaffold(
        topBar = {
            AnimeTopBar(
                style = if (isLand) AnimeTopBarStyle.Small else AnimeTopBarStyle.Large,
                title = {
                    Text(text = stringResource(R.string.about))
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    BackIcon(onClick = { context.activity.finish() })
                },
                actions = {
                    IconButton(onClick = {
                        val activity = context.activity
                        activity.showMessageDialog(
                            title = activity.getString(R.string.attention),
                            message = "本软件免费开源，严禁商用，支持Android 5.0+！仅在GitHub仓库长期发布！\n不介意的话可以给我的GitHub仓库点个Star",
                            positiveText = "去点Star",
                            negativeText = activity.getString(R.string.cancel),
                            onPositive = { _, _ -> openBrowser(Const.Common.GITHUB_URL) },
                            onNegative = { dialog, _ -> dialog.dismiss() }
                        )
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.Info,
                            contentDescription = stringResource(id = R.string.about_activity_menu_info)
                        )
                    }
                }
            )
        }) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .navigationBarsPadding(),
        ) {
            if (isLand) {
                item {
                    Row(modifier = Modifier.wrapContentSize()) {
                        AppIconArea(modifier = Modifier.weight(1f))
                        AboutScreenList(modifier = Modifier.weight(1f))
                    }
                }
            } else {
                item {
                    AppIconArea()
                }
                item {
                    AboutScreenList()
                }
            }
        }
    }
}

/**
 * 显示图标、应用名和版本的上半部分
 */
@Composable
fun AppIconArea(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 50.dp, bottom = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(100.dp)
                .height(100.dp)
        ) {
            Image(
                modifier = Modifier.size(100.dp),
                painter = painterResource(id = R.drawable.ic_akarin),
                contentDescription = null
            )
            val c: Calendar = Calendar.getInstance()
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
            if (month == Calendar.DECEMBER && (day > 21 || day < 29)) {     // 圣诞节彩蛋
                Image(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.TopEnd),
                    painter = painterResource(id = R.drawable.ic_christmas_hat),
                    contentDescription = null
                )
            }
        }
        Text(
            modifier = Modifier.padding(top = 15.dp),
            text = stringResource(id = R.string.app_name),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        Text(
            modifier = Modifier.padding(
                top = 2.dp, start = 16.dp, end = 16.dp
            ),
            textAlign = TextAlign.Center,
            text = stringResource(R.string.app_version_name, Util.getAppVersionName()) + "\n" +
                    stringResource(R.string.app_version_code, Util.getAppVersionCode()) + "\n" +
                    stringResource(
                        R.string.data_source_interface_version,
                        com.skyd.imomoe.model.interfaces.interfaceVersion
                    ),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * 下方的列表部分
 */
@Composable
fun AboutScreenList(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    var openDataSourceWebsiteDialog by remember { mutableStateOf(false) }
    var openDataSourceInfoDialog by remember { mutableStateOf(false) }
    var openUserNoticeDialog by remember { mutableStateOf(false) }
    var openThanksDialog by remember { mutableStateOf(false) }
    var openTestDeviceDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        AboutScreenListItem(
            title = stringResource(id = R.string.data_source_url),
            showIcon = true,
            onIconClick = {
                openDataSourceInfoDialog = true
            },
            onClick = {
                openDataSourceWebsiteDialog = true
            }
        )
        AboutScreenListItem(
            title = stringResource(id = R.string.github),
            onClick = {
                openBrowser(Const.Common.GITHUB_URL)
            }
        )
        AboutScreenListItem(
            title = stringResource(id = R.string.about_activity_data_source_repo),
            onClick = {
                openBrowser(Const.Common.GITHUB_DATA_SOURCE_URL)
            }
        )
        AboutScreenListItem(
            title = stringResource(id = R.string.open_source_licenses),
            onClick = {
                StartActivityProcessor.route.buildRouteUri {
                    appendQueryParameter("cls", LicenseActivity::class.qualifiedName)
                }.route(context.activity)
            }
        )
        AboutScreenListItem(
            title = stringResource(id = R.string.user_notice),
            onClick = {
                openUserNoticeDialog = true
            }
        )
        AboutScreenListItem(
            title = stringResource(id = R.string.test_device),
            onClick = {
                openTestDeviceDialog = true
            }
        )
        AboutScreenListItem(
            title = stringResource(id = R.string.about_activity_thanks),
            onClick = {
                openThanksDialog = true
            }
        )

        if (openDataSourceWebsiteDialog) {
            var warningString: String = stringResource(R.string.jump_to_data_source_website_warning)
            if (URL(Api.MAIN_URL).protocol == "http") {
                warningString = stringResource(R.string.jump_to_browser_http_warning) +
                        "\n" + warningString
            }
            MessageDialog(
                message = warningString,
                positiveText = stringResource(R.string.still_to_visit),
                onPositive = {
                    openBrowser(Api.MAIN_URL)
                    openDataSourceWebsiteDialog = false
                },
                onNegative = { openDataSourceWebsiteDialog = false },
                onDismissRequest = {
                    openDataSourceWebsiteDialog = false
                }
            )
        }

        if (openDataSourceInfoDialog) {
            MessageDialog(
                title = stringResource(id = R.string.data_source_info),
                message = (DataSourceManager.getConst()
                    ?: com.skyd.imomoe.model.impls.Const()).run {
                    "${
                        stringResource(
                            R.string.data_source_jar_version_name,
                            versionName().toString()
                        )
                    }\n${
                        stringResource(
                            R.string.data_source_jar_version_code,
                            versionCode().toString()
                        )
                    }\n${
                        stringResource(
                            R.string.data_source_interface_version,
                            DataSourceManager.customDataSourceInfo?.get("interfaceVersion")
                                .orEmpty()
                        )
                    }\n${about()}"
                },
                onPositive = { openDataSourceInfoDialog = false },
                onDismissRequest = {
                    openDataSourceInfoDialog = false
                }
            )
        }

        if (openUserNoticeDialog) {
            MessageDialog(
                title = stringResource(id = R.string.user_notice),
                message = Util.getUserNoticeContent().toHtml().toString(),
                properties = DialogProperties(
                    dismissOnBackPress = false,
                    dismissOnClickOutside = false
                ),
                onPositive = {
                    Util.setReadUserNoticeVersion(Const.Common.USER_NOTICE_VERSION)
                    openUserNoticeDialog = false
                },
                onDismissRequest = {
                    openUserNoticeDialog = false
                }
            )
        }

        if (openTestDeviceDialog) {
            MessageDialog(
                title = stringResource(id = R.string.test_device),
                message = "Physical Device: \nAndroid 10",
                onPositive = { openTestDeviceDialog = false },
                onDismissRequest = {
                    openTestDeviceDialog = false
                }
            )
        }

        if (openThanksDialog) {
            MessageDialog(
                title = stringResource(id = R.string.about_activity_thanks),
                message = "MaybeQHL提供弹幕服务器：\nhttps://github.com/MaybeQHL/my_danmu_pub",
                positiveText = stringResource(R.string.about_activity_open_danmaku_server_page),
                onPositive = {
                    openBrowser("https://github.com/MaybeQHL/my_danmu_pub")
                    openThanksDialog = false
                },
                onDismissRequest = {
                    openThanksDialog = false
                }
            )
        }
    }
}

/**
 * 下方列表部分的子项
 */
@Composable
fun AboutScreenListItem(
    title: String,
    showIcon: Boolean = false,
    onIconClick: () -> Unit = {},
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .clickable { onClick() }
            .padding(start = 25.dp, end = 16.dp)
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.titleMedium
        )
        if (showIcon) {
            IconButton(
                modifier = Modifier.fillMaxHeight(),
                onClick = { onIconClick() }) {
                Icon(imageVector = Icons.Rounded.Info, contentDescription = null)
            }
        }
        Icon(
            modifier = Modifier.size(30.dp),
            imageVector = Icons.Rounded.KeyboardArrowRight, contentDescription = null
        )
    }
}
