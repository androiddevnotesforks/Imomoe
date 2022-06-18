package com.skyd.imomoe.bean

import com.google.gson.annotations.SerializedName
import com.skyd.imomoe.view.adapter.variety.Diff
import java.io.File
import java.io.Serializable

typealias DataSource2Bean = DataSourceRepositoryBean

class DataSource1Bean(
    override var route: String,
    var file: File,
    var selected: Boolean = false,
    var name: String,
    var versionCode: Int? = null,
    var versionName: String? = null,
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is DataSource1Bean -> false
        route == o.route && file == o.file && selected == o.selected -> true
        else -> false
    }
}

class DataSourceRepositoryBeanWrapper(
    @SerializedName("dataSourceList")
    val dataSourceList: List<DataSourceRepositoryBean>
) : Serializable

class DataSourceRepositoryBean(
    override var route: String,
    @SerializedName("name")
    val name: String?,
    @SerializedName("interfaceVersion")
    var interfaceVersion: String?,
    @SerializedName("versionName")
    val versionName: String?,
    @SerializedName("versionCode")
    val versionCode: Int,
    @SerializedName("author")
    val author: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("describe")
    val describe: String?,
    @SerializedName("publicAt")
    val publicAt: Long,      // 发布时间戳
    @SerializedName("downloadUrl")
    val downloadUrl: String?,
    var status: Status = Status.NONE
) : BaseBean, Diff, Cloneable {
    override fun sameAs(o: Any?): Boolean {
        return o is DataSourceRepositoryBean && name == o.name && interfaceVersion == o.interfaceVersion
    }

    override fun contentSameAs(o: Any?): Boolean = when {
        o !is DataSourceRepositoryBean -> false
        route == o.route && name == o.name && interfaceVersion == o.interfaceVersion
                && versionName == o.versionName && versionCode == o.versionCode &&
                author == o.author && describe == o.describe && publicAt == o.publicAt &&
                downloadUrl == o.downloadUrl && icon == o.icon && status == o.status -> true
        else -> false
    }

    override fun diff(o: Any?): Any? {
        if (o !is DataSourceRepositoryBean) return null

        val list: MutableList<Any> = mutableListOf()
        if (status != o.status) list += STATUS
        return list.ifEmpty { null }
    }

    public override fun clone(): Any {
        return super.clone()
    }

    enum class Status {
        NONE,       // 未安装过
        OUTDATED,   // 有新版本
        NEWEST,     // 已经是最新版本
        DOWNLOADING,// 正在下载中
        INSTALLING  // 正在安装中
    }

    companion object {
        const val STATUS = "status"
    }
}