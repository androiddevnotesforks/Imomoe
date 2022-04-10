package com.skyd.imomoe.ext

import android.app.Activity
import android.content.res.ColorStateList
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.skyd.imomoe.App
import com.skyd.imomoe.R

fun CharSequence.showSnackbar(
    activity: Activity,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: CharSequence? = App.context.getString(R.string.close),
    actionCallback: (() -> Unit)? = null,
    backgroundTintList: ColorStateList? = null,
    textColor: ColorStateList? = null,
    actionTextColor: ColorStateList? = null
) {
    showSnackbar(
        view = activity.findViewById(android.R.id.content),
        duration = duration,
        actionText = actionText,
        actionCallback = actionCallback,
        backgroundTintList = backgroundTintList,
        textColor = textColor,
        actionTextColor = actionTextColor
    )
}


fun CharSequence.showSnackbar(
    view: View,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionText: CharSequence? = App.context.getString(R.string.close),
    actionCallback: (() -> Unit)? = null,
    backgroundTintList: ColorStateList? = null,
    textColor: ColorStateList? = null,
    actionTextColor: ColorStateList? = null
) {
    Snackbar.make(view, this, duration)
        .setAction(actionText) { actionCallback?.invoke() }
        .apply {
            if (backgroundTintList != null) setBackgroundTintList(backgroundTintList)
            if (textColor != null) setTextColor(textColor)
            if (actionTextColor != null) setActionTextColor(textColor)
        }
        .show()
}
