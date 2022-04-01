package com.skyd.imomoe.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.PlayModel
import com.skyd.imomoe.model.interfaces.IPlayModel
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.compare.EpisodeTitleSort.sortEpisodeTitle


class PlayViewModel : ViewModel() {
    private val playModel: IPlayModel by lazy {
        DataSourceManager.create(IPlayModel::class.java) ?: PlayModel()
    }
    var playBean: PlayBean? = null
    var partUrl: String = ""
    var detailPartUrl: String = ""
        set(value) {
            // 如果没有传入详情页面的网址，则通过播放页面的网址计算出详情页面的网址
            field = if (value.isBlank() ||
                value == (DataSourceManager.getConst()
                    ?: com.skyd.imomoe.model.impls.Const()).actionUrl.ANIME_DETAIL() &&
                partUrl.isNotBlank()
            ) {
                Util.getDetailLinkByEpisodeLink(partUrl)
            } else {
                value
            }
        }
    var animeCover: ImageBean? = null
    var mldAnimeCover: MutableLiveData<Boolean> = MutableLiveData()
    var mldPlayDataList: MutableLiveData<List<Any>?> = MutableLiveData()

    // 当前播放集数的索引
    var currentEpisodeIndex = 0

    // 当前播放的集数
    val animeEpisodeDataBean = AnimeEpisodeDataBean("", "")
    val episodesList: MutableList<AnimeEpisodeDataBean> = ArrayList()
    val mldEpisodesList: MutableLiveData<Boolean> = MutableLiveData()
    val mldPlayAnotherEpisode: MutableLiveData<Boolean> = MutableLiveData()
    val mldAnimeDownloadUrl: MutableLiveData<AnimeEpisodeDataBean> = MutableLiveData()
    val mldFavorite: MutableLiveData<Boolean> = MutableLiveData()

    fun setActivity(activity: Activity) {
        playModel.setActivity(activity)
    }

    fun clearActivity() {
        playModel.clearActivity()
    }

    /**
     * @return true if has next episode, false else.
     */
    fun playNextEpisode(): Boolean {
        if (currentEpisodeIndex + 1 in episodesList.indices) {
            playAnotherEpisode(
                episodesList[currentEpisodeIndex + 1].actionUrl,
                currentEpisodeIndex + 1
            )
            return true
        }
        return false
    }

    // 播放另一集（页面切换到另一集，因此partUrl要更新）
    fun playAnotherEpisode(partUrl: String, currentEpisodeIndex: Int) {
        this.partUrl = partUrl
        request(request = {
            playModel.playAnotherEpisode(partUrl).let {
                it ?: throw RuntimeException("html play class not found")
            }
        }, success = {
            animeEpisodeDataBean.actionUrl = it.actionUrl.ifBlank { partUrl }
            animeEpisodeDataBean.title = it.title
            animeEpisodeDataBean.videoUrl = it.videoUrl
            mldPlayAnotherEpisode.postValue(true)
        }, error = {
            animeEpisodeDataBean.actionUrl = "animeEpisode1"
            animeEpisodeDataBean.title = ""
            animeEpisodeDataBean.videoUrl = ""
            mldPlayAnotherEpisode.postValue(false)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        }, finish = { this.currentEpisodeIndex = currentEpisodeIndex })
    }

    fun getAnimeDownloadUrl(partUrl: String, position: Int) {
        request(request = {
            playModel.getAnimeDownloadUrl(partUrl).let {
                it ?: throw RuntimeException("getAnimeEpisodeUrlData return null")
            }
        }, success = {
            episodesList[position].videoUrl = it
            mldEpisodesList.postValue(true)
            mldAnimeDownloadUrl.postValue(episodesList[position])
        }, error = {
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun getPlayData() {
        if (mldFavorite.value == null) queryFavorite()
        if (animeCover == null) getAnimeCoverImageBean()
        request(request = { playModel.getPlayData(partUrl, animeEpisodeDataBean) }, success = {
            if (animeEpisodeDataBean.actionUrl.isBlank()) {
                animeEpisodeDataBean.actionUrl = partUrl
            }
            episodesList.clear()
            episodesList.addAll(it.second)
            episodesList.sortEpisodeTitle()
            playBean = it.third
            mldPlayDataList.postValue(it.first)
            mldEpisodesList.postValue(true)
        }, error = {
            mldPlayDataList.postValue(null)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    // 更新追番集数数据
    fun updateFavoriteData() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnime(detailPartUrl)
        }, success = {
            it ?: return@request
            it.lastEpisode = animeEpisodeDataBean.title
            it.lastEpisodeUrl = partUrl
            it.time = System.currentTimeMillis()
            request({ getAppDataBase().favoriteAnimeDao().updateFavoriteAnime(it) })
        })
    }

    // 插入观看历史记录
    fun insertHistoryData() {
        request(request = {
            animeCover.let {
                if (it == null) {
                    playModel.getAnimeCoverImageBean(detailPartUrl).run {
                        val cover = this ?: ImageBean("", "", "")
                        HistoryBean(
                            "", detailPartUrl,
                            playBean?.title?.title ?: "",
                            System.currentTimeMillis(),
                            cover,
                            partUrl,
                            animeEpisodeDataBean.title
                        )
                    }
                } else {
                    HistoryBean(
                        "", detailPartUrl,
                        playBean?.title?.title ?: "",
                        System.currentTimeMillis(),
                        it,
                        partUrl,
                        animeEpisodeDataBean.title
                    )
                }
            }
        }, success = {
            request(request = { getAppDataBase().historyDao().insertHistory(it) })
        })
    }

    fun getAnimeCoverImageBean() {
        request(request = {
            playModel.getAnimeCoverImageBean(detailPartUrl)
        }, success = {
            it ?: return@request
            val cover = animeCover
            if (cover == null) {
                animeCover = ImageBean("", it.url, it.referer)
            } else {
                cover.url = it.url
                cover.referer = it.referer
            }
            mldAnimeCover.postValue(true)
        }, error = {
            mldAnimeCover.postValue(false)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }


    // 查询是否追番
    fun queryFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnime(detailPartUrl)
        }, success = { mldFavorite.postValue(it != null) })
    }

    // 取消追番
    fun deleteFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().deleteFavoriteAnime(detailPartUrl)
        }, success = {
            App.context.getString(R.string.remove_favorite_succeed).showToast()
            mldFavorite.postValue(false)
        })
    }

    // 追番
    fun insertFavorite() {
        val cover = animeCover                  // 番剧封面
        val title = playBean?.title?.title      // 番剧名，非集数名
        if (cover != null && title != null) {
            request(request = {
                getAppDataBase().favoriteAnimeDao().insertFavoriteAnime(
                    FavoriteAnimeBean(
                        "",
                        detailPartUrl,
                        title,
                        System.currentTimeMillis(),
                        cover,
                        lastEpisodeUrl = partUrl,
                        lastEpisode = animeEpisodeDataBean.title
                    )
                )
            }, success = {
                App.context.getString(R.string.favorite_succeed).showToast()
                mldFavorite.postValue(true)
            })
        } else {
            App.context.getString(R.string.insert_favorite_failed_in_play_activity).showToast()
        }
    }
}