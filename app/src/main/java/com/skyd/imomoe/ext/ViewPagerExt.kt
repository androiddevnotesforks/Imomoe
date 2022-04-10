package com.skyd.imomoe.ext

import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2


fun ViewPager2.fitsSystemWindows2() {
    ViewCompat.setOnApplyWindowInsetsListener(this, OnApplyWindowInsetsListener { v, ins ->
        val insets = ViewCompat.onApplyWindowInsets(v, ins)
        if (insets.isConsumed) {
            return@OnApplyWindowInsetsListener insets
        }
        var consumed = false
        var i = 0
        val recyclerView = getChildAt(0) as RecyclerView
        val count: Int = recyclerView.childCount
        while (i < count) {
            ViewCompat.dispatchApplyWindowInsets(recyclerView.getChildAt(i), insets)
            if (insets.isConsumed) consumed = true
            i++
        }
        if (consumed) insets.consumeSystemWindowInsets() else insets
    })
}