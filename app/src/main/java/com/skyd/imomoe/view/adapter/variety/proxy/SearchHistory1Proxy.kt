package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SearchHistory1Bean
import com.skyd.imomoe.util.SearchHistory1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class SearchHistory1Proxy(
    private val onClickListener: ((
        holder: SearchHistory1ViewHolder,
        data: SearchHistory1Bean,
        index: Int
    ) -> Unit)? = null,
    private val onDeleteButtonClickListener: ((
        holder: SearchHistory1ViewHolder,
        data: SearchHistory1Bean,
        index: Int
    ) -> Unit)? = null
) : VarietyAdapter.Proxy<SearchHistory1Bean, SearchHistory1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        SearchHistory1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_search_history_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: SearchHistory1ViewHolder,
        data: SearchHistory1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        holder.tvSearchHistory1Title.text = data.title
        holder.ivSearchHistory1Delete.setOnClickListener {
            onDeleteButtonClickListener?.invoke(holder, data, index)
        }
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(holder, data, index)
        }
    }
}