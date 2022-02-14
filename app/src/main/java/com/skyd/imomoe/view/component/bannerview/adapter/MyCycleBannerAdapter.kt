package com.skyd.imomoe.view.component.bannerview.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.bean.AnimeCover6Bean
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover6Proxy
import com.skyd.skin.SkinManager

/**
 * Created by Sky_D on 2021-02-08.
 */
class MyCycleBannerAdapter(
    private val dataList: List<AnimeCover6Bean>
) : CycleBannerAdapter() {
    private val proxy = AnimeCover6Proxy(
        height = ViewGroup.LayoutParams.MATCH_PARENT,
        width = ViewGroup.LayoutParams.MATCH_PARENT
    )

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return proxy.onCreateViewHolder(parent, viewType).apply { SkinManager.setSkin(itemView) }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBind(holder: RecyclerView.ViewHolder, position: Int) {
        (proxy as VarietyAdapter.Proxy<Any, RecyclerView.ViewHolder>)
            .onBindViewHolder(holder, dataList[position], position)
    }

    override fun getCount(): Int = dataList.size
}