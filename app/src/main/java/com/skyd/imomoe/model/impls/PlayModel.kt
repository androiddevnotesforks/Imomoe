package com.skyd.imomoe.model.impls

import android.app.Activity
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.model.interfaces.IPlayModel

class PlayModel : IPlayModel {

    override suspend fun getPlayData(
        partUrl: String,
        animeEpisodeDataBean: AnimeEpisodeDataBean
    ): Triple<ArrayList<Any>, ArrayList<AnimeEpisodeDataBean>, PlayBean> {
        return Triple(
            ArrayList(), ArrayList(), PlayBean(
                "",
                AnimeTitleBean("", ""),
                AnimeEpisodeDataBean("", ""),
                ArrayList()
            )
        )
    }

    override suspend fun playAnotherEpisode(partUrl: String): AnimeEpisodeDataBean? {
        return null
    }

    override suspend fun getAnimeCoverImageBean(partUrl: String): ImageBean? {
        return null
    }

    override fun setActivity(activity: Activity) {
    }

    override fun clearActivity() {
    }

    override suspend fun getAnimeDownloadUrl(partUrl: String): String? {
        return null
    }

}