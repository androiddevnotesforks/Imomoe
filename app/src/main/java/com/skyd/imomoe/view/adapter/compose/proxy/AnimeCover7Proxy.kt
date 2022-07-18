package com.skyd.imomoe.view.adapter.compose.proxy

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover7Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.route.processor.EpisodeDownloadProcessor
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter

class AnimeCover7Proxy(
    private val onMenuItemClickListener: ((
        data: AnimeCover7Bean,
    ) -> Unit)? = null
) : LazyGridAdapter.Proxy<AnimeCover7Bean>() {
    @Composable
    override fun draw(modifier: Modifier, index: Int, data: AnimeCover7Bean) {
        AnimeCover7Item(data = data, onMenuItemClickListener = onMenuItemClickListener)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimeCover7Item(
    data: AnimeCover7Bean,
    onMenuItemClickListener: ((
        data: AnimeCover7Bean,
    ) -> Unit)? = null
) {
    val activity = LocalContext.current.activity
    var menuExpanded by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {
                    data.route.route(activity)
                },
                onLongClick = {
                    menuExpanded = true
                }
            )
            .padding(horizontal = 16.dp, vertical = 9.dp)
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = data.title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            modifier = Modifier
                .padding(start = 10.dp)
                .widthIn(min = 45.dp),
            text = data.size.orEmpty(),
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        if (data.route.startsWith(EpisodeDownloadProcessor.route, ignoreCase = true)) {
            Text(
                modifier = Modifier.padding(start = 10.dp),
                text = data.episodeCount.orEmpty(),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )
        }
        if (data.pathType == 1) {
            Text(
                modifier = Modifier
                    .padding(start = 10.dp),
                text = stringResource(id = R.string.old_path),
                textAlign = TextAlign.End,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = menuExpanded,
            onDismissRequest = { menuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = stringResource(id = R.string.anime_download_activity_item_menu_delete)) },
                onClick = {
                    menuExpanded = false
                    onMenuItemClickListener?.invoke(data)
                },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = null
                    )
                }
            )
        }
    }
}
