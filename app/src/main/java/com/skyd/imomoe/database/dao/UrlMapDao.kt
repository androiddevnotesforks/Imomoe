package com.skyd.imomoe.database.dao

import androidx.room.*
import com.skyd.imomoe.config.Const.Database.AppDataBase.URL_MAP_TABLE_NAME
import com.skyd.imomoe.database.entity.UrlMapEntity

@Dao
interface UrlMapDao {
    @Query(value = "SELECT * FROM $URL_MAP_TABLE_NAME")
    fun getAll(): List<UrlMapEntity>

    @Query(value = "SELECT * FROM $URL_MAP_TABLE_NAME WHERE enabled = 1")
    fun getAllEnabled(): List<UrlMapEntity>

    @Query(value = "SELECT newUrl FROM $URL_MAP_TABLE_NAME WHERE oldUrl = :oldUrl")
    fun getNewUrl(oldUrl: String): String?

    @Query(value = "REPLACE INTO $URL_MAP_TABLE_NAME(oldUrl, newUrl, enabled) VALUES(:oldUrl, :newUrl, :enabled)")
    fun setNewUrl(oldUrl: String, newUrl: String, enabled: Boolean)

    @Query(value = "DELETE FROM $URL_MAP_TABLE_NAME WHERE oldUrl = :oldUrl")
    fun delete(oldUrl: String): Int

    @Query(value = "UPDATE $URL_MAP_TABLE_NAME SET enabled = :enabled WHERE oldUrl = :oldUrl")
    fun enabled(oldUrl: String, enabled: Boolean): Int
}