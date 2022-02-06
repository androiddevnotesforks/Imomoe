package com.skyd.imomoe.view.listener.dsl

import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.AppCompatSpinner

fun AppCompatSpinner.setOnItemSelectedListener(init: OnItemSelectedListener.() -> Unit) {
    val listener = OnItemSelectedListener()
    listener.init()
    this.onItemSelectedListener = listener
}

private typealias OnItemSelected = (parent: AdapterView<*>?, view: View?, position: Int, id: Long) -> Unit
private typealias OnNothingSelected = (parent: AdapterView<*>?) -> Unit

class OnItemSelectedListener : AdapterView.OnItemSelectedListener {
    private var onItemSelected: OnItemSelected? = null
    private var onNothingSelected: OnNothingSelected? = null

    fun onItemSelected(itemSelected: OnItemSelected?) {
        this.onItemSelected = itemSelected
    }

    fun onNothingSelected(nothingSelected: OnNothingSelected?) {
        this.onNothingSelected = nothingSelected
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onItemSelected?.invoke(parent, view, position, id)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        onNothingSelected?.invoke(parent)
    }
}
