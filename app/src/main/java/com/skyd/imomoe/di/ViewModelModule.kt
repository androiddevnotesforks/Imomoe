package com.skyd.imomoe.di

import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.model.impls.*
import com.skyd.imomoe.model.interfaces.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object ViewModelModule {
    @Provides
    fun provideAnimeDetailModel(): IAnimeDetailModel {
        return DataSourceManager.create(IAnimeDetailModel::class.java) ?: AnimeDetailModel()
    }

    @Provides
    fun provideAnimeShowModel(): IAnimeShowModel {
        return DataSourceManager.create(IAnimeShowModel::class.java) ?: AnimeShowModel()
    }

    @Provides
    fun provideClassifyModel(): IClassifyModel {
        return DataSourceManager.create(IClassifyModel::class.java) ?: ClassifyModel()
    }

    @Provides
    fun provideConst(): IConst {
        return DataSourceManager.getConst() ?: Const()
    }

    @Provides
    fun provideEverydayAnimeModel(): IEverydayAnimeModel {
        return DataSourceManager.create(IEverydayAnimeModel::class.java) ?: EverydayAnimeModel()
    }

    @Provides
    fun provideEverydayAnimeWidgetModel(): IEverydayAnimeWidgetModel {
        return DataSourceManager.create(IEverydayAnimeWidgetModel::class.java)
            ?: EverydayAnimeWidgetModel()
    }

    @Provides
    fun provideHomeModel(): IHomeModel {
        return DataSourceManager.create(IHomeModel::class.java) ?: HomeModel()
    }

    @Provides
    fun provideMonthAnimeModel(): IMonthAnimeModel {
        return DataSourceManager.create(IMonthAnimeModel::class.java) ?: MonthAnimeModel()
    }

    @Provides
    fun providePlayModel(): IPlayModel {
        return DataSourceManager.create(IPlayModel::class.java) ?: PlayModel()
    }

    @Provides
    fun provideRankListModel(): IRankListModel {
        return DataSourceManager.create(IRankListModel::class.java) ?: RankListModel()
    }

    @Provides
    fun provideRankModel(): IRankModel {
        return DataSourceManager.create(IRankModel::class.java) ?: RankModel()
    }

    @Provides
    fun provideRouter(): IRouter {
        return DataSourceManager.getRouter() ?: Router()
    }

    @Provides
    fun provideSearchModel(): ISearchModel {
        return DataSourceManager.create(ISearchModel::class.java) ?: SearchModel()
    }

    @Provides
    fun provideUtil(): IUtil {
        return DataSourceManager.getUtil() ?: Util()
    }
}