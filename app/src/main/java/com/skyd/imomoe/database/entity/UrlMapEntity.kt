package com.skyd.imomoe.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.view.adapter.variety.Diff
import java.io.Serializable

@Entity(tableName = Const.Database.AppDataBase.URL_MAP_TABLE_NAME)
class UrlMapEntity(
    @PrimaryKey
    @ColumnInfo(name = "oldUrl")
    var oldUrl: String,        // 需要更改的前缀
    @ColumnInfo(name = "newUrl")
    var newUrl: String,      // 更改为
    @ColumnInfo(name = "enabled")
    var enabled: Boolean,      // 是否生效
) : Serializable, Diff {
    override fun sameAs(o: Any?): Boolean {
        return o is UrlMapEntity && o.newUrl == newUrl && o.oldUrl == oldUrl && o.enabled == enabled
    }

    override fun contentSameAs(o: Any?): Boolean {
        return o is UrlMapEntity && o.newUrl == newUrl && o.oldUrl == oldUrl && o.enabled == enabled
    }
}
