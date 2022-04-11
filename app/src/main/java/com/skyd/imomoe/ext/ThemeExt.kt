package com.skyd.imomoe.ext

import android.content.Context
import android.content.res.TypedArray
import android.util.TypedValue
import androidx.annotation.StyleRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.MutableLiveData
import com.skyd.imomoe.R


var darkMode: Int = sharedPreferences()
    .getInt("darkMode", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    .also { AppCompatDelegate.setDefaultNightMode(it) }
    set(value) {
        if (value != AppCompatDelegate.MODE_NIGHT_YES &&
            value != AppCompatDelegate.MODE_NIGHT_NO &&
            value != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ) {
            throw IllegalArgumentException("darkMode value invalid!!!")
        }
        sharedPreferences().editor { putInt("darkMode", value) }
        AppCompatDelegate.setDefaultNightMode(value)
        field = value
    }

private val map = hashMapOf(
    "Pink" to R.style.Theme_Anime_Pink,
    "Dynamic" to R.style.Theme_Anime_Dynamic,
    "Blue" to R.style.Theme_Anime_Blue,
    "Lemon" to R.style.Theme_Anime_Lemon,
    "Purple" to R.style.Theme_Anime_Purple,
)

private fun getKeyByValue(v: Int): String? {
    for (key in map.keys) {
        if (map[key] == v) {
            return key
        }
    }
    return null
}

var appThemeRes: MutableLiveData<Int> = object : MutableLiveData<Int>(
    map.getOrDefault(
        sharedPreferences().getString("themeRes", null),
        R.style.Theme_Anime_Pink
    )
) {
    override fun postValue(@StyleRes value: Int?) {
        sharedPreferences().editor {
            putString(
                "themeRes",
                getKeyByValue(value ?: R.style.Theme_Anime_Pink)
            )
        }
        super.postValue(value)
    }

    override fun setValue(value: Int?) {
        sharedPreferences().editor {
            putString(
                "themeRes",
                getKeyByValue(value ?: R.style.Theme_Anime_Pink)
            )
        }
        super.setValue(value)
    }
}

fun Context.getAttrColor(attr: Int): Int {
    val typedValue = TypedValue()
    val typedArray: TypedArray = obtainStyledAttributes(typedValue.data, intArrayOf(attr))
    val color = typedArray.getColor(0, 0)
    typedArray.recycle()
    return color
}