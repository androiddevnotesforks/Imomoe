package com.skyd.imomoe.view.adapter.variety.proxy

import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SkinCover1Bean
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.ext.theme.appThemeRes
import com.skyd.imomoe.util.SkinCover1ViewHolder
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class SkinCover1Proxy : VarietyAdapter.Proxy<SkinCover1Bean, SkinCover1ViewHolder>() {
    private var selectedItem: SkinCover1ViewHolder? = null
    private var selectedItemPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SkinCover1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_skin_cover_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: SkinCover1ViewHolder,
        data: SkinCover1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        if (data.using) {
            holder.ivSkinCover1Selected.visible()
            selectedItem = holder
            selectedItemPosition = index
        } else holder.ivSkinCover1Selected.gone()
        holder.tvSkinCover1Title.text = data.title
        data.cover.let { cover ->
            if (cover is Int) {
                holder.ivSkinCover1Cover.setImageDrawable(ColorDrawable(cover))
            } else if (cover is String) {
                holder.ivSkinCover1Cover.loadImage(cover)
            }
        }
        holder.itemView.setOnClickListener {
            if (data.using) return@setOnClickListener
            holder.ivSkinCover1Selected.visible()
            ((holder.bindingAdapter as VarietyAdapter)
                .dataList.getOrNull(selectedItemPosition) as? SkinCover1Bean)
                ?.using = false
            selectedItem?.ivSkinCover1Selected?.gone()
            selectedItem = holder
            selectedItemPosition = index
            appThemeRes.postValue(data.themeRes)
            holder.itemView.activity?.recreate()
        }
    }
}