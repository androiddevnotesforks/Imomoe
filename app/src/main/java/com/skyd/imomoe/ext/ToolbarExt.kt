package com.skyd.imomoe.ext

import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.appbar.AppBarLayout

/**
 * 防止键盘弹出时fitsSystemWindows会将键盘高度也加到Toolbar上
 */
fun Toolbar.fixKeyboardFitsSystemWindows() {
    ViewCompat.setOnApplyWindowInsetsListener(this) { v, ins ->
        v.updatePadding(top = ins.getInsets(WindowInsetsCompat.Type.statusBars()).top, bottom = 0)
        ins
    }
}

/**
 * 透明状态栏界面折叠下，隐藏Toolbar
 */
fun AppBarLayout.hideToolbarWhenCollapsed(v: View) {
    addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
        v.alpha = 1 + verticalOffset / appBarLayout.totalScrollRange.toFloat()
    })
}