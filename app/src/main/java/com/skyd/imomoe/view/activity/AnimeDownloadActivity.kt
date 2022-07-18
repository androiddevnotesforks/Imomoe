package com.skyd.imomoe.view.activity

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover7Bean
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import com.skyd.imomoe.view.adapter.compose.proxy.AnimeCover7Proxy
import com.skyd.imomoe.view.component.compose.*
import com.skyd.imomoe.viewmodel.AnimeDownloadUiState
import com.skyd.imomoe.viewmodel.AnimeDownloadViewModel
import com.skyd.imomoe.viewmodel.DeleteUiState

class AnimeDownloadActivity : BaseComposeActivity() {
    private val viewModel: AnimeDownloadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentBase {
            AnimeDownloadScreen()
        }

        viewModel.mode = intent.getIntExtra("mode", 0)
        viewModel.actionBarTitle =
            intent.getStringExtra("actionBarTitle") ?: getString(R.string.download_anime)
        viewModel.directoryName = intent.getStringExtra("directoryName").orEmpty()
        viewModel.path = intent.getIntExtra("path", 0)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeDownloadScreen(viewModel: AnimeDownloadViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        initData(activity = context.activity, viewModel = viewModel)
    }
    Scaffold(
        topBar = {
            AnimeTopBar(
                title = {
                    Text(text = viewModel.actionBarTitle)
                },
                navigationIcon = {
                    BackIcon(
                        onClick = { context.activity.finish() }
                    )
                },
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) {
                Snackbar(
                    modifier = Modifier
                        .padding(12.dp)
                        .padding(WindowInsets.navigationBars.asPaddingValues())
                ) {
                    Text(it.visuals.message)
                }
            }
        }
    ) { padding ->
        val uiState by viewModel.animeCoverList.collectAsState()
        when (uiState) {
            is AnimeDownloadUiState.WithData -> {
                val dataList = uiState.readOrNull().orEmpty()
                if (dataList.isEmpty()) {
                    ImageTextPlaceholder(
                        modifier = Modifier.padding(padding + WindowInsets.navigationBars.asPaddingValues()),
                        message = stringResource(id = R.string.no_download_video)
                    )
                } else {
                    AnimeDownloadList(dataList = dataList, modifier = Modifier.padding(padding))
                }
            }
            is AnimeDownloadUiState.Refreshing, is AnimeDownloadUiState.None -> {
                ProgressTextPlaceholder(
                    modifier = Modifier.padding(padding + WindowInsets.navigationBars.asPaddingValues()),
                    message = stringResource(id = R.string.read_download_data_file)
                )
            }
            else -> {}
        }

        LaunchedEffect(Unit) {
            viewModel.delete.collect { deleteUiState ->
                if (deleteUiState !is DeleteUiState.None) {
                    showWaitingDialog = false
                    initData(activity = context.activity, force = true, viewModel = viewModel)
                    snackbarHostState.showSnackbar(
                        message = context.getString(
                            if (deleteUiState is DeleteUiState.Success) {
                                R.string.anime_download_activity_delete_success
                            } else {
                                R.string.anime_download_activity_delete_failed
                            },
                            deleteUiState.getMessageData()
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeDownloadList(
    dataList: List<Any>,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
) {
    val adapter = remember {
        LazyGridAdapter(
            mutableListOf(
                AnimeCover7Proxy(onMenuItemClickListener = { data ->
                    showMessageDialog = true
                    messageDialogData = data
                })
            )
        )
    }
    AnimeLazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        dataList = dataList,
        adapter = adapter,
        contentPadding = contentPadding + WindowInsets.navigationBars.asPaddingValues()
    )
    if (showMessageDialog) {
        DeleteDialog()
    }
    if (showWaitingDialog) {
        WaitingDeleteDialog()
    }
}

private var showMessageDialog by mutableStateOf(false)
private var messageDialogData by mutableStateOf<AnimeCover7Bean?>(null)
private var showWaitingDialog by mutableStateOf(false)

@Composable
private fun DeleteDialog(viewModel: AnimeDownloadViewModel = hiltViewModel()) {
    val data = messageDialogData ?: return
    MessageDialog(
        message = stringResource(
            id = R.string.anime_download_activity_delete_message,
            data.title
        ),
        icon = Icons.Rounded.Delete,
        onNegative = { showMessageDialog = false },
        onPositive = {
            showMessageDialog = false
            showWaitingDialog = true
            viewModel.delete(data.path)
        },
        onDismissRequest = {
            showMessageDialog = false
        }
    )
}

@Composable
private fun WaitingDeleteDialog(viewModel: AnimeDownloadViewModel = hiltViewModel()) {
    WaitingDialog(
        onDismissRequest = { showWaitingDialog = false },
        message = stringResource(id = R.string.anime_download_activity_deleting),
        onNegative = {
            viewModel.cancelDelete()
            showWaitingDialog = false
        },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    )
}

private fun initData(
    activity: Activity,
    force: Boolean = false,
    viewModel: AnimeDownloadViewModel
) {
    activity.requestManageExternalStorage {
        onGranted {
            if (viewModel.mode == 0) {
                if (viewModel.animeCoverList.value is AnimeDownloadUiState.None || force) {
                    viewModel.getAnimeCover()
                }
            } else if (viewModel.mode == 1) {
                if (viewModel.animeCoverList.value is AnimeDownloadUiState.None || force) {
                    viewModel.getAnimeCoverEpisode()
                }
            }
        }
        onDenied {
            activity.getString(R.string.no_storage_permission_can_not_olay_local_video)
                .showToast(Toast.LENGTH_LONG)
            activity.finish()
        }
    }
}