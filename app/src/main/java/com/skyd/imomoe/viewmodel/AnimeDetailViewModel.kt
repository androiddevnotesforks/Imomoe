package com.skyd.imomoe.viewmodel

import androidx.lifecycle.ViewModel
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.bean.FavoriteAnimeBean
import com.skyd.imomoe.bean.ImageBean
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.interfaces.IAnimeDetailModel
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class AnimeDetailViewModel @Inject constructor(
    private val animeDetailModel: IAnimeDetailModel
) : ViewModel() {
    var cover: ImageBean = ImageBean("", "", "")
    var title: String = ""
    var animeDetailList: MutableStateFlow<DataState<List<Any>>> = MutableStateFlow(DataState.Empty)
    var partUrl: String = ""
    var favorite: MutableStateFlow<Boolean> = MutableStateFlow(false)

    fun getAnimeDetailData() {
        queryFavorite()
        request(request = { animeDetailModel.getAnimeDetailData(partUrl) }, success = {
            cover = it.first
            title = it.second
            animeDetailList.tryEmit(DataState.Success(it.third))
            refreshAnimeCover()     // 更新数据库中番剧封面地址
        }, error = {
            animeDetailList.tryEmit(DataState.Error(it.message.orEmpty()))
            "${appContext.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    // 查询是否追番
    fun queryFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnime(partUrl)
        }, success = { favorite.tryEmit(it != null) })
    }

    // 取消追番
    fun deleteFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().deleteFavoriteAnime(partUrl)
        }, success = {
            appContext.getString(R.string.remove_favorite_succeed).showToast()
            favorite.tryEmit(false)
        })
    }

    // 追番
    fun insertFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().insertFavoriteAnime(
                FavoriteAnimeBean(
                    "",
                    partUrl,
                    title,
                    System.currentTimeMillis(),
                    cover
                )
            )
        }, success = {
            appContext.getString(R.string.favorite_succeed).showToast()
            favorite.tryEmit(true)
        })
    }

    fun refreshAnimeCover() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().updateFavoriteAnimeCover(partUrl, cover)
        })
    }
}