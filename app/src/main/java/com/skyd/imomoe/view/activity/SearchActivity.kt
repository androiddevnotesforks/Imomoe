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
import com.skyd.imomoe.ext.fixKeyboardFitsSystemWindows
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.hideKeyboard
import com.skyd.imomoe.ext.visible
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

        viewModel.mldSearchResultList.observe(this) {
            mBinding.srlSearchActivity.closeHeaderOrFooter()
            if (!searchHistoryListShow) {
                if (this::mLayoutCircleProgressTextTip1.isInitialized) mLayoutCircleProgressTextTip1.gone()
                if (it != null) {
                    mBinding.tvSearchActivityTip.text = getString(
                        R.string.search_activity_tip, viewModel.keyword, it.size
                    )
                    adapter.dataList = it
                }
            }
        }

        viewModel.mldLoadMoreSearchResultList.observe(this) {
            mBinding.srlSearchActivity.closeHeaderOrFooter()
            if (!searchHistoryListShow) {
                if (this::mLayoutCircleProgressTextTip1.isInitialized) mLayoutCircleProgressTextTip1.gone()
                if (it != null) {
                    mBinding.tvSearchActivityTip.text = getString(
                        R.string.search_activity_tip,
                        viewModel.keyword,
                        adapter.dataList.size + it.size
                    )
                    adapter.dataList += it
                }
            }
        }

        viewModel.mldSearchHistoryList.observe(this) {
            if (searchHistoryListShow) {
                mBinding.tvSearchActivityTip.text = getString(R.string.search_history)
                adapter.dataList = it ?: emptyList()
            }
        }

        viewModel.mldDeleteCompleted.observe(this) {
            if (searchHistoryListShow && adapter.dataList.contains(it)) adapter.dataList -= it
        }

        viewModel.mldInsertCompleted.observe(this) {
            if (searchHistoryListShow) adapter.dataList = it ?: emptyList()
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
            if (viewModel.mldSearchHistoryList.value == null) viewModel.getSearchHistoryData()
        } else {
            if (viewModel.mldSearchResultList.value == null) search(viewModel.keyword, pageNumber)
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
        adapter.dataList = viewModel.searchHistoryList.toList()
        mBinding.srlSearchActivity.setEnableLoadMore(false)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, R.anim.anl_push_top_out)
    }
}
