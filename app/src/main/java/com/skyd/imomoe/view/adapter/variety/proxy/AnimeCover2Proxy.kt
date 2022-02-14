package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover2Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.util.AnimeCover2ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover2Proxy : VarietyAdapter.Proxy<AnimeCover2Bean, AnimeCover2ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover2ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_2, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeCover2ViewHolder,
        data: AnimeCover2Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.tvAnimeCover1Title.text = data.title
        holder.tvAnimeCover1Episode.gone()
        holder.itemView.setOnClickListener {
            Util.process(activity ?: return@setOnClickListener, data.actionUrl)
        }
    }
}