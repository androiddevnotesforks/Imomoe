package com.skyd.imomoe.view.adapter.variety.proxy

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.arialyy.aria.core.inf.IEntity
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeDownload1Bean
import com.skyd.imomoe.ext.formatSize
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.percentage
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.util.AnimeDownload1ViewHolder
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter

class AnimeDownload1Proxy(
    private val onCancelClickListener: ((
        holder: AnimeDownload1ViewHolder,
        data: AnimeDownload1Bean,
        index: Int
    ) -> Unit)? = null,
    private val onPauseClickListener: ((
        holder: AnimeDownload1ViewHolder,
        data: AnimeDownload1Bean,
        index: Int
    ) -> Unit)? = null,
    private val onResumeClickListener: ((
        holder: AnimeDownload1ViewHolder,
        data: AnimeDownload1Bean,
        index: Int
    ) -> Unit)? = null,
    private val onFailedRetryClickListener: ((
        holder: AnimeDownload1ViewHolder,
        data: AnimeDownload1Bean,
        index: Int
    ) -> Unit)? = null
) : VarietyAdapter.Proxy<AnimeDownload1Bean, AnimeDownload1ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        AnimeDownload1ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_anime_download_1, parent, false)
        )

    override fun onBindViewHolder(
        holder: AnimeDownload1ViewHolder,
        data: AnimeDownload1Bean,
        index: Int,
        action: ((Any?) -> Unit)?
    ) {
        updateState(holder, data, index)
        updateSpeed(holder, data)
        holder.tvAnimeDownload1Title.text = data.title
        holder.tvAnimeDownload1Episode.text = data.episode
        holder.tvAnimeDownload1Size.text = data.run {
            if (isM3U8) {
                holder.itemView.context.getString(
                    R.string.m3u8_peer_count,
                    peerNum.toString()
                )
            } else {
                fileSize.formatSize()
            }
        }
        holder.tvAnimeDownload1Percent.text = percent(data)
        holder.ivAnimeDownload1Cancel.setOnClickListener {
            onCancelClickListener?.invoke(holder, data, index)
        }
        holder.pbAnimeDownload1.setProgressCompat(progress(data), true)
    }

    override fun onBindViewHolder(
        holder: AnimeDownload1ViewHolder,
        data: AnimeDownload1Bean,
        index: Int,
        action: ((Any?) -> Unit)?,
        payloads: MutableList<Any>
    ) {
        payloads.forEach {
            if (it is List<*>) {
                it.forEach { item ->
                    when (item) {
                        AnimeDownload1Bean.PEER_INDEX, AnimeDownload1Bean.PERCENT -> {
                            holder.pbAnimeDownload1.setProgressCompat(progress(data), true)
                            holder.tvAnimeDownload1Percent.text = percent(data)
                            return
                        }
                        AnimeDownload1Bean.STATE -> {
                            updateState(holder, data, index)
                        }
                        AnimeDownload1Bean.SPEED -> {
                            updateSpeed(holder, data)
                        }
                    }
                }
            }
        }
        onBindViewHolder(holder, data, index, action)
    }

    private fun progress(data: AnimeDownload1Bean): Int {
        return if (data.isM3U8) {
            if (data.peerNum > 0) {
                ((data.peerIndex + 1) * 100.0 / data.peerNum).toInt()
            } else {
                0
            }
        } else {
            data.percent
        }
    }

    private fun percent(data: AnimeDownload1Bean): String {
        return if (data.isM3U8) {
            if (data.peerNum > 0) {
                ((data.peerIndex + 1) * 100.0 / data.peerNum).percentage()
            } else {
                0.0.percentage()
            }
        } else {
            data.percent.percentage
        }
    }

    private fun updateState(
        holder: AnimeDownload1ViewHolder,
        data: AnimeDownload1Bean,
        index: Int
    ) {
        when (data.state) {
            IEntity.STATE_RUNNING, IEntity.STATE_PRE, IEntity.STATE_POST_PRE -> {
                holder.ivAnimeDownload1State.apply {
                    setImageResource(R.drawable.ic_pause_24)
                    setOnClickListener {
                        onPauseClickListener?.invoke(holder, data, index)
                        setImageResource(R.drawable.ic_play_24)
                    }
                }
                holder.tvAnimeDownload1Speed.visible()
            }
            IEntity.STATE_WAIT, IEntity.STATE_STOP -> {
                holder.ivAnimeDownload1State.apply {
                    setImageResource(R.drawable.ic_play_24)
                    setOnClickListener {
                        onResumeClickListener?.invoke(holder, data, index)
                        setImageResource(R.drawable.ic_pause_24)
                    }
                }
                holder.tvAnimeDownload1Speed.gone()
            }
            IEntity.STATE_FAIL -> {
                holder.ivAnimeDownload1State.apply {
                    setImageResource(R.drawable.ic_replay_24)
                    setOnClickListener {
                        onFailedRetryClickListener?.invoke(holder, data, index)
                    }
                }
                holder.tvAnimeDownload1Speed.gone()
            }
            else -> {
                holder.ivAnimeDownload1State.apply {
                    setImageResource(R.drawable.ic_replay_24)
                    setOnClickListener { }
                }
                holder.tvAnimeDownload1Speed.gone()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateSpeed(
        holder: AnimeDownload1ViewHolder,
        data: AnimeDownload1Bean
    ) {
        if (holder.tvAnimeDownload1Speed.isVisible) {
            holder.tvAnimeDownload1Speed.text = "${data.speed.formatSize()}/s"
        }
    }
}