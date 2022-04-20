package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.AnimeCover8Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.route.processor.PlayActivityProcessor
import com.skyd.imomoe.util.AnimeCover8ViewHolder
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
            appContext.getString(R.string.already_seen_episode_x, it)
        } ?: appContext.getString(R.string.have_not_watched_this_anime)
        holder.itemView.setOnClickListener {
            if (data.lastEpisodeUrl != null)
                PlayActivityProcessor.route.buildRouteUri {
                    appendQueryParameter("partUrl", data.lastEpisodeUrl)
                    appendQueryParameter("detailPartUrl", data.animeUrl)
                }.route(activity)
            else data.animeUrl.route(activity)
        }
        // 长按跳转详情页
        holder.itemView.setOnLongClickListener {
            data.animeUrl.route(activity)
            true
        }
    }
}