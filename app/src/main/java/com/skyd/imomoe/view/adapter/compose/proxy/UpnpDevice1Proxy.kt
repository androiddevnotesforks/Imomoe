package com.skyd.imomoe.view.adapter.compose.proxy

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import org.fourthline.cling.model.meta.Device

class UpnpDevice1Proxy(
    private val onClickListener: ((
        index: Int,
        data: Device<*, *, *>,
    ) -> Unit)? = null
) : LazyGridAdapter.Proxy<Device<*, *, *>>() {
    @Composable
    override fun draw(index: Int, data: Device<*, *, *>) {
        UpnpDevice1Item(
            index = index,
            data = data,
            onClickListener = onClickListener
        )
    }
}

@Composable
fun UpnpDevice1Item(
    index: Int,
    data: Device<*, *, *>,
    onClickListener: ((
        index: Int,
        data: Device<*, *, *>,
    ) -> Unit)? = null
) {
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .run {
                if (onClickListener != null) clickable { onClickListener(index, data) }
                else this
            }
            .padding(vertical = 10.dp, horizontal = 16.dp),
        text = data.details?.friendlyName.orEmpty(),
        style = MaterialTheme.typography.titleMedium,
        maxLines = 1,
        color = MaterialTheme.colorScheme.primary
    )
}
