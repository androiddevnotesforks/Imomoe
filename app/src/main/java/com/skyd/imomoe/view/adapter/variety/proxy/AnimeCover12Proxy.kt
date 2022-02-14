package com.skyd.imomoe.view.adapter.variety.proxy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover12Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.util.AnimeCover12ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover12Proxy : VarietyAdapter.Proxy<AnimeCover12Bean, AnimeCover12ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover12ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_12, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AnimeCover12ViewHolder,
        data: AnimeCover12Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.tvAnimeCover12Title.text = data.title
        holder.tvAnimeCover12Episode.text = data.episodeClickable.title
        holder.itemView.setOnClickListener {
            activity?.also {
                if (data.episodeClickable.actionUrl == data.actionUrl)
                    Util.process(it, data.episodeClickable.actionUrl)
                else Util.process(it, data.episodeClickable.actionUrl + data.actionUrl)
            }
        }
        holder.tvAnimeCover12Title.setOnClickListener {
            activity?.also { Util.process(it, data.actionUrl) }
        }
    }
}