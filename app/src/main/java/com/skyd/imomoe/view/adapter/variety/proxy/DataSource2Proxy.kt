package com.skyd.imomoe.view.adapter.variety.proxy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.DataSource2Bean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.ext.toTimeString
import com.skyd.imomoe.util.DataSource2ViewHolder
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class DataSource2Proxy(
    private val onClickListener: ((
        holder: DataSource2ViewHolder,
        data: DataSource2Bean,
        index: Int
    ) -> Unit)? = null
) : VarietyAdapter.Proxy<DataSource2Bean, DataSource2ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        DataSource2ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_data_source_2, parent, false)
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(
        holder: DataSource2ViewHolder,
        data: DataSource2Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        holder.tvDataSource2Name.text = data.name
        holder.tvDataSource2Version.text = "${data.versionName}(${data.versionCode})"
        holder.tvDataSource2PublishAt.text = data.publicAt.toTimeString("yyyy-MM-dd HH:mm")
        holder.tvDataSource2Describe.text = data.describe
        holder.tvDataSource2Author.text = data.author
        data.icon.let {
            if (it.isNullOrBlank()) {
                holder.ivDataSource2Icon.setImageDrawable(getResDrawable(R.drawable.ic_insert_drive_file_main_color_2_24_skin))
            } else {
                holder.ivDataSource2Icon.loadImage(
                    if (it.startsWith("/")) Api.DATA_SOURCE_IMAGE_PREFIX + it
                    else it
                )
            }
        }
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(holder, data, index)
        }
    }
}