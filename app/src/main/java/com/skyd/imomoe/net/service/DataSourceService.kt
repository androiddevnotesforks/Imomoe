package com.skyd.imomoe.net.service

import com.skyd.imomoe.bean.DataSourceRepositoryBeanWrapper
import com.skyd.imomoe.config.Api
import retrofit2.http.GET
import retrofit2.http.Url

interface DataSourceService {
    @GET
    suspend fun getDataSourceJson(@Url url: String = Api.DATA_SOURCE_JSON_URL): DataSourceRepositoryBeanWrapper
}