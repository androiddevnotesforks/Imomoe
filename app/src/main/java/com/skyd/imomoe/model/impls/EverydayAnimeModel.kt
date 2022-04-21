package com.skyd.imomoe.model.impls

import com.skyd.imomoe.bean.TabBean
import com.skyd.imomoe.model.interfaces.IEverydayAnimeModel

class EverydayAnimeModel : IEverydayAnimeModel {
    override suspend fun getEverydayAnimeData(): Triple<ArrayList<TabBean>, ArrayList<List<Any>>, String> {
        return Triple(ArrayList(), ArrayList(), "")
    }
}