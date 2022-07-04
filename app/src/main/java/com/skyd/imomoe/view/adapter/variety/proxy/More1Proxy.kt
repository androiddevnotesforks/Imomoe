package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.More1Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.util.More1ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class More1Proxy : VarietyAdapter.Proxy<More1Bean, More1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        More1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_more_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: More1ViewHolder,
        data: More1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.ivMore1.setImageDrawable(Util.getResDrawable(data.image))
        holder.tvMore1.text = data.title
        holder.itemView.setOnClickListener {
            data.route.route(activity)
        }
    }
}