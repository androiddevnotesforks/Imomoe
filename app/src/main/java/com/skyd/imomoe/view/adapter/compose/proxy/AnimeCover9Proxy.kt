package com.skyd.imomoe.view.adapter.compose.proxy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover9Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.coil.AnimeAsyncImage
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter

class AnimeCover9Proxy(
    private val onDeleteButtonClickListener: ((
        index: Int,
        data: AnimeCover9Bean,
    ) -> Unit)? = null
) : LazyGridAdapter.Proxy<AnimeCover9Bean>() {
    @Composable
    override fun draw(modifier: Modifier, index: Int, data: AnimeCover9Bean) {
        AnimeCover9Item(
            modifier = modifier,
            index = index,
            data = data,
            onDeleteButtonClickListener = onDeleteButtonClickListener
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimeCover9Item(
    modifier: Modifier = Modifier,
    index: Int,
    data: AnimeCover9Bean,
    onDeleteButtonClickListener: ((
        index: Int,
        data: AnimeCover9Bean,
    ) -> Unit)? = null
) {
    val activity = LocalContext.current.activity
    Card(
        modifier = modifier
            .padding(vertical = 7.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .clickable {
                    val lastEpisodeUrl = data.lastEpisodeUrl
                    if (lastEpisodeUrl != null) {
                        lastEpisodeUrl.route(activity)
                    } else {
                        data.animeUrl.route(activity)
                    }
                }
        ) {
            AnimeAsyncImage(
                modifier = Modifier
                    .width(120.dp)
                    .fillMaxHeight(),
                url = data.cover.url,
                referer = data.cover.referer,
                contentScale = ContentScale.Crop,
            )
            Column(modifier = Modifier.padding(start = 10.dp)) {
                Row {
                    Text(
                        modifier = Modifier
                            .padding(top = 10.dp, end = 16.dp)
                            .weight(1f),
                        text = data.animeTitle,
                        maxLines = 3,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Row(
                        modifier = Modifier
                            .padding(top = 13.dp, end = 12.dp)
                            .clickable {
                                data.animeUrl.route(activity)
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.detail_page),
                            maxLines = 3,
                            style = MaterialTheme.typography.labelMedium
                        )
                        Icon(
                            modifier = Modifier.size(12.dp),
                            painter = painterResource(id = R.drawable.ic_arrow_forward_ios_12),
                            contentDescription = null
                        )
                    }
                }
                Row {
                    Column(modifier = Modifier.weight(1f)) {
                        OutlinedCard(
                            modifier = Modifier.padding(top = 7.dp, end = 16.dp),
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                text = data.lastEpisode.orEmpty(),
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            modifier = Modifier
                                .weight(1f)
                                .padding(top = 7.dp, bottom = 10.dp),
                            text = Util.time2Now(data.time),
                            maxLines = 3,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(
                        modifier = Modifier.align(Alignment.Bottom),
                        onClick = {
                            onDeleteButtonClickListener?.invoke(index, data)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Delete,
                            contentDescription = stringResource(id = R.string.delete)
                        )
                    }
                }
            }
        }
    }
}
