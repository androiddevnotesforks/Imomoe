package com.skyd.imomoe.database.converter

import androidx.room.TypeConverter
import com.skyd.imomoe.util.download.DownloadStatus

class AnimeDownloadStatusConverter {

    @TypeConverter
    fun intToEnum(status: Int?): DownloadStatus? = DownloadStatus.values()[status ?: 0]

    @TypeConverter
    fun enumToInt(animeDownloadStatus: DownloadStatus?): Int? = animeDownloadStatus?.ordinal

}