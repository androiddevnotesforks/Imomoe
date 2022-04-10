package com.skyd.imomoe.ext

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ProgressBar
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.skyd.imomoe.R


fun Fragment.showMessageDialog(
    title: CharSequence = getString(R.string.warning),
    message: CharSequence? = null,
    @DrawableRes icon: Int = 0,
    cancelable: Boolean = true,
    negativeText: String = resources.getString(R.string.cancel),
    positiveText: String = resources.getString(R.string.ok),
    onNegative: ((dialog: DialogInterface, which: Int) -> Unit)? = { dialog, _ -> dialog.dismiss() },
    onPositive: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
): AlertDialog =
    requireActivity().showMessageDialog(
        onPositive = onPositive,
        onNegative = onNegative,
        message = message,
        icon = icon,
        title = title,
        cancelable = cancelable,
        positiveText = positiveText,
        negativeText = negativeText
    )

fun Activity.showMessageDialog(
    title: CharSequence = getString(R.string.warning),
    message: CharSequence? = null,
    @DrawableRes icon: Int = 0,
    cancelable: Boolean = true,
    negativeText: String = getString(R.string.cancel),
    positiveText: String = getString(R.string.ok),
    onNegative: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    onPositive: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
): AlertDialog {
    return MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setMessage(message)
        .apply { onPositive?.let { setPositiveButton(positiveText, it) } }
        .apply { onNegative?.let { setNegativeButton(negativeText, it) } }
        .setCancelable(cancelable)
        .setIcon(icon)
        .show()
}

fun Activity.showListDialog(
    title: CharSequence? = null,
    items: List<CharSequence>? = null,
    checkedItem: Int = -1,
    onItemClickListener: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    icon: Drawable? = null,
    cancelable: Boolean = true,
    negativeText: String = getString(R.string.cancel),
    neutralText: String? = null,
    positiveText: String = getString(R.string.ok),
    onNegative: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    onNeutral: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    onPositive: ((dialog: DialogInterface, which: Int, itemIndex: Int) -> Unit)? = null,
): AlertDialog {
    var itemIndex = checkedItem
    var positionButton: Button? = null
    return MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .setSingleChoiceItems(items?.toTypedArray(), checkedItem) { dialog, which ->
            itemIndex = which
            positionButton?.isEnabled = which != -1
            onItemClickListener?.invoke(dialog, which)
        }
        .apply {
            onPositive?.let {
                setPositiveButton(positiveText) { dialog, which ->
                    onPositive.invoke(dialog, which, itemIndex)
                }
            }
        }
        .apply { onNegative?.let { setNegativeButton(negativeText, it) } }
        .apply {
            if (onNeutral != null && neutralText != null) {
                setNeutralButton(neutralText, onNeutral)
            }
        }
        .setCancelable(cancelable)
        .setIcon(icon)
        .show().apply {
            positionButton = getButton(AlertDialog.BUTTON_POSITIVE)
            positionButton?.isEnabled = itemIndex != -1
        }
}

fun Fragment.showInputDialog(
    title: CharSequence? = null,
    hint: String? = null,
    prefill: CharSequence? = null,
    icon: Drawable? = null,
    cancelable: Boolean = true,
    negativeText: String = getString(R.string.cancel),
    neutralText: String? = null,
    positiveText: String = getString(R.string.ok),
    onNegative: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    onNeutral: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    onPositive: ((dialog: DialogInterface, which: Int, text: CharSequence) -> Unit)? = null,
): AlertDialog {
    return requireActivity().showInputDialog(
        title = title,
        hint = hint,
        prefill = prefill,
        icon = icon,
        cancelable = cancelable,
        negativeText = negativeText,
        neutralText = neutralText,
        positiveText = positiveText,
        onNegative = onNegative,
        onNeutral = onNeutral,
        onPositive = onPositive
    )
}

fun Context.showInputDialog(
    title: CharSequence? = null,
    hint: String? = null,
    prefill: CharSequence? = null,
    icon: Drawable? = null,
    cancelable: Boolean = true,
    negativeText: String = getString(R.string.cancel),
    neutralText: String? = null,
    positiveText: String = getString(R.string.ok),
    onNegative: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    onNeutral: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    onPositive: ((dialog: DialogInterface, which: Int, text: CharSequence) -> Unit)? = null,
): AlertDialog {
    var text: CharSequence = ""
    var positionButton: Button? = null
    return MaterialAlertDialogBuilder(this)
        .setTitle(title)
        .apply {
            onPositive?.let {
                setPositiveButton(positiveText) { dialog, which ->
                    onPositive.invoke(dialog, which, text)
                }
            }
        }
        .apply { onNegative?.let { setNegativeButton(negativeText, it) } }
        .apply {
            if (onNeutral != null && neutralText != null) {
                setNeutralButton(neutralText, onNeutral)
            }
        }
        .setCancelable(cancelable)
        .setIcon(icon)
        .setView(R.layout.layout_input_dialog)
        .show()
        .apply {
            findViewById<TextInputLayout>(R.id.til_input_dialog)?.hint = hint
            findViewById<TextInputEditText>(R.id.et_input_dialog)?.also {
                it.doOnTextChanged { t, _, _, _ ->
                    if (!t.isNullOrEmpty()) {
                        text = t
                        positionButton?.isEnabled = true
                    } else {
                        positionButton?.isEnabled = false
                    }
                }
                autoShowKeyboard(this@showInputDialog, it)
                window?.fixWindowTranslucentStatus()
                if (prefill != null) it.setText(prefill)
                it.selectAll()
            }
            positionButton = getButton(AlertDialog.BUTTON_POSITIVE)
            positionButton?.isEnabled = false
        }
}

private var waitingDialog: AlertDialog? = null
fun dismissWaitingDialog() {
    waitingDialog?.dismiss()
}

fun Activity.showWaitingDialog(
    message: CharSequence? = null,
    cancelable: Boolean = true,
    negativeText: String = getString(R.string.cancel),
    positiveText: String = getString(R.string.ok),
    onNegative: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
    onPositive: ((dialog: DialogInterface, which: Int) -> Unit)? = null,
): AlertDialog {
    waitingDialog = MaterialAlertDialogBuilder(this)
        .setMessage(message)
        .setView(ProgressBar(this))
        .apply { onPositive?.let { setPositiveButton(positiveText, it) } }
        .apply { onNegative?.let { setNegativeButton(negativeText, it) } }
        .setCancelable(cancelable)
        .setOnDismissListener { waitingDialog = null }
        .show()
    return waitingDialog!!
}

/**
 * 修复theme.xml里面设置windowTranslucentStatus=true时，Dialog无法上弹的问题
 */
private fun Window.fixWindowTranslucentStatus() {
    clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
}

private fun autoShowKeyboard(context: Context, v: View) {
    v.post {
        v.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT)
    }
}