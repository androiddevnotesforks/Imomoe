package com.skyd.imomoe.view.component.compose

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    val dismissButton: @Composable (() -> Unit) = {
        TextButton(
            onClick = {
                onNegative?.invoke()
            }
        ) {
            Text(text = negativeText)
        }
    }
    val iconLambda: @Composable (() -> Unit) = {
        Icon(painter = painterResource(id = icon), contentDescription = null)
    }
    AlertDialog(
        icon = if (icon != 0) iconLambda else null,
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