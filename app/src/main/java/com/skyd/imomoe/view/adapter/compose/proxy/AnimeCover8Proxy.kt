package com.skyd.imomoe.view.adapter.compose.proxy

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover8Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.util.coil.AnimeAsyncImage
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter

class AnimeCover8Proxy : LazyGridAdapter.Proxy<AnimeCover8Bean>() {
    @Composable
    override fun draw(modifier: Modifier, index: Int, data: AnimeCover8Bean) {
        AnimeCover8Item(modifier = modifier, data = data)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AnimeCover8Item(
    modifier: Modifier = Modifier,
    data: AnimeCover8Bean,
) {
    Card(
        modifier = modifier
            .padding(top = 7.dp, bottom = 5.dp)
            .aspectRatio(7 / 10f)
    ) {
        val activity = LocalContext.current.activity
        Box(
            modifier = Modifier.combinedClickable(
                onClick = {
                    val lastEpisodeUrl = data.lastEpisodeUrl
                    if (lastEpisodeUrl != null) {
                        lastEpisodeUrl.route(activity)
                    } else {
                        data.animeUrl.route(activity)
                    }
                },
                onLongClick = {
                    data.animeUrl.route(activity)
                }
            )
        ) {
            AnimeAsyncImage(
                modifier = Modifier.fillMaxSize(),
                url = data.cover.url,
                referer = data.cover.referer,
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            0f to Color.Transparent,
                            1f to Color(0x54000000)
                        )
                    )
                    .padding(horizontal = 7.dp)
                    .padding(top = 20.dp, bottom = 7.dp)
            ) {
                Text(
                    modifier = Modifier.padding(top = 5.dp),
                    text = data.animeTitle,
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
                Text(
                    modifier = Modifier.padding(top = 2.dp),
                    text = data.lastEpisode?.let {
                        stringResource(id = R.string.already_seen_episode_x, it)
                    } ?: stringResource(id = R.string.have_not_watched_this_anime),
                    maxLines = 1,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White
                )
            }
        }
    }
}
