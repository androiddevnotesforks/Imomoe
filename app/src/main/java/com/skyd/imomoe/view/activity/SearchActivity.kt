package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SearchHistoryBean
import com.skyd.imomoe.databinding.ActivitySearchBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover3Proxy
import com.skyd.imomoe.view.adapter.variety.proxy.SearchHistory1Proxy
import com.skyd.imomoe.view.adapter.variety.proxy.SearchHistoryHeader1Proxy
import com.skyd.imomoe.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : BaseActivity<ActivitySearchBinding>() {
    private lateinit var mLayoutCircleProgressTextTip1: RelativeLayout
    private lateinit var tvCircleProgressTextTip1: TextView
    private val viewModel: SearchViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(
            mutableListOf(AnimeCover3Proxy(), SearchHistoryHeader1Proxy(), SearchHistory1Proxy(
                onClickListener = { _, data, _ -> search(data.title) },
                onDeleteButtonClickListener = { _, data, _ -> viewModel.deleteSearchHistory(data) }
            ))
        )
    }
    private var searchHistoryListShow: Boolean = true
    private var lastRefreshTime: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val pageNumber = intent.getStringExtra("pageNumber").orEmpty()
        viewModel.keyword = intent.getStringExtra("keyword").orEmpty()

        mBinding.run {
            srlSearchActivity.setEnableRefresh(false)
            srlSearchActivity.setOnLoadMoreListener { viewModel.loadMoreSearchData() }

            rvSearchActivity.layoutManager = LinearLayoutManager(this@SearchActivity)
            rvSearchActivity.adapter = adapter
            showSearchHistory()

            tbSearchActivity.fixKeyboardFitsSystemWindows()
            svSearchActivity.isSubmitButtonEnabled = true
            svSearchActivity.requestFocus()
            svSearchActivity.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return if (query.isNullOrBlank()) {
                        getString(R.string.search_input_keywords_tips).showToast()
                        false
                    } else {
                        // 避免刷新间隔太短
                        if (System.currentTimeMillis() - lastRefreshTime > 500) {
                            lastRefreshTime = System.currentTimeMillis()
                            search(query)
                            svSearchActivity.hideKeyboard()
                            true
                        } else {
                            false
                        }
                    }
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    if (this@SearchActivity::mLayoutCircleProgressTextTip1.isInitialized)
                        mLayoutCircleProgressTextTip1.gone()
                    if (newText.isNullOrEmpty()) {
                        tvSearchActivityTip.text = getString(R.string.search_history)
                        showSearchHistory()
                    }
                    return true
                }
            })
        }

        viewModel.searchResultList.collectWithLifecycle(this) { data ->
            mBinding.srlSearchActivity.closeHeaderOrFooter()
            when (data) {
                is DataState.Success -> {
                    if (!searchHistoryListShow) {
                        if (this@SearchActivity::mLayoutCircleProgressTextTip1.isInitialized) {
                            mLayoutCircleProgressTextTip1.gone()
                        }
                        mBinding.tvSearchActivityTip.text = getString(
                            R.string.search_activity_tip, viewModel.keyword, data.data.size
                        )
                        adapter.dataList = data.data
                    }
                }
                else -> {
                    adapter.dataList = emptyList()
                }
            }
        }

        viewModel.searchHistoryList.collectWithLifecycle(this) { data ->
            when (data) {
                is DataState.Success -> {
                    if (searchHistoryListShow) {
                        mBinding.tvSearchActivityTip.text = getString(R.string.search_history)
                        adapter.dataList = data.data
                    }
                }
                else -> {
                    adapter.dataList = emptyList()
                }
            }
        }

        viewModel.deleteCompleted.collectWithLifecycle(this) { data ->
            if (searchHistoryListShow && data != null && adapter.dataList.contains(data)) {
                adapter.dataList -= data
            }
        }

        mBinding.tbSearchActivity.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_item_search_activity_close -> {
                    finish()
                    true
                }
                else -> false
            }
        }

        if (viewModel.keyword.isBlank()) {
            if (viewModel.searchHistoryList.value is DataState.Empty) {
                viewModel.getSearchHistoryData()
            }
        } else {
            if (viewModel.searchResultList.value is DataState.Empty) {
                search(viewModel.keyword, pageNumber)
            }
        }
    }

    override fun getBinding() = ActivitySearchBinding.inflate(layoutInflater)

    fun search(key: String, partUrl: String = "") {
        //setText一定要在加载布局之前，否则progressbar会被gone掉
        mBinding.run {
            svSearchActivity.setQuery(key, false)
            if (this@SearchActivity::tvCircleProgressTextTip1.isInitialized) {
                mLayoutCircleProgressTextTip1.visible()
            } else {
                mLayoutCircleProgressTextTip1 =
                    layoutSearchActivityLoading.inflate() as RelativeLayout
                tvCircleProgressTextTip1 =
                    mLayoutCircleProgressTextTip1.findViewById(R.id.tv_circle_progress_text_tip_1)
            }
            if (this@SearchActivity::tvCircleProgressTextTip1.isInitialized) tvCircleProgressTextTip1.gone()
            showSearchResult()
            adapter.dataList = emptyList()
        }
        viewModel.insertSearchHistory(
            SearchHistoryBean("", System.currentTimeMillis(), key)
        )
        viewModel.getSearchData(key, partUrl = partUrl)
    }

    private fun showSearchResult() {
        searchHistoryListShow = false
        adapter.dataList = emptyList()
        mBinding.srlSearchActivity.setEnableLoadMore(true)
    }

    private fun showSearchHistory() {
        searchHistoryListShow = true
        adapter.dataList = viewModel.searchHistoryList.value.readOrNull().orEmpty().toList()
        mBinding.srlSearchActivity.setEnableLoadMore(false)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_top_out)
    }
}
