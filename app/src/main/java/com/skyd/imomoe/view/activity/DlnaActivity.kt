package com.skyd.imomoe.view.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.util.dlna.dmc.DLNACastManager
import com.skyd.imomoe.util.dlna.dmc.OnDeviceRegistryListenerDsl
import com.skyd.imomoe.util.dlna.dmc.registerDeviceListener
import com.skyd.imomoe.util.dlna.dmc.unregisterListener
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import com.skyd.imomoe.view.adapter.compose.proxy.UpnpDevice1Proxy
import com.skyd.imomoe.view.component.compose.AnimeLazyVerticalGrid
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.AnimeTopBarStyle
import com.skyd.imomoe.view.component.compose.BackIcon
import com.skyd.imomoe.viewmodel.DlnaUiState
import com.skyd.imomoe.viewmodel.DlnaViewModel

class DlnaActivity : BaseComposeActivity() {
    private val viewModel: DlnaViewModel by viewModels()
    private val deviceRegistryListener: OnDeviceRegistryListenerDsl.() -> Unit = {
        onDeviceRemoved { device ->
            viewModel.removeDevice(device)
        }

        onDeviceAdded { device ->
            viewModel.addDevice(device)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentBase {
            DlnaScreen()
        }

        viewModel.initData(
            url = intent.getStringExtra("url").orEmpty(),
            title = intent.getStringExtra("title").orEmpty()
        )

        viewModel.uiState.collectWithLifecycle(this) {
            if (it is DlnaUiState.Initialized) {
                DLNACastManager.instance.registerDeviceListener(deviceRegistryListener)
                DLNACastManager.instance.search(DLNACastManager.DEVICE_TYPE_DMR)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        DLNACastManager.instance.bindCastService(this)
    }

    override fun onStop() {
        DLNACastManager.instance.bindCastService(this)
        super.onStop()
    }

    override fun onDestroy() {
        DLNACastManager.instance.unregisterListener(deviceRegistryListener)
        super.onDestroy()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DlnaScreen() {
    val context = LocalContext.current
    Scaffold(topBar = {
        AnimeTopBar(
            style = AnimeTopBarStyle.Small,
            title = {
                Text(text = stringResource(R.string.play_on_tv))
            },
            navigationIcon = {
                BackIcon(
                    onClick = { context.activity.finish() }
                )
            }
        )
    }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    text = stringResource(id = R.string.device_list),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                )
                CircularProgressIndicator(modifier = Modifier.size(21.dp))
            }
            DeviceList()
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                text = stringResource(id = R.string.dlna_step),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(WindowInsets.navigationBars.asPaddingValues()),
                text = "1. 确保电视和手机在同一WiFi下，打开支持投屏的电视。\n2. 点击上方搜索到的设备进行投屏。\n3. 注意，部分未知格式的视频可能无法投屏。",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun DeviceList(viewModel: DlnaViewModel = hiltViewModel()) {
    val activity = LocalContext.current.activity
    val uiState by viewModel.uiState.collectAsState()
    val adapter: LazyGridAdapter = remember {
        LazyGridAdapter(
            mutableListOf(
                UpnpDevice1Proxy(onClickListener = { _, data ->
                    val key = System.currentTimeMillis().toString()
                    DlnaControlActivity.deviceHashMap[key] = data
                    activity.startActivity(
                        Intent(activity, DlnaControlActivity::class.java)
                            .putExtra("url", uiState.url)
                            .putExtra("title", uiState.title)
                            .putExtra("deviceKey", key)
                    )
                })
            )
        )
    }
    AnimeLazyVerticalGrid(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(),
        dataList = uiState.readOrNull().orEmpty(),
        adapter = adapter
    )
}
