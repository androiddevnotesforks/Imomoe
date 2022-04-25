package com.skyd.imomoe.viewmodel

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IPlayModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.compare.EpisodeTitleSort.sortEpisodeTitle
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject


@HiltViewModel
class PlayViewModel @Inject constructor(
    private val playModel: IPlayModel
) : ViewModel() {
    var playBean: PlayBean? = null
    var partUrl: String = ""
    var detailPartUrl: String = ""
        get() {
            // 如果没有传入详情页面的网址，则通过播放页面的网址计算出详情页面的网址
            return field.ifBlank {
                Util.getDetailLinkByEpisodeLink(partUrl)
            }
        }
    val animeCover: MutableStateFlow<ImageBean?> = MutableStateFlow(null)
    val playDataList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)

    // 当前播放集数的索引
    var currentEpisodeIndex = 0

    // 当前播放的集数
    val animeEpisodeDataBean = AnimeEpisodeDataBean("", "")
    val episodesList: MutableStateFlow<DataState<List<AnimeEpisodeDataBean>>> =
        MutableStateFlow(DataState.Empty)
    val playAnotherEpisodeEvent: MutableSharedFlow<Boolean> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val animeDownloadUrl: MutableSharedFlow<AnimeEpisodeDataBean> =
        MutableSharedFlow(extraBufferCapacity = 1)
    val favorite: MutableStateFlow<Boolean> = MutableStateFlow(false)

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
        val list = episodesList.value.readOrNull().orEmpty()
        if (currentEpisodeIndex + 1 in list.indices) {
            playAnotherEpisode(
                list[currentEpisodeIndex + 1].route,
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
            animeEpisodeDataBean.route = it.route.ifBlank { partUrl }
            animeEpisodeDataBean.title = it.title
            animeEpisodeDataBean.videoUrl = it.videoUrl
            playAnotherEpisodeEvent.tryEmit(true)
        }, error = {
            animeEpisodeDataBean.route = "animeEpisode1"
            animeEpisodeDataBean.title = ""
            animeEpisodeDataBean.videoUrl = ""
            playAnotherEpisodeEvent.tryEmit(false)
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        }, finish = { this.currentEpisodeIndex = currentEpisodeIndex })
    }

    fun getAnimeDownloadUrl(partUrl: String, position: Int) {
        request(request = {
            playModel.getAnimeDownloadUrl(partUrl).let {
                it ?: throw RuntimeException("getAnimeEpisodeUrlData return null")
            }
        }, success = {
            val episode = episodesList.value.readOrNull().orEmpty()[position]
            episode.videoUrl = it
            animeDownloadUrl.tryEmit(episode)
        }, error = {
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    fun getPlayData() {
        if (favorite.value == null) queryFavorite()
        request(request = {
            if (animeCover.value == null) {
                animeCover.tryEmit(playModel.getAnimeCoverImageBean(partUrl))
            }
            playModel.getPlayData(partUrl, animeEpisodeDataBean)
        }, success = {
            if (animeEpisodeDataBean.route.isBlank()) {
                animeEpisodeDataBean.route = partUrl
            }
            val list = episodesList.value.readOrNull().orEmpty().toMutableList()
            list.clear()
            list.addAll(it.second)
            list.sortEpisodeTitle()
            playBean = it.third
            episodesList.tryEmit(DataState.Success(list))
            playDataList.tryEmit(DataState.Success(it.first))
        }, error = {
            playDataList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
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
            animeCover.value.let {
                if (it == null) {
                    playModel.getAnimeCoverImageBean(partUrl).run {
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
            playModel.getAnimeCoverImageBean(partUrl)
        }, success = {
            it ?: return@request
            val cover = animeCover.value
            if (cover == null) {
                animeCover.tryEmit(ImageBean("", it.url, it.referer))
            } else {
                cover.url = it.url
                cover.referer = it.referer
            }
        }, error = {
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }


    // 查询是否追番
    fun queryFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnime(detailPartUrl)
        }, success = { favorite.tryEmit(it != null) })
    }

    // 取消追番
    fun deleteFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().deleteFavoriteAnime(detailPartUrl)
        }, success = {
            appContext.getString(R.string.remove_favorite_succeed).showToast()
            favorite.tryEmit(false)
        })
    }

    // 追番
    fun insertFavorite() {
        val cover = animeCover.value            // 番剧封面
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
                appContext.getString(R.string.favorite_succeed).showToast()
                favorite.tryEmit(true)
            })
        } else {
            appContext.getString(R.string.insert_favorite_failed_in_play_activity).showToast()
        }
    }
}