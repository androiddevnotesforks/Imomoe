package com.skyd.imomoe.ext.theme

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.util.TypedValue
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.sharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch


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

private val coroutineScope by lazy { CoroutineScope(Dispatchers.IO) }

var appThemeRes: MutableStateFlow<Int> = MutableStateFlow(
    // getOrDefault method was added in API level 24
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        map.getOrDefault(
            sharedPreferences().getString("themeRes", null),
            R.style.Theme_Anime_Pink
        )
    } else {
        val v = sharedPreferences().getString("themeRes", null)
        map[v] ?: R.style.Theme_Anime_Pink
    }
).apply {
    coroutineScope.launch {
        collect {
            sharedPreferences().editor {
                putString("themeRes", getKeyByValue(value))
            }
        }
    }
}

fun Context.getAttrColor(attr: Int): Int {
    val typedValue = TypedValue()
    val typedArray: TypedArray = obtainStyledAttributes(typedValue.data, intArrayOf(attr))
    val color = typedArray.getColor(0, 0)
    typedArray.recycle()
    return color
}