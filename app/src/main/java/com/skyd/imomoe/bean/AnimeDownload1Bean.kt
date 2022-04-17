package com.skyd.imomoe.bean

import com.arialyy.aria.core.download.DownloadEntity
import com.skyd.imomoe.view.adapter.variety.Diff

class AnimeDownload1Bean(
    override var actionUrl: String,
    var title: String,
    var episode: String,
    var url: String,
    var id: Long,
    var peerNum: Int = -1,
    var peerIndex: Int = -1,
    var percent: Int = 0,
    var fileSize: Long = 0,
) : BaseBean, Diff {
    val isM3U8: Boolean
        get() = peerIndex != -1 && peerNum != -1

    override fun sameAs(o: Any?): Boolean {
        return o is AnimeDownload1Bean && url == o.url && id == o.id
    }

    override fun contentSameAs(o: Any?): Boolean {
        return when {
            o !is AnimeDownload1Bean -> false
            actionUrl == o.actionUrl && title == o.title && episode == o.episode &&
                    url == o.url && id == o.id &&
                    percent == o.percent &&
                    peerNum == o.peerNum &&
                    peerIndex == o.peerIndex -> true
            else -> false
        }
    }

    override fun diff(o: Any?): Any? {
        return when {
            o !is AnimeDownload1Bean -> null
            peerIndex != o.peerIndex -> PEER_INDEX
            percent != o.percent -> PERCENT
            else -> null
        }
    }

    override fun equals(other: Any?): Boolean {
        return (other is AnimeDownload1Bean && url == other.url && id == other.id &&
                peerIndex == other.peerIndex && peerNum == other.peerNum &&
                percent == other.percent && fileSize == other.fileSize &&
                episode == other.episode && title == other.title) ||
                (other is DownloadEntity && url == other.url && id == other.id &&
                        peerIndex == other.m3U8Entity?.peerIndex &&
                        peerNum == other.m3U8Entity?.peerNum &&
                        percent == other.percent && fileSize == other.fileSize)
    }

    override fun hashCode(): Int {
        var result = actionUrl.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + episode.hashCode()
        result = 31 * result + url.hashCode()
        result = 31 * result + id.hashCode()
        result = 31 * result + peerNum
        result = 31 * result + peerIndex
        result = 31 * result + percent
        return result
    }

    companion object {
        const val PEER_INDEX = "peerIndex"
        const val PERCENT = "percent"

        fun create(
            title: String,
            episode: String,
            entity: DownloadEntity
        ): AnimeDownload1Bean {
            return AnimeDownload1Bean(
                actionUrl = "",
                title = title,
                episode = episode,
                url = entity.url,
                id = entity.id
            ).apply {
                val m3U8Entity = entity.m3U8Entity
                if (m3U8Entity != null) {
                    peerIndex = m3U8Entity.peerIndex
                    peerNum = m3U8Entity.peerNum
                }
                fileSize = entity.fileSize
                percent = entity.percent
            }
        }
    }
}