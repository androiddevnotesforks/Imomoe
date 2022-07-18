package com.skyd.imomoe.view.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SkinCover1Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.plus
import com.skyd.imomoe.ext.theme.appThemeRes
import com.skyd.imomoe.ext.theme.getAttrColor
import com.skyd.imomoe.view.adapter.compose.LazyGridAdapter
import com.skyd.imomoe.view.adapter.compose.proxy.SkinCover1Proxy
import com.skyd.imomoe.view.component.compose.AnimeLazyVerticalGrid
import com.skyd.imomoe.view.component.compose.AnimeTopBar
import com.skyd.imomoe.view.component.compose.BackIcon

class SkinActivity : BaseComposeActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentBase {
            SkinScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SkinScreen() {
    val context = LocalContext.current
    Scaffold(topBar = {
        AnimeTopBar(
            title = {
                Text(text = stringResource(R.string.skin_center))
            },
            navigationIcon = {
                BackIcon(
                    onClick = { context.activity.finish() }
                )
            }
        )
    }) { padding ->
        SkinList(modifier = Modifier.padding(padding))
    }
}

@Composable
private fun SkinList(modifier: Modifier) {
    val context = LocalContext.current
    val adapter = remember {
        LazyGridAdapter(
            mutableListOf(SkinCover1Proxy())
        )
    }
    val dataList = remember { initSkinData(context = context) }
    AnimeLazyVerticalGrid(
        modifier = modifier.fillMaxSize(),
        dataList = dataList,
        adapter = adapter,
        contentPadding = WindowInsets.navigationBars.asPaddingValues() +
                PaddingValues(vertical = 7.dp)
    )
}

private fun initSkinData(context: Context): List<SkinCover1Bean> {
    val list = mutableListOf<SkinCover1Bean>()
    list += SkinCover1Bean(
        "",
        ContextCompat.getColor(context, R.color.primary_pink),
        context.getString(R.string.theme_pink_title),
        appThemeRes == R.style.Theme_Anime_Pink,
        R.style.Theme_Anime_Pink
    )
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        list += SkinCover1Bean(
            "",
            context.getAttrColor(R.attr.colorPrimary),
            context.getString(R.string.theme_dynamic_title),
            appThemeRes == R.style.Theme_Anime_Dynamic,
            R.style.Theme_Anime_Dynamic
        )
    }
    list += SkinCover1Bean(
        "",
        ContextCompat.getColor(context, R.color.primary_blue),
        context.getString(R.string.theme_blue_title),
        appThemeRes == R.style.Theme_Anime_Blue,
        R.style.Theme_Anime_Blue
    )
    list += SkinCover1Bean(
        "",
        ContextCompat.getColor(context, R.color.primary_lemon),
        context.getString(R.string.theme_lemon_title),
        appThemeRes == R.style.Theme_Anime_Lemon,
        R.style.Theme_Anime_Lemon
    )
    list += SkinCover1Bean(
        "",
        ContextCompat.getColor(context, R.color.primary_purple),
        context.getString(R.string.theme_purple_title),
        appThemeRes == R.style.Theme_Anime_Purple,
        R.style.Theme_Anime_Purple
    )
    list += SkinCover1Bean(
        "",
        ContextCompat.getColor(context, R.color.primary_green),
        context.getString(R.string.theme_green_title),
        appThemeRes == R.style.Theme_Anime_Green,
        R.style.Theme_Anime_Green
    )
    return list
}