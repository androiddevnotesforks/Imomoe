package com.skyd.imomoe.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.skyd.imomoe.App
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.database.converter.AnimeDownloadStatusConverter
import com.skyd.imomoe.database.converter.ImageBeanConverter
import com.skyd.imomoe.database.entity.AnimeDownloadEntity
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.bean.HistoryBean
import com.skyd.imomoe.config.Const.Database.AppDataBase.APP_DATA_BASE_FILE_NAME
import com.skyd.imomoe.database.dao.*
import com.skyd.imomoe.database.migration.Migration1To2
import com.skyd.imomoe.database.migration.Migration2To3
import com.skyd.imomoe.database.migration.Migration3To4

@Database(
    entities = [SearchHistoryBean::class,
        AnimeDownloadEntity::class,
        FavoriteAnimeBean::class,
        HistoryBean::class], version = 4
)
@TypeConverters(
    value = [AnimeDownloadStatusConverter::class,
        ImageBeanConverter::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun searchHistoryDao(): SearchHistoryDao
    abstract fun animeDownloadDao(): AnimeDownloadDao
    abstract fun favoriteAnimeDao(): FavoriteAnimeDao
    abstract fun historyDao(): HistoryDao

    companion object {
        private var instance: AppDatabase? = null

        private val migrations = arrayOf(Migration1To2(), Migration2To3(), Migration3To4())

        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                if (instance != null) return instance as AppDatabase
                return synchronized(this) {
                    Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        APP_DATA_BASE_FILE_NAME
                    )
                        .addMigrations(*migrations)
                        .build()
                }
            } else {
                return instance as AppDatabase
            }

        }
    }
}

fun getAppDataBase() = AppDatabase.getInstance(App.context)