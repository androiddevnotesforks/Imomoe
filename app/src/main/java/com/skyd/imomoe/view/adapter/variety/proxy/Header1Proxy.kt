package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.Header1Bean
import com.skyd.imomoe.util.Header1ViewHolder
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class Header1Proxy(
    @IntRange(from = 0, to = 1) private val color: Int = MAIN_COLOR_2
) : VarietyAdapter.Proxy<Header1Bean, Header1ViewHolder>() {
    companion object {
        const val MAIN_COLOR_2 = 0
        const val WHITE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        Header1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_header_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: Header1ViewHolder,
        data: Header1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        when (color) {
            MAIN_COLOR_2 -> {
                holder.tvHeader1Title.setTextColor(getResColor(R.color.foreground_main_color_2_skin))
            }
            WHITE -> {
                holder.tvHeader1Title.setTextColor(getResColor(R.color.foreground_white_skin))
            }
        }
        holder.tvHeader1Title.text = data.title
    }
}