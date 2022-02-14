package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover8Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.util.AnimeCover8ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover8Proxy : VarietyAdapter.Proxy<AnimeCover8Bean, AnimeCover8ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover8ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_8, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeCover8ViewHolder,
        data: AnimeCover8Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.ivAnimeCover8Cover.loadImage(url = data.cover.url, referer = data.cover.referer)
        holder.tvAnimeCover8Title.text = data.animeTitle
        holder.tvAnimeCover8Episodes.text = data.lastEpisode?.let {
            App.context.getString(R.string.already_seen_episode_x, it)
        } ?: App.context.getString(R.string.have_not_watched_this_anime)
        holder.itemView.setOnClickListener {
            if (data.lastEpisodeUrl != null) Util.process(
                activity ?: return@setOnClickListener,
                data.lastEpisodeUrl + data.animeUrl,
                data.lastEpisodeUrl.orEmpty()
            )
            else
                Util.process(activity ?: return@setOnClickListener, data.animeUrl, data.animeUrl)
        }
        // 长按跳转详情页
        holder.itemView.setOnLongClickListener {
            if (activity != null) Util.process(activity, data.animeUrl, data.animeUrl)
            true
        }
    }
}