package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover4Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.util.AnimeCover4ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover4Proxy : VarietyAdapter.Proxy<AnimeCover4Bean, AnimeCover4ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover4ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_4, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeCover4ViewHolder,
        data: AnimeCover4Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.ivAnimeCover4Cover.setTag(R.id.image_view_tag, data.cover.url)
        if (activity != null) {
            if (holder.ivAnimeCover4Cover.getTag(R.id.image_view_tag) == data.cover.url) {
                holder.ivAnimeCover4Cover.loadImage(data.cover.url, referer = data.cover.referer)
            }
        }
        holder.tvAnimeCover4Title.text = data.title
        holder.itemView.setOnClickListener {
            Util.process(activity ?: return@setOnClickListener, data.actionUrl)
        }
    }
}