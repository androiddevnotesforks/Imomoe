package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidViewBinding
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityBackupRestoreContainerBinding
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.plus
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.AnimeTopBarStyle
import com.skyd.imomoe.view.component.compose.BackIcon

class BackupRestoreActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentBase {
            WebDavScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WebDavScreen() {
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
                    Text(text = stringResource(R.string.backup_and_restore))
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
                    factory = ActivityBackupRestoreContainerBinding::inflate
                )
            }
        }
    }
}
