package com.skyd.imomoe.ext

import android.annotation.SuppressLint
import android.text.Html
import android.text.Spanned
import com.skyd.imomoe.BuildConfig

/**
 * 屏蔽带有某些关键字的弹幕
 *
 * @return 若屏蔽此字符串，则返回true，否则false
 */
fun String.shield(): Boolean {
    BuildConfig.SHIELD_TEXT.forEach {
        if (this.contains(it, true)) return true
    }
    return false
}

fun String.toHtml(@SuppressLint("InlinedApi") flag: Int = Html.FROM_HTML_MODE_LEGACY): Spanned {
    return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        Html.fromHtml(this, flag)
    } else {
        @Suppress("DEPRECATION")
        Html.fromHtml(this)
    }
}