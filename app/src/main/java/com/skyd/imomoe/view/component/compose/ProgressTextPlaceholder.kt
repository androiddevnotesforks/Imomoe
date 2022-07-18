package com.skyd.imomoe.view.component.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProgressTextPlaceholder(
    modifier: Modifier = Modifier,
    message: String,
    onClick: (() -> Unit)? = null
) {
    Centered(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier.run { if (onClick != null) clickable(onClick = onClick) else this },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator()
            Text(
                modifier = Modifier.padding(top = 20.dp),
                text = message,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}