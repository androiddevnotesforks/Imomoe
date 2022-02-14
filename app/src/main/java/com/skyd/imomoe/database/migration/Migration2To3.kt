package com.skyd.imomoe.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skyd.imomoe.config.Const.Database.AppDataBase.FAVORITE_ANIME_TABLE_NAME
import com.skyd.imomoe.config.Const.Database.AppDataBase.HISTORY_TABLE_NAME

class Migration2To3 : Migration(2, 3) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE ${FAVORITE_ANIME_TABLE_NAME}(type TEXT NOT NULL, actionUrl TEXT NOT NULL, animeUrl TEXT PRIMARY KEY NOT NULL, animeTitle TEXT NOT NULL, time INTEGER NOT NULL, cover TEXT NOT NULL, lastEpisodeUrl TEXT, lastEpisode TEXT)")
        database.execSQL("CREATE TABLE ${HISTORY_TABLE_NAME}(type TEXT NOT NULL, actionUrl TEXT NOT NULL, animeUrl TEXT PRIMARY KEY NOT NULL, animeTitle TEXT NOT NULL, time INTEGER NOT NULL, cover TEXT NOT NULL, lastEpisodeUrl TEXT, lastEpisode TEXT)")
    }
}