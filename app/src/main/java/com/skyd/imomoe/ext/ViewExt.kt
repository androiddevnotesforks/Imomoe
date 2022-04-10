package com.skyd.imomoe.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import com.skyd.imomoe.appContext


fun View.enable() {
    if (isEnabled) return
    isEnabled = true
}

fun View.disable() {
    if (!isEnabled) return
    isEnabled = false
}

fun View.gone(animate: Boolean = false, dur: Long = 500L) {
    if (visibility == View.GONE) return
    if (animate) startAnimation(AlphaAnimation(1f, 0f).apply { duration = dur })
    visibility = View.GONE
}

fun View.visible(animate: Boolean = false, dur: Long = 500L) {
    if (visibility == View.VISIBLE) return
    visibility = View.VISIBLE
    if (animate) startAnimation(AlphaAnimation(0f, 1f).apply { duration = dur })
}

fun View.invisible(animate: Boolean = false, dur: Long = 500L) {
    if (visibility == View.INVISIBLE) return
    visibility = View.INVISIBLE
    if (animate) startAnimation(AlphaAnimation(0f, 1f).apply { duration = dur })
}

fun View.clickScale(scale: Float = 0.75f, duration: Long = 100) {
    animate().scaleX(scale).scaleY(scale).setDuration(duration)
        .withEndAction {
            animate().scaleX(1f).scaleY(1f).setDuration(duration).start()
        }.start()
}

val View.activity: Activity?
    get() {
        var context: Context = context
        while (context is ContextWrapper) {
            if (context is Activity) {
                return context
            }
            context = context.baseContext
        }
        return null
    }

fun View.showKeyboard() {
    isFocusable = true
    isFocusableInTouchMode = true
    requestFocus()
    val inputManager =
        appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.showSoftInput(this, 0)
}

fun View.hideKeyboard() {
    val inputManager =
        appContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.hideSoftInputFromWindow(this.windowToken, 0)
}