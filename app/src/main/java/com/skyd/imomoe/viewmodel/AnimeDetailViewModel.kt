package com.skyd.imomoe.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.*
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.ext.request
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.AnimeDetailModel
import com.skyd.imomoe.model.interfaces.IAnimeDetailModel
import com.skyd.imomoe.util.showToast


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
        queryFavorite()
        request(request = { animeDetailModel.getAnimeDetailData(partUrl) }, success = {
            cover = it.first
            title = it.second
            mldAnimeDetailList.postValue(Pair(ResponseDataType.REFRESH, it.third))
            refreshAnimeCover()     // 更新数据库中番剧封面地址
        }, error = {
            mldAnimeDetailList.postValue(Pair(ResponseDataType.FAILED, ArrayList()))
            "${App.context.getString(R.string.get_data_failed)}\n${it.message}".showToast()
        })
    }

    // 查询是否追番
    fun queryFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().getFavoriteAnime(partUrl)
        }, success = { mldFavorite.postValue(it != null) })
    }

    // 取消追番
    fun deleteFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().deleteFavoriteAnime(partUrl)
        }, success = {
            App.context.getString(R.string.remove_favorite_succeed).showToast()
            mldFavorite.postValue(false)
        })
    }

    // 追番
    fun insertFavorite() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().insertFavoriteAnime(
                FavoriteAnimeBean(
                    Const.ViewHolderTypeString.ANIME_COVER_8, "",
                    partUrl,
                    title,
                    System.currentTimeMillis(),
                    cover
                )
            )
        }, success = {
            App.context.getString(R.string.favorite_succeed).showToast()
            mldFavorite.postValue(true)
        })
    }

    fun refreshAnimeCover() {
        request(request = {
            getAppDataBase().favoriteAnimeDao().updateFavoriteAnimeCover(partUrl, cover)
        })
    }
}