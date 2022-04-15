package com.skyd.imomoe.ext.theme

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.ext.showListDialog

class DarkMode(val name: String, val value: Int) : CharSequence {
    override val length: Int
        get() = name.length

    override fun get(index: Int): Char = name[index]

    override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
        return name.subSequence(startIndex, endIndex)
    }

    override fun toString(): String = name

    override fun equals(other: Any?): Boolean {
        return when (other) {
            null -> false
            is String -> other == name
            is DarkMode -> other.name == this.name && other.value == this.value
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value
        return result
    }
}

private infix fun String.to(that: Int): DarkMode = DarkMode(this, that)

val darkModeList: List<DarkMode> = mutableListOf(
    appContext.getString(R.string.dark_ext_dark_yes) to AppCompatDelegate.MODE_NIGHT_YES,
    appContext.getString(R.string.dark_ext_dark_no) to AppCompatDelegate.MODE_NIGHT_NO
).apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) add(
        0,
        appContext.getString(R.string.dark_ext_dark_follow_system) to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
    )
}

var darkMode: Int = 0
    get() {
        return if (field != AppCompatDelegate.MODE_NIGHT_YES &&
            field != AppCompatDelegate.MODE_NIGHT_NO &&
            field != AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        ) throw IllegalStateException("call initDarkMode() in app onCreate")
        else field
    }
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

fun initDarkMode() {
    darkMode = sharedPreferences()
        .getInt(
            "darkMode", if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
        .also { AppCompatDelegate.setDefaultNightMode(it) }
}

fun Activity.selectDarkMode() {
    var initialSelection = 0
    darkModeList.forEachIndexed { index, s ->
        if (s.value == darkMode) initialSelection = index
    }
    showListDialog(
        title = getString(R.string.dark_ext_select_dark_mode),
        items = darkModeList,
        checkedItem = initialSelection,
        onNegative = { dialog, _ -> dialog.dismiss() }
    ) { _, _, itemIndex ->
        darkMode = darkModeList[itemIndex].value
    }
}
