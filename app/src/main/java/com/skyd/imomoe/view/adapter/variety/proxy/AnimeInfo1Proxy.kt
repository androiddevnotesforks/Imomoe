package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeInfo1Bean
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.util.AnimeInfo1ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeInfo1Proxy(
    private val onBindViewHolder: ((
        holder: AnimeInfo1ViewHolder,
        data: AnimeInfo1Bean,
        index: Int
    ) -> Boolean)? = null
) : VarietyAdapter.Proxy<AnimeInfo1Bean, AnimeInfo1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeInfo1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_anime_info_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeInfo1ViewHolder,
        data: AnimeInfo1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        if (onBindViewHolder?.invoke(holder, data, index) == true) return
        val activity = holder.itemView.activity
        holder.ivAnimeInfo1Cover.setTag(R.id.image_view_tag, data.cover.url)
        if (holder.ivAnimeInfo1Cover.getTag(R.id.image_view_tag) == data.cover.url) {
            holder.ivAnimeInfo1Cover.loadImage(
                data.cover.url, referer = data.cover.referer, placeholder = 0, error = 0
            )
        }
        holder.tvAnimeInfo1Title.text = data.title
        holder.tvAnimeInfo1Alias.text = data.alias
        holder.tvAnimeInfo1Area.text = data.area
        holder.tvAnimeInfo1Year.text = data.year
        holder.tvAnimeInfo1Index.text =
            App.context.getString(R.string.anime_detail_index, data.index)
        holder.tvAnimeInfo1Info.text = data.info
        holder.flAnimeInfo1Type.removeAllViews()
        data.animeType.forEach { type ->
            activity ?: return@forEach
            val tvFlowLayout: TextView = activity.layoutInflater
                .inflate(R.layout.item_anime_type_1, holder.flAnimeInfo1Type, false) as TextView
            tvFlowLayout.text = type.title
            tvFlowLayout.setOnClickListener {
                if (type.actionUrl.isBlank()) return@setOnClickListener
                //此处是”类型“，若要修改，需要注意Tab大分类是否还是”类型“
                val actionUrl = type.actionUrl.run {
                    if (endsWith("/")) "${this}${type.title}"
                    else "${this}/${type.title}"
                }
                Util.process(activity, Const.ActionUrl.ANIME_CLASSIFY + actionUrl)
            }
            holder.flAnimeInfo1Type.addView(tvFlowLayout)
        }
        holder.flAnimeInfo1Tag.removeAllViews()
        data.tag.forEach { tag ->
            activity ?: return@forEach
            val tvFlowLayout: TextView = activity.layoutInflater
                .inflate(R.layout.item_anime_type_1, holder.flAnimeInfo1Tag, false) as TextView
            tvFlowLayout.text = tag.title
            tvFlowLayout.setOnClickListener {
                //此处是”标签“，由于分类没有这一大项，因此传入”“串
                val actionUrl = tag.actionUrl.run {
                    if (endsWith("/")) "${this}${tag.title}"
                    else "${this}/${tag.title}"
                }
                Util.process(activity, Const.ActionUrl.ANIME_CLASSIFY + actionUrl)
            }
            holder.flAnimeInfo1Tag.addView(tvFlowLayout)
        }
    }
}