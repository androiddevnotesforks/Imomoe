package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.formatSize
import com.skyd.imomoe.util.RestoreFile1ViewHolder
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.Util.time2Now
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.thegrizzlylabs.sardineandroid.DavResource

class RestoreFile1Proxy(
    private val onClickListener: ((
        holder: RestoreFile1ViewHolder,
        data: DavResource,
        index: Int
    ) -> Unit)? = null,
    private val onLongClickListener: ((
        holder: RestoreFile1ViewHolder,
        data: DavResource,
        index: Int
    ) -> Boolean)? = null
) : VarietyAdapter.Proxy<DavResource, RestoreFile1ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        RestoreFile1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_restore_file_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: RestoreFile1ViewHolder,
        data: DavResource,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        if (data.name.contains(".db")) {
            holder.ivRestoreFile1Icon.setImageDrawable(getResDrawable(R.drawable.ic_database_main_color_2_24_skin))
        } else {
            holder.ivRestoreFile1Icon.setImageDrawable(getResDrawable(R.drawable.ic_insert_drive_file_main_color_2_24_skin))
        }
        holder.tvRestoreFile1Title.text = data.displayName
        holder.tvRestoreFile1Size.text = data.contentLength.formatSize()
        holder.tvRestoreFile1LastModified.text = time2Now(data.modified.time)
        holder.itemView.setOnClickListener {
            onClickListener?.invoke(holder, data, index)
        }
        holder.itemView.setOnLongClickListener {
            onLongClickListener?.invoke(holder, data, index) ?: false
        }
    }
}