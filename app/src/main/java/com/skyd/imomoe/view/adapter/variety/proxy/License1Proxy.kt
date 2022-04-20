package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.License1Bean
import com.skyd.imomoe.ext.activity
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.util.License1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class License1Proxy : VarietyAdapter.Proxy<License1Bean, License1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        License1ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_license_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: License1ViewHolder,
        data: License1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        val activity = holder.itemView.activity
        holder.tvLicense1Name.text = data.title
        holder.tvLicense1License.text = data.license
        holder.itemView.setOnClickListener {
            activity?.also { data.route.route(it) }
        }
    }
}