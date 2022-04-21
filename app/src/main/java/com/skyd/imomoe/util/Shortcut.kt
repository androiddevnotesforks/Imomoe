package com.skyd.imomoe.util

import android.app.Activity
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.graphics.drawable.Icon
import android.os.Build
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Const.ShortCuts.ACTION_EVERYDAY
import com.skyd.imomoe.config.Const.ShortCuts.ID_DOWNLOAD
import com.skyd.imomoe.config.Const.ShortCuts.ID_EVERYDAY
import com.skyd.imomoe.config.Const.ShortCuts.ID_FAVORITE
import com.skyd.imomoe.view.activity.AnimeDownloadActivity
import com.skyd.imomoe.view.activity.FavoriteActivity
import com.skyd.imomoe.view.activity.MainActivity

/**
 * 设置app图标快捷菜单
 */
fun Activity.registerShortcuts() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
        val mShortcutManager = getSystemService(ShortcutManager::class.java)
        val shortcutInfoList = listOf(
            ShortcutInfo.Builder(this, ID_FAVORITE)
                .setShortLabel(getString(R.string.shortcuts_favorite_short))
                .setLongLabel(getString(R.string.shortcuts_favorite_long))
                .setIcon(
                    Icon.createWithResource(this, R.drawable.layerlist_shortcuts_favorite_24)
                )
                .setIntent(
                    Intent(this, FavoriteActivity::class.java).setAction(Intent.ACTION_VIEW)
                )
                .build(),
            ShortcutInfo.Builder(this, ID_EVERYDAY)
                .setShortLabel(getString(R.string.shortcuts_everyday_short))
                .setLongLabel(getString(R.string.shortcuts_everyday_long))
                .setIcon(
                    Icon.createWithResource(this, R.drawable.layerlist_shortcuts_everyday_24)
                )
                .setIntent(
                    Intent(this, MainActivity::class.java).setAction(ACTION_EVERYDAY)
                )
                .build(),
            ShortcutInfo.Builder(this, ID_DOWNLOAD)
                .setShortLabel(getString(R.string.shortcuts_download_short))
                .setLongLabel(getString(R.string.shortcuts_download_long))
                .setIcon(
                    Icon.createWithResource(this, R.drawable.layerlist_shortcuts_download_24)
                )
                .setIntent(
                    Intent(this, AnimeDownloadActivity::class.java)
                        .setAction(Intent.ACTION_VIEW)
                )
                .build()
        )
        mShortcutManager.dynamicShortcuts = shortcutInfoList
    }
}