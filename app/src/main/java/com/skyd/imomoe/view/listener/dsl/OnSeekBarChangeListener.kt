package com.skyd.imomoe.view.listener.dsl

import android.widget.SeekBar

fun SeekBar.setOnSeekBarChangeListener(init: OnSeekBarChangeListener.() -> Unit) {
    val listener = OnSeekBarChangeListener()
    listener.init()
    this.setOnSeekBarChangeListener(listener)
}

private typealias OnProgressChanged = (seekBar: SeekBar?, progress: Int, fromUser: Boolean) -> Unit
private typealias OnStartTrackingTouch = (seekBar: SeekBar?) -> Unit
private typealias OnStopTrackingTouch = (seekBar: SeekBar?) -> Unit

class OnSeekBarChangeListener : SeekBar.OnSeekBarChangeListener {
    private var onProgressChanged: OnProgressChanged? = null
    private var onStartTrackingTouch: OnStartTrackingTouch? = null
    private var onStopTrackingTouch: OnStopTrackingTouch? = null

    fun onProgressChanged(onProgressChanged: OnProgressChanged?) {
        this.onProgressChanged = onProgressChanged
    }

    fun onStartTrackingTouch(onStartTrackingTouch: OnStartTrackingTouch?) {
        this.onStartTrackingTouch = onStartTrackingTouch
    }

    fun onStopTrackingTouch(onStopTrackingTouch: OnStopTrackingTouch?) {
        this.onStopTrackingTouch = onStopTrackingTouch
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        onProgressChanged?.invoke(seekBar, progress, fromUser)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        onStartTrackingTouch?.invoke(seekBar)
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        onStopTrackingTouch?.invoke(seekBar)
    }
}
