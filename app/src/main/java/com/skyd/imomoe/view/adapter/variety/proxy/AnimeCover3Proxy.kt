package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover3Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.util.AnimeCover3ViewHolder
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover3Proxy : VarietyAdapter.Proxy<AnimeCover3Bean, AnimeCover3ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover3ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_3, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeCover3ViewHolder,
        data: AnimeCover3Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.ivAnimeCover3Cover.setTag(R.id.image_view_tag, data.cover?.url)
        holder.tvAnimeCover3Title.text = data.title
        holder.tvAnimeCover3Describe.text = data.describe
        if (data.episode.isNullOrBlank()) {
            holder.tvAnimeCover3Episode.gone()
        } else {
            holder.tvAnimeCover3Episode.visible()
            holder.tvAnimeCover3Episode.text = data.episode
        }
        holder.flAnimeCover3Type.removeAllViews()
        if (activity != null) {
            if (holder.ivAnimeCover3Cover.getTag(R.id.image_view_tag) == data.cover?.url) {
                holder.ivAnimeCover3Cover.loadImage(data.cover?.url, referer = data.cover?.referer)
            }

            data.animeType.orEmpty().forEach { type ->
                val cardView = activity.layoutInflater.inflate(
                    R.layout.item_anime_type_1,
                    holder.flAnimeCover3Type,
                    false
                ) as CardView
                cardView.findViewById<TextView>(R.id.tv_anime_type_1).text = type.title
                cardView.setOnClickListener {
                    if (type.route.isBlank()) return@setOnClickListener
                    type.route.route(activity)
                }
                holder.flAnimeCover3Type.addView(cardView)
            }
        }
        holder.itemView.setOnClickListener {
            data.route.route(activity)
        }
    }
}