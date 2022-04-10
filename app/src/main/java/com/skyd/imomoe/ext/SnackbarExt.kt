package com.skyd.imomoe.ext

import android.app.Activity
import android.content.res.ColorStateList
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.skyd.imomoe.R

fun Activity.showSnackbar(
    text: CharSequence,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: CharSequence? = getString(R.string.close),
    actionCallback: (() -> Unit)? = null,
    backgroundTintList: ColorStateList? = null,
    textColor: ColorStateList? = null,
    actionTextColor: ColorStateList? = null
) {
    findViewById<View>(android.R.id.content).showSnackbar(
        text = text,
        duration = duration,
        actionText = actionText,
        actionCallback = actionCallback,
        backgroundTintList = backgroundTintList,
        textColor = textColor,
        actionTextColor = actionTextColor
    )
}


fun View.showSnackbar(
    text: CharSequence,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: CharSequence? = context.getString(R.string.close),
    actionCallback: (() -> Unit)? = null,
    backgroundTintList: ColorStateList? = null,
    textColor: ColorStateList? = null,
    actionTextColor: ColorStateList? = null
) {
    Snackbar.make(this, text, duration)
        .setAction(actionText) { actionCallback?.invoke() }
        .apply {
            if (backgroundTintList != null) setBackgroundTintList(backgroundTintList)
            if (textColor != null) setTextColor(textColor)
            if (actionTextColor != null) setActionTextColor(textColor)
        }
        .show()
}
