package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.ClassifyTab1Bean
import com.skyd.imomoe.util.ClassifyTab1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class ClassifyTab1Proxy(
    private val onClickListener: ((
        holder: ClassifyTab1ViewHolder,
        data: ClassifyTab1Bean,
        index: Int
    ) -> Unit)? = null
) : VarietyAdapter.Proxy<ClassifyTab1Bean, ClassifyTab1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        ClassifyTab1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_text_view_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: ClassifyTab1ViewHolder,
        data: ClassifyTab1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        holder.textView.text = data.title
        holder.itemView.setOnClickListener { onClickListener?.invoke(holder, data, index) }
    }
}