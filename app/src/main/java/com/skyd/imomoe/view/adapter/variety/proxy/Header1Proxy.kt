package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.Header1Bean
import com.skyd.imomoe.ext.theme.getAttrColor
import com.skyd.imomoe.util.Header1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter


class Header1Proxy(
    @IntRange(from = 0, to = 1) private val color: Int = THEME_COLOR
) : VarietyAdapter.Proxy<Header1Bean, Header1ViewHolder>() {
    companion object {
        const val THEME_COLOR = 0
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
            THEME_COLOR -> {
                holder.tvHeader1Title.setTextColor(
                    holder.itemView.context.getAttrColor(R.attr.colorPrimary)
                )
            }
            WHITE -> {
                holder.tvHeader1Title.setTextColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        android.R.color.white
                    )
                )
            }
        }
        holder.tvHeader1Title.text = data.title
    }
}