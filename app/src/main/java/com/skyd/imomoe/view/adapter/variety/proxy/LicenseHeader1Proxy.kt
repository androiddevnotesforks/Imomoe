package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.LicenseHeader1Bean
import com.skyd.imomoe.util.LicenseHeader1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class LicenseHeader1Proxy : VarietyAdapter.Proxy<LicenseHeader1Bean, LicenseHeader1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        LicenseHeader1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_license_header_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: LicenseHeader1ViewHolder,
        data: LicenseHeader1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
    }
}