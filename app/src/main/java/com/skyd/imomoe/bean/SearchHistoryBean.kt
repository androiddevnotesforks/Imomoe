package com.skyd.imomoe.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.view.adapter.variety.Diff

typealias SearchHistory1Bean = SearchHistoryBean

@Entity(tableName = Const.Database.AppDataBase.SEARCH_HISTORY_TABLE_NAME)
class SearchHistoryBean(
    @ColumnInfo(name = "actionUrl")
    override var actionUrl: String,
    @PrimaryKey
    @ColumnInfo(name = "id")
    var timeStamp: Long,        //时间戳作为主键
    @ColumnInfo(name = "title")
    var title: String
) : BaseBean, Diff {
    override fun equals(other: Any?): Boolean {
        if (other is SearchHistoryBean) {
            return other.title == title
        }
        return false
    }

    override fun contentSameAs(o: Any?): Boolean = when {
        o !is SearchHistoryBean -> false
        actionUrl == o.actionUrl && timeStamp == o.timeStamp && title == o.title -> true
        else -> false
    }

    override fun hashCode(): Int {
        var result = actionUrl.hashCode()
        result = 31 * result + timeStamp.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }
}
