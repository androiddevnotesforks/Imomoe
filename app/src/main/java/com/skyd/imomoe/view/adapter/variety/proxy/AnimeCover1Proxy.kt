package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover1Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.util.AnimeCover1ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover1Proxy(
    @IntRange(from = 0, to = 1) private val color: Int = BLACK,
) : VarietyAdapter.Proxy<AnimeCover1Bean, AnimeCover1ViewHolder>() {
    companion object {
        const val BLACK = 0
        const val WHITE = 1
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeCover1ViewHolder,
        data: AnimeCover1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        when (color) {
            BLACK -> holder.tvAnimeCover1Title.setTextColor(getResColor(R.color.foreground_black_skin))
            WHITE -> holder.tvAnimeCover1Title.setTextColor(getResColor(R.color.foreground_white_skin))
        }
        holder.ivAnimeCover1Cover.setTag(R.id.image_view_tag, data.cover.url)
        activity?.let {
            if (holder.ivAnimeCover1Cover.getTag(R.id.image_view_tag) == data.cover.url) {
                holder.ivAnimeCover1Cover.loadImage(data.cover.url, referer = data.cover.referer)
            }
        }
        holder.tvAnimeCover1Title.text = data.title
        if (data.episode.isBlank()) {
            holder.tvAnimeCover1Episode.gone()
        } else {
            holder.tvAnimeCover1Episode.visible()
            holder.tvAnimeCover1Episode.text = data.episode
        }
        holder.itemView.setOnClickListener {
            activity?.also { Util.process(it, data.actionUrl) }
        }
    }
}