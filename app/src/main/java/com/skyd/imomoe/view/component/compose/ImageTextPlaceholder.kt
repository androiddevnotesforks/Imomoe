package com.skyd.imomoe.view.component.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.R

@Composable
fun ImageTextPlaceholder(
    modifier: Modifier = Modifier,
    painter: Painter = painterResource(id = R.drawable.ic_sentiment_very_dissatisfied_24),
    message: String,
    onClick: (() -> Unit)? = null
) {
    Centered(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .run { if (onClick != null) clickable(onClick = onClick) else this }
                .fillMaxWidth(fraction = 0.5f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                modifier = Modifier.fillMaxWidth(fraction = 0.6f),
                painter = painter,
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = message,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}