package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.AnimeDetailModel
import com.skyd.imomoe.model.interfaces.IAnimeDetailModel
import com.skyd.imomoe.util.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class AnimeDetailViewModel : ViewModel() {
    private val animeDetailModel: IAnimeDetailModel by lazy {
        DataSourceManager.create(IAnimeDetailModel::class.java) ?: AnimeDetailModel()
    }
    var cover: ImageBean = ImageBean("", "", "", "")
    var title: String = ""
    var animeDetailList: MutableList<IAnimeDetailBean> = ArrayList()
    var mldAnimeDetailList: MutableLiveData<Pair<ResponseDataType, MutableList<IAnimeDetailBean>>> =
        MutableLiveData()
    var partUrl: String = ""
    var mldFavorite: MutableLiveData<Boolean> = MutableLiveData()

    fun getAnimeDetailData() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                queryFavorite()
                animeDetailModel.getAnimeDetailData(partUrl).apply {
                    cover = first
                    title = second
                    mldAnimeDetailList.postValue(Pair(ResponseDataType.REFRESH, third))
                }
                refreshAnimeCover()     // 更新数据库中番剧封面地址
            } catch (e: Exception) {
                mldAnimeDetailList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
                e.printStackTrace()
                (App.context.getString(R.string.get_data_failed) + "\n" + e.message).showToast()
            }
        }
    }

    // 查询是否追番
    fun queryFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val favoriteAnime = getAppDataBase().favoriteAnimeDao().getFavoriteAnime(partUrl)
                mldFavorite.postValue(favoriteAnime != null)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 取消追番
    fun deleteFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAppDataBase().favoriteAnimeDao().deleteFavoriteAnime(partUrl)
                App.context.getString(R.string.remove_favorite_succeed).showToast()
                mldFavorite.postValue(false)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 追番
    fun insertFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAppDataBase().favoriteAnimeDao().insertFavoriteAnime(
                    FavoriteAnimeBean(
                        Const.ViewHolderTypeString.ANIME_COVER_8, "",
                        partUrl,
                        title,
                        System.currentTimeMillis(),
                        cover
                    )
                )
                App.context.getString(R.string.favorite_succeed).showToast()
                mldFavorite.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun refreshAnimeCover() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                getAppDataBase().favoriteAnimeDao().updateFavoriteAnimeCover(partUrl, cover)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        const val TAG = "AnimeDetailViewModel"
    }
}