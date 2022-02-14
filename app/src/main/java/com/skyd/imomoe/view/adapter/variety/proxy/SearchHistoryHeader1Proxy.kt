package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SearchHistoryHeader1Bean
import com.skyd.imomoe.util.SearchHistoryHeader1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class SearchHistoryHeader1Proxy : VarietyAdapter.Proxy<SearchHistoryHeader1Bean, SearchHistoryHeader1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SearchHistoryHeader1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_search_history_header_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: SearchHistoryHeader1ViewHolder,
        data: SearchHistoryHeader1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        holder.tvSearchHistoryHeader1Title.text = data.title
    }
}