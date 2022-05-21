package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover5Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.util.AnimeCover5ViewHolder
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover5Proxy : VarietyAdapter.Proxy<AnimeCover5Bean, AnimeCover5ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover5ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_5, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeCover5ViewHolder,
        data: AnimeCover5Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        if (data.area.title.isNullOrBlank()) {
            holder.tvAnimeCover5Area.gone()
            holder.tvAnimeCover5Date.post {
                holder.tvAnimeCover5Date.setPadding(0, 0, 0, 0)
            }
        } else {
            holder.tvAnimeCover5Area.visible()
            holder.tvAnimeCover5Date.post {
                holder.tvAnimeCover5Date.setPadding(12.dp, 0, 0, 0)
            }
        }
        if (data.date.isBlank()) {
            holder.tvAnimeCover5Date.gone()
        } else {
            holder.tvAnimeCover5Date.visible()
        }
        holder.tvAnimeCover5Title.text = data.title
        holder.tvAnimeCover5Area.text = data.area.title
        holder.tvAnimeCover5Date.text = data.date
        holder.tvAnimeCover5Episode.text = data.episodeClickable.title
        if (holder.tvAnimeCover5Area.isGone && holder.tvAnimeCover5Date.isGone) {
            holder.tvAnimeCover5Title.post {
                holder.tvAnimeCover5Title.setPadding(
                    holder.tvAnimeCover5Title.paddingStart, 12.dp,
                    holder.tvAnimeCover5Title.paddingEnd, 12.dp
                )
            }
        }
        holder.itemView.setOnClickListener {
            data.episodeClickable.route.route(activity)
        }
        holder.tvAnimeCover5Area.setOnClickListener {
            data.area.route.route(activity)
        }
        holder.tvAnimeCover5Title.setOnClickListener {
            activity ?: return@setOnClickListener
            data.route.route(activity)
        }
    }
}