package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivitySettingContainerBinding
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.plus
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.AnimeTopBarStyle
import com.skyd.imomoe.view.component.compose.BackIcon
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentBase {
            SettingScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingScreen() {
    val context = LocalContext.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        decayAnimationSpec = rememberSplineBasedDecay(),
        state = rememberTopAppBarScrollState()
    )
    Scaffold(
        topBar = {
            AnimeTopBar(
                style = AnimeTopBarStyle.Large,
                title = {
                    Text(text = stringResource(R.string.setting))
                },
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    BackIcon(onClick = { context.activity.finish() })
                }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = it + WindowInsets.navigationBars.asPaddingValues()
        ) {
            item {
                AndroidViewBinding(
                    factory = ActivitySettingContainerBinding::inflate
                )
            }
        }
    }
}
