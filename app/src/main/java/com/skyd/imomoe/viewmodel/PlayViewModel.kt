package com.skyd.imomoe.viewmodel

import android.app.Activity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Const.ViewHolderTypeString
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.PlayModel
import com.skyd.imomoe.model.interfaces.IPlayModel
import com.skyd.imomoe.util.showToast


class PlayViewModel : ViewModel() {
    private val playModel: IPlayModel by lazy {
        DataSourceManager.create(IPlayModel::class.java) ?: PlayModel()
    }
    var playBean: PlayBean? = null
    var partUrl: String = ""
    var animeCover: ImageBean = ImageBean("", "", "", "")
    var mldAnimeCover: MutableLiveData<Boolean> = MutableLiveData()
    var mldPlayBean: MutableLiveData<PlayBean> = MutableLiveData()
    var playBeanDataList: MutableList<IAnimeDetailBean> = ArrayList()
    val episodesList: MutableList<AnimeEpisodeDataBean> = ArrayList()
    var currentEpisodeIndex = 0
    val mldEpisodesList: MutableLiveData<Boolean> = MutableLiveData()
    val animeEpisodeDataBean = AnimeEpisodeDataBean("animeEpisode1", "", "")
    val mldAnimeEpisodeDataRefreshed: MutableLiveData<Boolean> = MutableLiveData()
    val mldGetAnimeEpisodeData: MutableLiveData<Int> = MutableLiveData()

    fun setActivity(activity: Activity) {
        playModel.setActivity(activity)
    }

    fun clearActivity() {
        playModel.clearActivity()
    }

    fun refreshAnimeEpisodeData(partUrl: String, currentEpisodeIndex: Int, title: String = "") {
        this@PlayViewModel.partUrl = partUrl
        request(request = {
            playModel.refreshAnimeEpisodeData(partUrl, animeEpisodeDataBean).also {
                if (!it) throw RuntimeException("html play class not found")
            }
        }, success = {
            if (it) {
                animeEpisodeDataBean.title = title
                mldAnimeEpisodeDataRefreshed.postValue(true)
            }
        }, error = {
            animeEpisodeDataBean.actionUrl = "animeEpisode1"
            animeEpisodeDataBean.title = ""
            animeEpisodeDataBean.videoUrl = ""
            mldAnimeEpisodeDataRefreshed.postValue(false)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        }, finish = { this.currentEpisodeIndex = currentEpisodeIndex })
    }

    fun getAnimeEpisodeUrlData(partUrl: String, position: Int) {
        request(request = {
            playModel.getAnimeEpisodeUrlData(partUrl).let {
                it ?: throw RuntimeException("getAnimeEpisodeUrlData return null")
            }
        }, success = {
            episodesList[position].videoUrl = it
            mldEpisodesList.postValue(true)
            mldGetAnimeEpisodeData.postValue(position)
        }, error = {
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun getPlayData(partUrl: String) {
        this@PlayViewModel.partUrl = partUrl
        request(request = { playModel.getPlayData(partUrl, animeEpisodeDataBean) }, success = {
            playBeanDataList.clear()
            episodesList.clear()
            playBeanDataList.addAll(it.first)
            episodesList.addAll(it.second)
            playBean = it.third
            mldPlayBean.postValue(playBean)
            mldEpisodesList.postValue(true)
        }, error = {
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    // 更新追番集数数据
    fun updateFavoriteData(
        detailPartUrl: String,
        lastEpisodeUrl: String,
        lastEpisode: String,
        time: Long
    ) {
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnime(detailPartUrl)
        }, success = {
            it ?: return@request
            it.lastEpisode = lastEpisode
            it.lastEpisodeUrl = lastEpisodeUrl
            it.time = time
            request({ getAppDataBase().favoriteAnimeDao().updateFavoriteAnime(it) })
        })
    }

    // 插入观看历史记录
    fun insertHistoryData(detailPartUrl: String) {
        request(request = {
            if (animeCover.url.isBlank()) {
                playModel.getAnimeCoverImageBean(detailPartUrl).run {
                    this ?: return@run null
                    HistoryBean(
                        ViewHolderTypeString.ANIME_COVER_9, "", detailPartUrl,
                        playBean?.title?.title ?: "",
                        System.currentTimeMillis(),
                        this,
                        partUrl,
                        animeEpisodeDataBean.title
                    )
                }
            } else {
                HistoryBean(
                    ViewHolderTypeString.ANIME_COVER_9, "", detailPartUrl,
                    playBean?.title?.title ?: "",
                    System.currentTimeMillis(),
                    animeCover,
                    partUrl,
                    animeEpisodeDataBean.title
                )
            }
        }, success = {
            it ?: return@request
            request(request = { getAppDataBase().historyDao().insertHistory(it) })
        })
    }

    fun getAnimeCoverImageBean(detailPartUrl: String) {
        request(request = {
            playModel.getAnimeCoverImageBean(detailPartUrl)
        }, success = {
            it ?: return@request
            animeCover.url = it.url
            animeCover.referer = it.referer
            mldAnimeCover.postValue(true)
        }, error = {
            mldAnimeCover.postValue(false)
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }
}