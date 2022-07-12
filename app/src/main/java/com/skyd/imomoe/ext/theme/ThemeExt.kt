package com.skyd.imomoe.ext.theme

import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Color
import android.os.Build
import android.util.TypedValue
import android.view.View
import android.view.Window
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.recreateAllBaseActivity
import com.skyd.imomoe.ext.sharedPreferences


private val map = hashMapOf(
    "Pink" to R.style.Theme_Anime_Pink,
    "Dynamic" to R.style.Theme_Anime_Dynamic,
    "Blue" to R.style.Theme_Anime_Blue,
    "Lemon" to R.style.Theme_Anime_Lemon,
    "Purple" to R.style.Theme_Anime_Purple,
    "Green" to R.style.Theme_Anime_Green,
)

private fun getKeyByValue(v: Int): String? {
    for (key in map.keys) {
        if (map[key] == v) {
            return key
        }
    }
    return null
}

var appThemeRes: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
    // getOrDefault method was added in API level 24
    map.getOrDefault(
        sharedPreferences().getString("themeRes", null),
        R.style.Theme_Anime_Pink
    )
} else {
    val v = sharedPreferences().getString("themeRes", null)
    map[v] ?: R.style.Theme_Anime_Pink
}
    set(value) {
        sharedPreferences().editor {
            putString("themeRes", getKeyByValue(value))
        }
        field = value
        recreateAllBaseActivity.tryEmit(Unit)
    }

fun Context.getAttrColor(attr: Int): Int {
    val typedValue = TypedValue()
    val typedArray: TypedArray = obtainStyledAttributes(typedValue.data, intArrayOf(attr))
    val color = typedArray.getColor(0, 0)
    typedArray.recycle()
    return color
}

/**
 * 设置状态栏和导航栏透明
 * @param root 根布局，一般传入mBinding.root，或者是window.decorView.findViewById<ViewGroup>(android.R.id.content)
 * @param darkFont 状态栏颜色是不是深色，传入null代表不更改默认颜色
 */
fun Window.transparentSystemBar(
    root: View,
    darkFont: Boolean? = context.resources.configuration.uiMode and
            Configuration.UI_MODE_NIGHT_MASK != Configuration.UI_MODE_NIGHT_YES
) {
    WindowCompat.setDecorFitsSystemWindows(this, false)
    statusBarColor = Color.TRANSPARENT
    navigationBarColor = Color.TRANSPARENT
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        navigationBarDividerColor = Color.TRANSPARENT
    }

    darkFont?.let {
        // 状态栏和导航栏字体颜色
        WindowInsetsControllerCompat(this, root).let { controller ->
            controller.isAppearanceLightStatusBars = it
            controller.isAppearanceLightNavigationBars = it
        }
    }
}