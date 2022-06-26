package com.skyd.imomoe.view.adapter.variety.proxy

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.DataSource2Bean
import com.skyd.imomoe.bean.DataSourceRepositoryBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.ext.theme.getAttrColor
import com.skyd.imomoe.ext.toTimeString
import com.skyd.imomoe.util.DataSource2ViewHolder
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class DataSource2Proxy(
    private val onActionClickListener: ((
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
        holder.tvDataSource2Author.text =
            appContext.getString(R.string.data_source_author, data.author)
        updateStatus(holder, data)
        data.icon.let {
            if (it.isNullOrBlank()) {
                holder.ivDataSource2Icon.loadImage(R.drawable.ic_insert_drive_file_24)
                holder.ivDataSource2Icon.imageTintList = ColorStateList.valueOf(
                    appContext.getAttrColor(R.attr.colorPrimary)
                )
            } else {
                holder.ivDataSource2Icon.loadImage(
                    if (it.startsWith("/")) Api.DATA_SOURCE_PREFIX + it
                    else it
                )
                holder.ivDataSource2Icon.imageTintList = null
            }
        }
        holder.btnDataSource2Action.setOnClickListener {
            onActionClickListener?.invoke(holder, data, index)
        }
    }

    override fun onBindViewHolder(
        holder: DataSource2ViewHolder,
        data: DataSource2Bean,
        index: Int,
        action: ((Any?) -> Unit)?,
        payloads: MutableList<Any>
    ) {
        payloads.forEach {
            if (it is List<*>) {
                it.forEach { item ->
                    when (item) {
                        DataSource2Bean.STATUS -> {
                            updateStatus(holder, data)
                            return
                        }
                    }
                }
            }
        }
        onBindViewHolder(holder, data, index, action)
    }

    private fun updateStatus(
        holder: DataSource2ViewHolder,
        data: DataSource2Bean,
    ) {
        val activity = holder.itemView.activity
        holder.btnDataSource2Action.apply {
            text = when (data.status) {
                DataSourceRepositoryBean.Status.NONE -> {
                    isEnabled = true
                    activity.getString(R.string.download)
                }
                DataSourceRepositoryBean.Status.NEWEST -> {
                    isEnabled = false
                    activity.getString(R.string.installed)
                }
                DataSourceRepositoryBean.Status.DOWNLOADING -> {
                    isEnabled = false
                    activity.getString(R.string.downloading)
                }
                DataSourceRepositoryBean.Status.INSTALLING -> {
                    isEnabled = false
                    activity.getString(R.string.installing)
                }
                DataSourceRepositoryBean.Status.OUTDATED -> {
                    isEnabled = true
                    activity.getString(R.string.update)
                }
                else -> {
                    isEnabled = true
                    activity.getString(R.string.download)
                }
            }
        }
    }
}