package com.skyd.imomoe.view.adapter.variety.proxy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover7Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.invisible
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.route.processor.EpisodeDownloadProcessor
import com.skyd.imomoe.util.AnimeCover7ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover7Proxy(
    private val onLongClickListener: ((
        holder: AnimeCover7ViewHolder,
        data: AnimeCover7Bean,
        index: Int
    ) -> Boolean)? = null
) : VarietyAdapter.Proxy<AnimeCover7Bean, AnimeCover7ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover7ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_7, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AnimeCover7ViewHolder,
        data: AnimeCover7Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.tvAnimeCover7Title.text = data.title
        holder.tvAnimeCover7Size.text = data.size
        if (data.pathType == 1) {
            holder.tvAnimeCover7OldPath.text = activity.getString(R.string.old_path)
            holder.tvAnimeCover7OldPath.visible()
        } else {
            holder.tvAnimeCover7OldPath.gone()
        }
        if (data.route.startsWith(EpisodeDownloadProcessor.route, ignoreCase = true)) {
            holder.tvAnimeCover7Episodes.text = data.episodeCount
            holder.tvAnimeCover7Episodes.visible()
        } else {
            holder.tvAnimeCover7Episodes.invisible()
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener?.invoke(holder, data, index) ?: false
        }
        holder.itemView.setOnClickListener {
            data.route.route(activity)
        }
    }
}