package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisode1Bean
import com.skyd.imomoe.util.AnimeEpisode1ViewHolder
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter


class AnimeEpisode1Proxy(
    @IntRange(from = 0, to = 1) private val color: Int = MAIN_COLOR_2,
    private val onClickListener: ((
        holder: AnimeEpisode1ViewHolder,
        data: AnimeEpisode1Bean,
        index: Int
    ) -> Unit)? = null,
    private val height: Int? = null,
    private val width: Int? = null
) : VarietyAdapter.Proxy<AnimeEpisode1Bean, AnimeEpisode1ViewHolder>() {
    companion object {
        const val MAIN_COLOR_2 = 0
        const val WHITE = 1
    }

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
        when (color) {
            MAIN_COLOR_2 -> {
                holder.tvAnimeEpisode1.setTextColor(getResColor(R.color.foreground_main_color_2_skin))
                holder.tvAnimeEpisode1.background =
                    getResDrawable(R.drawable.shape_circle_corner_edge_main_color_2_ripper_5_skin)
            }
            WHITE -> {
                holder.tvAnimeEpisode1.setTextColor(getResColor(R.color.foreground_white_skin))
                holder.tvAnimeEpisode1.background =
                    getResDrawable(R.drawable.shape_circle_corner_edge_white_ripper_5_skin)
            }
        }
        holder.tvAnimeEpisode1.apply {
            text = data.title
            setOnClickListener { onClickListener?.invoke(holder, data, index) }
        }
    }
}