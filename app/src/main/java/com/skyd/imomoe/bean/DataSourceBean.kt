package com.skyd.imomoe.bean

import com.google.gson.annotations.SerializedName
import com.skyd.imomoe.view.adapter.variety.Diff
import java.io.File
import java.io.Serializable

typealias DataSource1Bean = DataSourceFileBean
typealias DataSource2Bean = DataSourceRepositoryBean

class DataSourceFileBean(
    override var route: String,
    var file: File,
    var selected: Boolean = false
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is DataSourceFileBean -> false
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
    val interfaceVersion: String?,
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
    val downloadUrl: String?
) : BaseBean, Diff {
    override fun contentSameAs(o: Any?): Boolean = when {
        o !is DataSourceRepositoryBean -> false
        route == o.route && name == o.name && interfaceVersion == o.interfaceVersion
                && versionName == o.versionName && versionCode == o.versionCode &&
                author == o.author && describe == o.describe && publicAt == o.publicAt &&
                downloadUrl == o.downloadUrl && icon == o.icon -> true
        else -> false
    }
}