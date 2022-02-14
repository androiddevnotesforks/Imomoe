package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover3Bean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.util.AnimeCover3ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover3Proxy : VarietyAdapter.Proxy<AnimeCover3Bean, AnimeCover3ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover3ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_3, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeCover3ViewHolder,
        data: AnimeCover3Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.ivAnimeCover3Cover.setTag(R.id.image_view_tag, data.cover.url)
        holder.tvAnimeCover3Title.text = data.title
        holder.tvAnimeCover3Describe.text = data.describe
        if (data.episode.isBlank()) {
            holder.tvAnimeCover3Episode.gone()
        } else {
            holder.tvAnimeCover3Episode.visible()
            holder.tvAnimeCover3Episode.text = data.episode
        }
        holder.flAnimeCover3Type.removeAllViews()
        if (activity != null) {
            if (holder.ivAnimeCover3Cover.getTag(R.id.image_view_tag) == data.cover.url) {
                holder.ivAnimeCover3Cover.loadImage(data.cover.url, referer = data.cover.referer)
            }

            data.animeType.forEach { type ->
                val tvFlowLayout: TextView = activity.layoutInflater.inflate(
                    R.layout.item_anime_type_1,
                    holder.flAnimeCover3Type,
                    false
                ) as TextView
                tvFlowLayout.text = type.title
                tvFlowLayout.setOnClickListener {
                    if (type.actionUrl.isBlank()) return@setOnClickListener
                    //此处是”类型“，若要修改，需要注意Tab大分类是否还是”类型“
                    val actionUrl = type.actionUrl.run {
                        if (endsWith("/")) "${this}${type.title}"
                        else "${this}/${type.title}"
                    }
                    Util.process(
                        activity,
                        Const.ActionUrl.ANIME_CLASSIFY + actionUrl
                    )
                }
                holder.flAnimeCover3Type.addView(tvFlowLayout)
            }
        }
        holder.itemView.setOnClickListener {
            if (activity != null) Util.process(activity, data.actionUrl)
        }
    }
}