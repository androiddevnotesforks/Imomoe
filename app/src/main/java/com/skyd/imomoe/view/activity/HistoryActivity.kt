package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.ViewStub
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityHistoryBinding
import com.skyd.imomoe.ext.showMessageDialog
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover9Proxy
import com.skyd.imomoe.viewmodel.HistoryViewModel

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {
    private val viewModel: HistoryViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(AnimeCover9Proxy(
            onDeleteButtonClickListener = { _, data, _ -> viewModel.deleteHistory(data) }
        )))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mBinding.tbHistoryActivity.also {
            it.setNavigationOnClickListener { finish() }
            it.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item_history_activity_delete_all -> {
                        if (adapter.dataList.isEmpty()) return@setOnMenuItemClickListener true
                        showMessageDialog(
                            onPositive = { _, _ -> viewModel.deleteAllHistory() },
                            icon = R.drawable.ic_delete_24,
                            positiveText = getString(R.string.delete),
                            message = getString(R.string.confirm_delete_all_watch_history)
                        )
                        true
                    }
                    else -> false
                }
            }
        }

        mBinding.run {
            srlHistoryActivity.setOnRefreshListener { viewModel.getHistoryList() }

            rvHistoryActivity.layoutManager = LinearLayoutManager(this@HistoryActivity)
            rvHistoryActivity.adapter = adapter
        }

        viewModel.mldHistoryList.observe(this) {
            mBinding.srlHistoryActivity.isRefreshing = false
            if (it != null) {
                if (it.isEmpty()) showLoadFailedTip(getString(R.string.no_history))
                adapter.dataList = it
            }
        }

        viewModel.mldDeleteHistory.observe(this) {
            if (it != null) {
                adapter.dataList.let { list ->
                    if (list.contains(it) && list.size == 1) {
                        showLoadFailedTip(getString(R.string.no_history))
                    }
                    adapter.dataList -= it
                }
            }
        }

        viewModel.mldDeleteAllHistory.observe(this) {
            showLoadFailedTip(getString(R.string.no_history))
            adapter.dataList = emptyList()
        }

        mBinding.srlHistoryActivity.isRefreshing = true
        if (viewModel.mldHistoryList.value == null) viewModel.getHistoryList()
    }

    override fun getBinding() = ActivityHistoryBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutHistoryActivityNoHistory
}
