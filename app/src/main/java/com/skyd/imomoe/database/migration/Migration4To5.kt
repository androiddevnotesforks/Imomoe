package com.skyd.imomoe.database.migration

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.skyd.imomoe.config.Const.Database.AppDataBase.URL_MAP_TABLE_NAME

class Migration4To5 : Migration(4, 5) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE ${URL_MAP_TABLE_NAME}(oldUrl TEXT PRIMARY KEY NOT NULL, newUrl TEXT NOT NULL, enable INTEGER NOT NULL)")
    }
}