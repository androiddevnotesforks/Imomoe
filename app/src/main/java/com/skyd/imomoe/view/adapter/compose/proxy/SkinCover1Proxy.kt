package com.skyd.imomoe.view.adapter.compose.proxy

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SkinCover1Bean
import com.skyd.imomoe.ext.theme.appThemeRes
import com.skyd.imomoe.util.coil.AnimeAsyncImage
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter

class SkinCover1Proxy : LazyGridAdapter.Proxy<SkinCover1Bean>() {
    @Composable
    override fun draw(modifier: Modifier, index: Int, data: SkinCover1Bean) {
        SkinCover1Item(modifier = modifier, data = data)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SkinCover1Item(
    modifier: Modifier = Modifier,
    data: SkinCover1Bean,
) {
    ElevatedCard(
        modifier = modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .aspectRatio(ratio = 2 / 1.4f)
    ) {
        Box(
            modifier = Modifier.clickable {
                if (data.using) return@clickable
                // 设置appThemeRes后，所有Activity都会重启
                appThemeRes = data.themeRes
            }
        ) {
            val cover = data.cover
            if (cover is Int) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = ColorPainter(Color(cover)),
                    contentDescription = null
                )
            } else if (cover is String) {
                AnimeAsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    url = cover,
                    contentDescription = null
                )
            }
            if (data.using) {
                Image(
                    modifier = Modifier
                        .padding(top = 6.dp, end = 6.dp)
                        .size(16.dp)
                        .align(Alignment.TopEnd),
                    painter = painterResource(id = R.drawable.ic_right_32),
                    colorFilter = ColorFilter.tint(color = Color.White),
                    contentDescription = null
                )
            }
            Text(
                modifier = Modifier
                    .padding(9.dp)
                    .align(Alignment.BottomEnd),
                text = data.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}
