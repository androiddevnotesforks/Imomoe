package com.skyd.imomoe.ext

import android.app.Activity
import android.graphics.drawable.Drawable
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R

fun Fragment.warningDialog(
    onPositive: ((MaterialDialog) -> Unit)? = null,
    onNegative: ((MaterialDialog) -> Unit)? = { it.dismiss() },
    icon: Drawable? = null,
    cancelable: Boolean = true,
    positiveRes: Int = R.string.ok,
    negativeRes: Int = R.string.cancel
): MaterialDialog =
    requireActivity().warningDialog(
        onPositive = onPositive, onNegative = onNegative,
        icon = icon, cancelable = cancelable, positiveRes = positiveRes, negativeRes = negativeRes
    )

fun Activity.warningDialog(
    onPositive: ((MaterialDialog) -> Unit)? = null,
    onNegative: ((MaterialDialog) -> Unit)? = { it.dismiss() },
    icon: Drawable? = null,
    cancelable: Boolean = true,
    positiveRes: Int = R.string.ok,
    negativeRes: Int = R.string.cancel
): MaterialDialog {
    return MaterialDialog(this).apply {
        title(res = R.string.warning)
        cancelable(cancelable)
        if (icon != null) icon(drawable = icon)
        if (onPositive != null) positiveButton(res = positiveRes) { onPositive.invoke(this) }
        if (onNegative != null) negativeButton(res = negativeRes) { onNegative.invoke(this) }
    }
}