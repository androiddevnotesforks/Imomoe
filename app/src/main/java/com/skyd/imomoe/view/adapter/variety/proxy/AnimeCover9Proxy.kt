package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover9Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.util.AnimeCover9ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover9Proxy(
    private val onDeleteButtonClickListener: ((
        holder: AnimeCover9ViewHolder,
        data: AnimeCover9Bean,
        index: Int
    ) -> Unit)? = null
) : VarietyAdapter.Proxy<AnimeCover9Bean, AnimeCover9ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover9ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_9, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeCover9ViewHolder,
        data: AnimeCover9Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.ivAnimeCover9Cover.loadImage(url = data.cover.url, referer = data.cover.referer)
        holder.tvAnimeCover9Title.text = data.animeTitle
        holder.tvAnimeCover9Episodes.text = data.lastEpisode
        holder.tvAnimeCover9Time.text = Util.time2Now(data.time)
        holder.tvAnimeCover9DetailPage.setOnClickListener {
            activity?.also { Util.process(it, data.animeUrl, data.animeUrl) }
        }
        holder.ivAnimeCover9Delete.setOnClickListener {
            onDeleteButtonClickListener?.invoke(holder, data, index)
        }
        holder.itemView.setOnClickListener {
            if (data.lastEpisodeUrl != null)
                activity?.also {
                    Util.process(
                        activity,
                        data.lastEpisodeUrl + data.animeUrl,
                        data.lastEpisodeUrl ?: ""
                    )
                }
            else activity?.also { Util.process(activity, data.animeUrl, data.animeUrl) }
        }
    }
}