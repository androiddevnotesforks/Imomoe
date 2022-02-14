package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.GridRecyclerView1
import com.skyd.imomoe.util.GridRecyclerView1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class GridRecyclerView1Proxy(
    private val onBindViewHolder: ((
        holder: GridRecyclerView1ViewHolder,
        data: GridRecyclerView1,
        index: Int
    ) -> Unit)? = null,
    private val height: Int? = null,
    private val width: Int? = null
) :
    VarietyAdapter.Proxy<GridRecyclerView1, GridRecyclerView1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        GridRecyclerView1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_grid_recycler_view_1, parent, false)
        ).apply {
            itemView.layoutParams.let { layoutParams ->
                height?.let { layoutParams.height = it }
                width?.let { layoutParams.width = it }
                itemView.layoutParams = layoutParams
            }
        }

    override fun onBindViewHolder(
        holder: GridRecyclerView1ViewHolder,
        data: GridRecyclerView1,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        onBindViewHolder?.invoke(holder, data, index)
    }
}