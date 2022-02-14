package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeDescribe1Bean
import com.skyd.imomoe.util.AnimeDescribe1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeDescribe1Proxy : VarietyAdapter.Proxy<AnimeDescribe1Bean, AnimeDescribe1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeDescribe1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_anime_describe_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeDescribe1ViewHolder,
        data: AnimeDescribe1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        holder.tvAnimeDescribe1.text = data.describe
        holder.tvAnimeDescribe1.setOnClickListener { }
    }
}