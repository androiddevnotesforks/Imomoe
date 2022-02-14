package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.Banner1Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.util.Banner1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.component.bannerview.adapter.MyCycleBannerAdapter
import com.skyd.imomoe.view.component.bannerview.indicator.DotIndicator
import com.skyd.skin.SkinManager

class Banner1Proxy : VarietyAdapter.Proxy<Banner1Bean, Banner1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        Banner1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_banner_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: Banner1ViewHolder,
        data: Banner1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.banner1.apply {
            setAdapter(MyCycleBannerAdapter(data.animeCoverList))
            activity?.also { setIndicator(DotIndicator(it).apply { SkinManager.setViewTag(this) }) }
            startPlay(5000)
        }
    }
}