package com.skyd.imomoe.view.adapter.variety.proxy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arialyy.aria.core.download.DownloadEntity
import com.skyd.imomoe.R
import com.skyd.imomoe.util.AnimeDownload1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeDownload1Proxy(
    private val onBindViewHolder: ((
        holder: AnimeDownload1ViewHolder,
        data: DownloadEntity,
        index: Int
    ) -> Unit)? = null
) : VarietyAdapter.Proxy<DownloadEntity, AnimeDownload1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeDownload1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_anime_download_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeDownload1ViewHolder,
        data: DownloadEntity,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        onBindViewHolder?.invoke(holder, data, index)
    }
}