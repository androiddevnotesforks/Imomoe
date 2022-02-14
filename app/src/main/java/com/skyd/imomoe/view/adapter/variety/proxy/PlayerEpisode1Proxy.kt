package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.component.player.AnimeVideoPlayer

class PlayerEpisode1Proxy(
    private val onBindViewHolder: ((
        holder: AnimeVideoPlayer.RightRecyclerViewViewHolder,
        data: AnimeEpisodeDataBean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) -> Boolean)? = null      // 返回值指是否消费了onBindViewHolder
) : VarietyAdapter.Proxy<AnimeEpisodeDataBean, AnimeVideoPlayer.RightRecyclerViewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeVideoPlayer.RightRecyclerViewViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_player_list_item_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeVideoPlayer.RightRecyclerViewViewHolder,
        data: AnimeEpisodeDataBean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        if (onBindViewHolder?.invoke(holder, data, index, action) == true) return
        holder.tvTitle.text = data.title
    }
}