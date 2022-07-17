package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import com.skyd.imomoe.view.adapter.compose.proxy.AnimeCover9Proxy
import com.skyd.imomoe.view.component.compose.*
import com.skyd.imomoe.viewmodel.HistoryUiState
import com.skyd.imomoe.viewmodel.HistoryViewModel

class HistoryActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentBase {
            HistoryScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(viewModel: HistoryViewModel = hiltViewModel()) {
    val context = LocalContext.current
    var showDeleteAllWarningDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(topBar = {
        AnimeTopBar(
            style = AnimeTopBarStyle.Small,
            title = {
                Text(text = stringResource(R.string.watch_history))
            },
            navigationIcon = {
                BackIcon(
                    onClick = { context.activity.finish() }
                )
            },
            actions = {
                TopBarIcon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = stringResource(id = R.string.history_activity_menu_delete_all),
                    onClick = {
                        showDeleteAllWarningDialog = true
                    }
                )
            }
        )
    }) { padding ->
        val swipeRefreshState = rememberSwipeRefreshState(
            isRefreshing = viewModel.uiState.value is HistoryUiState.Refreshing
        )
        val uiState = viewModel.uiState.collectAsState()
        when (val uiStateValue = uiState.value) {
            is HistoryUiState.Error -> {
                ImageTextPlaceholder(
                    modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                    message = uiStateValue.message.ifBlank { stringResource(id = R.string.get_data_failed) }
                )
            }
            is HistoryUiState.WithData -> {
                SwipeRefresh(
                    modifier = Modifier.padding(padding),
                    state = swipeRefreshState,
                    onRefresh = {
                        viewModel.getHistoryList()
                    }
                ) {
                    val dataList = uiStateValue.dataList ?: return@SwipeRefresh
                    if (dataList.isEmpty()) {
                        ImageTextPlaceholder(
                            modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                            message = stringResource(id = R.string.no_history)
                        )
                    } else {
                        HistoryList(dataList)
                    }
                }
            }
        }

        if (showDeleteAllWarningDialog) {
            MessageDialog(
                icon = Icons.Rounded.Warning,
                message = stringResource(id = R.string.confirm_delete_all_watch_history),
                positiveText = stringResource(R.string.delete),
                onPositive = {
                    showDeleteAllWarningDialog = false
                    viewModel.deleteAllHistory()
                },
                onNegative = {
                    showDeleteAllWarningDialog = false
                },
                onDismissRequest = {
                    showDeleteAllWarningDialog = false
                }
            )
        }
    }
}

@Composable
private fun HistoryList(dataList: List<Any>, viewModel: HistoryViewModel = hiltViewModel()) {
    val adapter = LazyGridAdapter(
        mutableListOf(
            AnimeCover9Proxy(onDeleteButtonClickListener = { _, data ->
                viewModel.deleteHistory(data)
            })
        )
    )
    AnimeLazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        dataList = dataList,
        adapter = adapter
    )
}