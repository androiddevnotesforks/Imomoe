package com.skyd.imomoe.view.component.compose

import androidx.annotation.DrawableRes
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import com.skyd.imomoe.R

@Composable
fun MessageDialog(
    title: String = stringResource(id = R.string.warning),
    message: String,
    @DrawableRes icon: Int = 0,
    properties: DialogProperties = DialogProperties(),
    negativeText: String = stringResource(id = R.string.cancel),
    positiveText: String = stringResource(id = R.string.ok),
    onDismissRequest: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onPositive: (() -> Unit)? = null,
) {
    MessageDialog(
        title = title,
        message = message,
        icon = if (icon == 0) null else painterResource(id = icon),
        properties = properties,
        negativeText = negativeText,
        positiveText = positiveText,
        onDismissRequest = onDismissRequest,
        onNegative = onNegative,
        onPositive = onPositive
    )
}

@Composable
fun MessageDialog(
    title: String = stringResource(id = R.string.warning),
    message: String,
    icon: ImageVector? = null,
    properties: DialogProperties = DialogProperties(),
    negativeText: String = stringResource(id = R.string.cancel),
    positiveText: String = stringResource(id = R.string.ok),
    onDismissRequest: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onPositive: (() -> Unit)? = null,
) {
    MessageDialog(
        title = title,
        message = message,
        icon = if (icon == null) null else rememberVectorPainter(icon),
        properties = properties,
        negativeText = negativeText,
        positiveText = positiveText,
        onDismissRequest = onDismissRequest,
        onNegative = onNegative,
        onPositive = onPositive
    )
}

@Composable
fun MessageDialog(
    title: String = stringResource(id = R.string.warning),
    message: String,
    icon: Painter? = null,
    properties: DialogProperties = DialogProperties(),
    negativeText: String = stringResource(id = R.string.cancel),
    positiveText: String = stringResource(id = R.string.ok),
    onDismissRequest: (() -> Unit)? = null,
    onNegative: (() -> Unit)? = null,
    onPositive: (() -> Unit)? = null,
) {
    val dismissButton: @Composable (() -> Unit) = {
        TextButton(
            onClick = {
                onNegative?.invoke()
            }
        ) {
            Text(text = negativeText)
        }
    }
    val iconLambda: @Composable (() -> Unit)? = if (icon != null) {
        { Icon(painter = icon, contentDescription = null) }
    } else null
    AlertDialog(
        icon = if (icon != null) iconLambda else null,
        title = {
            Text(text = title)
        },
        text = {
            Text(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                text = message
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onPositive?.invoke()
                }
            ) {
                Text(text = positiveText)
            }
        },
        dismissButton = if (onNegative == null) null else dismissButton,
        onDismissRequest = {
            onDismissRequest?.invoke()
        },
        properties = properties
    )
}