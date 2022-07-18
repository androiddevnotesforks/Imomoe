package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.plus
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import com.skyd.imomoe.view.adapter.compose.proxy.AnimeCover8Proxy
import com.skyd.imomoe.view.component.compose.AnimeLazyVerticalGrid
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.BackIcon
import com.skyd.imomoe.view.component.compose.ImageTextPlaceholder
import com.skyd.imomoe.viewmodel.FavoriteViewModel
import com.skyd.imomoe.viewmodel.FavoriteUiState

class FavoriteActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentBase {
            FavoriteScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoriteScreen(viewModel: FavoriteViewModel = hiltViewModel()) {
    val context = LocalContext.current
    Scaffold(topBar = {
        AnimeTopBar(
            title = {
                Text(text = stringResource(R.string.my_favorite))
            },
            navigationIcon = {
                BackIcon(
                    onClick = { context.activity.finish() }
                )
            }
        )
    }) { padding ->
        val swipeRefreshState = rememberSwipeRefreshState(
            isRefreshing = viewModel.uiState.value is FavoriteUiState.Refreshing
        )
        val uiState = viewModel.uiState.collectAsState()
        when (val uiStateValue = uiState.value) {
            is FavoriteUiState.Error -> {
                ImageTextPlaceholder(
                    modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                    message = uiStateValue.message.ifBlank { stringResource(id = R.string.get_data_failed) }
                )
            }
            is FavoriteUiState.WithData -> {
                SwipeRefresh(
                    modifier = Modifier.padding(padding),
                    state = swipeRefreshState,
                    onRefresh = {
                        viewModel.getFavoriteData()
                    }
                ) {
                    val dataList = uiStateValue.dataList ?: return@SwipeRefresh
                    if (dataList.isEmpty()) {
                        ImageTextPlaceholder(
                            modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
                            message = stringResource(id = R.string.no_favorite)
                        )
                    } else {
                        FavoriteList(dataList = dataList)
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteList(dataList: List<Any>) {
    val adapter = remember {
        LazyGridAdapter(
            mutableListOf(AnimeCover8Proxy())
        )
    }
    AnimeLazyVerticalGrid(
        modifier = Modifier.fillMaxSize(),
        dataList = dataList,
        adapter = adapter,
        contentPadding = WindowInsets.navigationBars.asPaddingValues() +
                PaddingValues(vertical = 6.dp)
    )
}