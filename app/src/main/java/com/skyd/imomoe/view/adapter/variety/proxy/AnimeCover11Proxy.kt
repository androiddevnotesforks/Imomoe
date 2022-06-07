package com.skyd.imomoe.view.adapter.variety.proxy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeCover11Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.util.AnimeCover11ViewHolder
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeCover11Proxy : VarietyAdapter.Proxy<AnimeCover11Bean, AnimeCover11ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeCover11ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_anime_cover_11, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: AnimeCover11ViewHolder,
        data: AnimeCover11Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.tvAnimeCover11Rank.text = "${index + 1}"
        holder.tvAnimeCover11Rank.background = if (index in 0..2) {
            val backgrounds = intArrayOf(
                R.drawable.shape_fill_circle_corner_golden_50,
                R.drawable.shape_fill_circle_corner_silvery_50,
                R.drawable.shape_fill_circle_corner_coppery_50
            )
            Util.getResDrawable(backgrounds[index])
        } else {
            Util.getResDrawable(R.drawable.shape_fill_circle_corner_50)
        }
        holder.tvAnimeCover11Title.text = data.title
        holder.itemView.setOnClickListener {
            data.route.route(activity)
        }
    }
}