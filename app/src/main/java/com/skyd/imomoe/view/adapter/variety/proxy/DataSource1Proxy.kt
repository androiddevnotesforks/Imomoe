package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.DataSource1Bean
import com.skyd.imomoe.ext.formatSize
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.util.DataSource1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class DataSource1Proxy(
    private val onClickListener: ((
        holder: DataSource1ViewHolder,
        data: DataSource1Bean,
        index: Int
    ) -> Unit)? = null,
    private val onLongClickListener: ((
        holder: DataSource1ViewHolder,
        data: DataSource1Bean,
        index: Int
    ) -> Boolean)? = null
) : VarietyAdapter.Proxy<DataSource1Bean, DataSource1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        DataSource1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_data_source_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: DataSource1ViewHolder,
        data: DataSource1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        holder.tvDataSource1Name.text = data.name
        holder.tvDataSource1Size.text = data.file.formatSize()
        if (data.selected) holder.ivDataSource1Selected.visible()
        else holder.ivDataSource1Selected.gone()
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(holder, data, index)
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener?.invoke(holder, data, index) ?: false
        }
    }
}