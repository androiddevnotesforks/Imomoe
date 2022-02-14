package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover6Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.util.AnimeCover6ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover6Proxy(
    private val height: Int? = null,
    private val width: Int? = null
) : VarietyAdapter.Proxy<AnimeCover6Bean, AnimeCover6ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover6ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_6, parent, false)
        ).apply {
            itemView.layoutParams.let { layoutParams ->
                height?.let { layoutParams.height = it }
                width?.let { layoutParams.width = it }
                itemView.layoutParams = layoutParams
            }
        }

    override fun onBindViewHolder(
        holder: AnimeCover6ViewHolder,
        data: AnimeCover6Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.ivAnimeCover6Cover.loadImage(data.cover.url, referer = data.cover.referer)
        holder.tvAnimeCover6Title.text = data.title
        holder.tvAnimeCover6Episode.text = data.episodeClickable?.title
        if (data.describe.isEmpty()) {
            holder.tvAnimeCover6Describe.gone()
        } else {
            holder.tvAnimeCover6Describe.visible()
            holder.tvAnimeCover6Describe.text = data.describe
        }
        holder.itemView.setOnClickListener {
            Util.process(activity ?: return@setOnClickListener, data.actionUrl)
        }
    }
}