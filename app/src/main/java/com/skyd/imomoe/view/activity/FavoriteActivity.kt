package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityFavoriteBinding
import com.skyd.imomoe.ext.addFitsSystemWindows
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover8Proxy
import com.skyd.imomoe.viewmodel.FavoriteViewModel

class FavoriteActivity : BaseActivity<ActivityFavoriteBinding>() {
    private val viewModel: FavoriteViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy { VarietyAdapter(mutableListOf(AnimeCover8Proxy())) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.run {
            tbFavoriteActivity.setNavigationOnClickListener { finish() }
            ablFavoriteActivity.addFitsSystemWindows(right = true, top = true)

            srlFavoriteActivity.setOnRefreshListener { viewModel.getFavoriteData() }
            rvFavoriteActivity.addFitsSystemWindows(right = true, bottom = true)
            rvFavoriteActivity.layoutManager = GridLayoutManager(
                this@FavoriteActivity,
                AnimeShowSpanSize.MAX_SPAN_SIZE
            ).apply { spanSizeLookup = AnimeShowSpanSize(adapter) }
            rvFavoriteActivity.addItemDecoration(AnimeShowItemDecoration())
            rvFavoriteActivity.adapter = adapter
        }

        viewModel.favoriteList.collectWithLifecycle(this) { data ->
            when (data) {
                is DataState.Success -> {
                    mBinding.srlFavoriteActivity.isRefreshing = false
                    if (data.data.isEmpty()) showLoadFailedTip(getString(R.string.no_favorite))
                    adapter.dataList = data.data
                }
                is DataState.Refreshing -> {
                    mBinding.srlFavoriteActivity.isRefreshing = true
                }
                else -> {
                    mBinding.srlFavoriteActivity.isRefreshing = false
                }
            }
        }

    }

    override fun getBinding() = ActivityFavoriteBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutFavoriteActivityNoFavorite
}

