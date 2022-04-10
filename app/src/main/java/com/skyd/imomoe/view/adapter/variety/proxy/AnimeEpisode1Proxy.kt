package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisode1Bean
import com.skyd.imomoe.util.AnimeEpisode1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter


class AnimeEpisode1Proxy(
    private val onClickListener: ((
        holder: AnimeEpisode1ViewHolder,
        data: AnimeEpisode1Bean,
        index: Int
    ) -> Unit)? = null,
    private val height: Int? = null,
    private val width: Int? = null
) : VarietyAdapter.Proxy<AnimeEpisode1Bean, AnimeEpisode1ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeEpisode1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_anime_episode_1, parent, false)
        ).apply {
            itemView.layoutParams.let { layoutParams ->
                height?.let { layoutParams.height = it }
                width?.let { layoutParams.width = it }
                itemView.layoutParams = layoutParams
            }
        }

    override fun onBindViewHolder(
        holder: AnimeEpisode1ViewHolder,
        data: AnimeEpisode1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        holder.tvAnimeEpisode1.apply {
            text = data.title
            setOnClickListener { onClickListener?.invoke(holder, data, index) }
        }
    }
}