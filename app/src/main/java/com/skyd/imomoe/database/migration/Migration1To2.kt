package com.skyd.imomoe.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skyd.imomoe.config.Const.Database.AppDataBase.ANIME_DOWNLOAD_TABLE_NAME

class Migration1To2 : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE $ANIME_DOWNLOAD_TABLE_NAME ADD fileName TEXT")
    }
}