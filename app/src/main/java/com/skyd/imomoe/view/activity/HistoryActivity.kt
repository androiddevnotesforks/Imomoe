package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewStub
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.materialdialogs.MaterialDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.ActivityHistoryBinding
import com.skyd.imomoe.ext.warningDialog
import com.skyd.imomoe.util.Util.getResDrawable
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

        mBinding.run {
            atbHistoryActivity.setBackButtonClickListener { finish() }

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

        mBinding.atbHistoryActivity.run {
            setButtonClickListener(0) {
                if (adapter.dataList.isEmpty()) return@setButtonClickListener
                warningDialog(
                    onPositive = { viewModel.deleteAllHistory() },
                    icon = getResDrawable(R.drawable.ic_delete_main_color_2_24_skin),
                    positiveRes = R.string.delete
                ).message(res = R.string.confirm_delete_all_watch_history).show()
            }
        }

        mBinding.srlHistoryActivity.isRefreshing = true
        viewModel.getHistoryList()
    }

    override fun getBinding(): ActivityHistoryBinding =
        ActivityHistoryBinding.inflate(layoutInflater)

    override fun getLoadFailedTipView(): ViewStub = mBinding.layoutHistoryActivityNoHistory

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        super.onChangeSkin()
        adapter.notifyDataSetChanged()
    }
}
