package com.skyd.imomoe.ext

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Rect
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

val View.activity: Activity
    get() = context.activity

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

/**
 * 判断View和给定的Rect是否重叠（边和点不计入）
 * @return true if overlap
 */
fun View.overlap(rect: Rect): Boolean {
    val location = IntArray(2)
    getLocationOnScreen(location)
    val left = location[0]
    val right = location[0] + width
    val top = location[1]
    val bottom = location[1] + height
    return !(left > rect.right || right < rect.left || top > rect.bottom || bottom < rect.top)
}