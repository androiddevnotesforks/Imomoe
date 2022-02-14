package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.PageNumberBean
import com.skyd.imomoe.model.interfaces.ISearchModel

class SearchModel : ISearchModel {
    override suspend fun getSearchData(
        keyWord: String,
        partUrl: String
    ): Pair<ArrayList<Any>, PageNumberBean?> {
        return Pair(ArrayList(), null)
    }
}