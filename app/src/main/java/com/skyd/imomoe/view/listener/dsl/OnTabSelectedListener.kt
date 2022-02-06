package com.skyd.imomoe.view.listener.dsl

import com.google.android.material.tabs.TabLayout

fun TabLayout.addOnTabSelectedListener(init: OnTabSelectedListener.() -> Unit) {
    val listener = OnTabSelectedListener()
    listener.init()
    this.addOnTabSelectedListener(listener)
}

private typealias OnTabSelected = (tab: TabLayout.Tab?) -> Unit
private typealias OnTabUnselected = (tab: TabLayout.Tab?) -> Unit
private typealias OnTabReselected = (tab: TabLayout.Tab?) -> Unit

class OnTabSelectedListener : TabLayout.OnTabSelectedListener {
    private var onTabSelected: OnTabSelected? = null
    private var onTabUnselected: OnTabUnselected? = null
    private var onTabReselected: OnTabReselected? = null

    fun onTabSelected(onTabSelected: OnTabSelected?) {
        this.onTabSelected = onTabSelected
    }

    fun onTabUnselected(onTabUnselected: OnTabUnselected?) {
        this.onTabUnselected = onTabUnselected
    }

    fun onTabReselected(onTabReselected: OnTabReselected?) {
        this.onTabReselected = onTabReselected
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        onTabSelected?.invoke(tab)
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        onTabUnselected?.invoke(tab)
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        onTabReselected?.invoke(tab)
    }
}
