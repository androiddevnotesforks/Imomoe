package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.util.UpnpDevice1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import org.fourthline.cling.model.meta.Device

class UpnpDevice1Proxy(
    private val onClickListener: ((
        holder: UpnpDevice1ViewHolder,
        data: Device<*, *, *>,
        index: Int
    ) -> Unit)? = null
) : VarietyAdapter.Proxy<Device<*, *, *>, UpnpDevice1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        UpnpDevice1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_dlna_device_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: UpnpDevice1ViewHolder,
        data: Device<*, *, *>,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        holder.tvUpnpDevice1Title.text = data.details?.friendlyName
        holder.itemView.setOnClickListener { onClickListener?.invoke(holder, data, index) }
    }
}