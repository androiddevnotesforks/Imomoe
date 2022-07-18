package com.skyd.imomoe.view.adapter.compose.proxy

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.skyd.imomoe.bean.More1Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter

class More1Proxy : LazyGridAdapter.Proxy<More1Bean>() {
    @Composable
    override fun draw(modifier: Modifier, index: Int, data: More1Bean) {
        More1Item(modifier = modifier, data = data)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun More1Item(
    modifier: Modifier = Modifier,
    data: More1Bean,
) {
    ElevatedCard(modifier = modifier.padding(vertical = 6.dp)) {
        CardContent(data)
    }
}

@Composable
private fun CardContent(data: More1Bean) {
    val activity = LocalContext.current.activity
    var padding by remember { mutableStateOf(0.dp) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .clickable { data.route.route(activity) }
            .padding(horizontal = 20.dp, vertical = 17.dp + padding / 2),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier = Modifier.size(40.dp),
            painter = painterResource(id = data.image),
            contentDescription = null
        )
        val density = LocalDensity.current.density
        Text(
            modifier = Modifier.padding(top = 10.dp),
            text = data.title,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium,
            maxLines = 2,
            onTextLayout = {
                val lineCount = it.lineCount
                val height = (it.size.height / density).dp
                padding = if (lineCount > 1) 0.dp else height
            }
        )
    }
}