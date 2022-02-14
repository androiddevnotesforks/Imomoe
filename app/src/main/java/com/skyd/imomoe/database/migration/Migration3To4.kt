package com.skyd.imomoe.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skyd.imomoe.config.Const.Database.AppDataBase.FAVORITE_ANIME_TABLE_NAME
import com.skyd.imomoe.config.Const.Database.AppDataBase.HISTORY_TABLE_NAME
import com.skyd.imomoe.config.Const.Database.AppDataBase.SEARCH_HISTORY_TABLE_NAME

class Migration3To4 : Migration(3, 4) {
    // 删除type列
    // 不支持删除某一列，只能创建新表
    override fun migrate(database: SupportSQLiteDatabase) {
        // searchHistoryList表========================================
        // ===========================================================
        // 创建一个新表searchHistoryListTemp，只设定想要的字段
        database.execSQL(
            "CREATE TABLE ${SEARCH_HISTORY_TABLE_NAME}Temp " +
                    "(actionUrl TEXT NOT NULL, id INTEGER PRIMARY KEY NOT NULL, title TEXT NOT NULL)"
        )
        // 将原来表中的数据复制过来，
        database.execSQL(
            "INSERT INTO ${SEARCH_HISTORY_TABLE_NAME}Temp (actionUrl, id, title)" +
                    "SELECT actionUrl, id, title FROM $SEARCH_HISTORY_TABLE_NAME"
        )
        // 删除原表
        database.execSQL("DROP TABLE $SEARCH_HISTORY_TABLE_NAME")
        // 将新建的表改名
        database.execSQL("ALTER TABLE ${SEARCH_HISTORY_TABLE_NAME}Temp RENAME to $SEARCH_HISTORY_TABLE_NAME")

        // favoriteAnimeList表========================================
        // ===========================================================
        database.execSQL(
            "CREATE TABLE ${FAVORITE_ANIME_TABLE_NAME}Temp " +
                    "(actionUrl TEXT NOT NULL, animeUrl TEXT PRIMARY KEY NOT NULL, " +
                    "animeTitle TEXT NOT NULL, time INTEGER NOT NULL, cover TEXT NOT NULL, " +
                    "lastEpisodeUrl TEXT, lastEpisode TEXT)"
        )
        database.execSQL(
            "INSERT INTO ${FAVORITE_ANIME_TABLE_NAME}Temp (actionUrl, animeUrl, animeTitle, time, cover, lastEpisodeUrl, lastEpisode)" +
                    "SELECT actionUrl, animeUrl, animeTitle, time, cover, lastEpisodeUrl, lastEpisode FROM $FAVORITE_ANIME_TABLE_NAME"
        )
        database.execSQL("DROP TABLE $FAVORITE_ANIME_TABLE_NAME")
        database.execSQL("ALTER TABLE ${FAVORITE_ANIME_TABLE_NAME}Temp RENAME to $FAVORITE_ANIME_TABLE_NAME")

        // historyList表==============================================
        // ===========================================================
        database.execSQL(
            "CREATE TABLE ${HISTORY_TABLE_NAME}Temp " +
                    "(actionUrl TEXT NOT NULL, animeUrl TEXT PRIMARY KEY NOT NULL, " +
                    "animeTitle TEXT NOT NULL, time INTEGER NOT NULL, cover TEXT NOT NULL, " +
                    "lastEpisodeUrl TEXT, lastEpisode TEXT)"
        )
        database.execSQL(
            "INSERT INTO ${HISTORY_TABLE_NAME}Temp (actionUrl, animeUrl, animeTitle, time, cover, lastEpisodeUrl, lastEpisode)" +
                    "SELECT actionUrl, animeUrl, animeTitle, time, cover, lastEpisodeUrl, lastEpisode FROM $HISTORY_TABLE_NAME"
        )
        database.execSQL("DROP TABLE $HISTORY_TABLE_NAME")
        database.execSQL("ALTER TABLE ${HISTORY_TABLE_NAME}Temp RENAME to $HISTORY_TABLE_NAME")
    }
}