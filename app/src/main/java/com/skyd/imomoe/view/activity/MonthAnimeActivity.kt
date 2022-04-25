package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityMonthAnimeBinding
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover3Proxy
import com.skyd.imomoe.viewmodel.MonthAnimeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MonthAnimeActivity : BaseActivity<ActivityMonthAnimeBinding>() {
    private val viewModel: MonthAnimeViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy { VarietyAdapter(mutableListOf(AnimeCover3Proxy())) }
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.partUrl = intent.getStringExtra("partUrl").orEmpty()

        mBinding.run {
            tbMonthAnimeActivity.title = getString(R.string.year_month_anime, viewModel.partUrl)
            tbMonthAnimeActivity.setNavigationOnClickListener { finish() }

            rvMonthAnimeActivity.layoutManager = GridLayoutManager(this@MonthAnimeActivity, 4)
                .apply { spanSizeLookup = AnimeShowSpanSize(adapter) }
            rvMonthAnimeActivity.adapter = adapter

            srlMonthAnimeActivity.setOnRefreshListener { //避免刷新间隔太短
                if (System.currentTimeMillis() - lastRefreshTime > 500) {
                    lastRefreshTime = System.currentTimeMillis()
                    viewModel.getMonthAnimeData(viewModel.partUrl)
                } else {
                    srlMonthAnimeActivity.closeHeaderOrFooter()
                }
            }
            srlMonthAnimeActivity.setOnLoadMoreListener { viewModel.loadMoreMonthAnimeData() }
        }

        viewModel.monthAnimeList.collectWithLifecycle(this) { data ->
            when (data) {
                is DataState.Empty -> mBinding.srlMonthAnimeActivity.autoRefresh()
                is DataState.Success -> {
                    hideLoadFailedTip()
                    mBinding.srlMonthAnimeActivity.closeHeaderOrFooter()
                    adapter.dataList = data.data
                }
                is DataState.Error -> {
                    adapter.dataList = emptyList()
                    showLoadFailedTip {
                        viewModel.getMonthAnimeData(viewModel.partUrl)
                    }
                    mBinding.srlMonthAnimeActivity.closeHeaderOrFooter()
                }
                else -> {}
            }
        }
    }

    override fun getBinding() = ActivityMonthAnimeBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutMonthAnimeActivityLoadFailed
}
