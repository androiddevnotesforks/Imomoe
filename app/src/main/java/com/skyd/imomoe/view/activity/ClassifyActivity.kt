package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.ClassifyTab1Bean
import com.skyd.imomoe.databinding.ActivityClassifyBinding
import com.skyd.imomoe.ext.addFitsSystemWindows
import com.skyd.imomoe.ext.collectWithLifecycle
import com.skyd.imomoe.ext.hideToolbarWhenCollapsed
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover3Proxy
import com.skyd.imomoe.view.adapter.variety.proxy.ClassifyTab1Proxy
import com.skyd.imomoe.view.listener.dsl.setOnItemSelectedListener
import com.skyd.imomoe.viewmodel.ClassifyViewModel
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ClassifyActivity : BaseActivity<ActivityClassifyBinding>() {
    private val viewModel: ClassifyViewModel by viewModels()
    private var lastRefreshTime: Long = 0L
    private val spinnerAdapter: ArrayAdapter<ClassifyBean> by lazy {
        ArrayAdapter(this, R.layout.item_spinner_item_1)
    }
    private val classifyTabAdapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(ClassifyTab1Proxy(
            onClickListener = { _, data, _ -> classifyTabClicked(data) }
        )))
    }
    private val classifyAdapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(AnimeCover3Proxy()))
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.currentPartUrl = intent.getStringExtra("partUrl").orEmpty()
        viewModel.classifyTabTitle = intent.getStringExtra("classifyTabTitle").orEmpty()
        viewModel.classifyTitle = intent.getStringExtra("classifyTitle").orEmpty()

        mBinding.run {
            tbClassifyActivity.setNavigationOnClickListener { finish() }
            ablClassifyActivity.addFitsSystemWindows(right = true, top = true)
            rvClassifyActivity.addFitsSystemWindows(right = true, bottom = true)
            srlClassifyActivity.setOnRefreshListener {
                //避免刷新间隔太短
                if (System.currentTimeMillis() - lastRefreshTime > 500) {
                    lastRefreshTime = System.currentTimeMillis()
                    if (!viewModel.classifyTabList.value.readOrNull().isNullOrEmpty())
                        viewModel.getClassifyData(viewModel.currentPartUrl)
                    else viewModel.getClassifyTabData()
                } else {
                    srlClassifyActivity.finishRefresh()
                }
            }
            srlClassifyActivity.setOnLoadMoreListener { viewModel.loadMoreClassifyData() }

            rvClassifyActivityTab.layoutManager = GridLayoutManager(
                this@ClassifyActivity, 2,
                if (resources.getBoolean(R.bool.is_landscape)) GridLayoutManager.VERTICAL
                else GridLayoutManager.HORIZONTAL, false
            )
            rvClassifyActivityTab.adapter = classifyTabAdapter

            rvClassifyActivity.layoutManager = GridLayoutManager(
                this@ClassifyActivity,
                AnimeShowSpanSize.MAX_SPAN_SIZE
            ).apply { spanSizeLookup = AnimeShowSpanSize(classifyAdapter) }
            rvClassifyActivity.addItemDecoration(AnimeShowItemDecoration())
            rvClassifyActivity.adapter = classifyAdapter

            spinnerClassifyActivity.adapter = spinnerAdapter
            spinnerClassifyActivity.setOnItemSelectedListener {
                onItemSelected { _, _, position, _ ->
                    classifyTabAdapter.dataList =
                        spinnerAdapter.getItem(position)?.classifyDataList.orEmpty()
                }
            }

            ablClassifyActivity.hideToolbarWhenCollapsed(tbClassifyActivity)
        }

        viewModel.classifyTabList.collectWithLifecycle(this) { data ->
            mBinding.srlClassifyActivity.finishRefresh()
            when (data) {
                is DataState.Success -> {
                    spinnerAdapter.clear()
                    spinnerAdapter.addAll(data.data)
                    spinnerAdapter.notifyDataSetChanged()

                    //自动选中第一个
                    if (viewModel.currentPartUrl.isEmpty() &&
                        data.data.isNotEmpty() &&
                        data.data[0].classifyDataList.size > 0
                    ) {
                        val firstItem = data.data[0].classifyDataList[0]
                        viewModel.currentPartUrl = firstItem.route
                        viewModel.classifyTabTitle = data.data[0].toString()
                        viewModel.classifyTitle = firstItem.title
                        tabSelected(viewModel.currentPartUrl)
                    } else {
                        var found = false
                        data.data.forEachIndexed { index, classifyBean ->
                            classifyBean.classifyDataList.forEach { item ->
                                if (item.route == viewModel.currentPartUrl) {
                                    mBinding.spinnerClassifyActivity.setSelection(index, true)
                                    viewModel.classifyTabTitle = classifyBean.name
                                    viewModel.classifyTitle = item.title
                                    tabSelected(viewModel.currentPartUrl)
                                    found = true
                                    return@forEachIndexed
                                }
                            }
                        }
                        if (!found) tabSelected(viewModel.currentPartUrl)
                    }
                }
                is DataState.Error -> {
                    spinnerAdapter.clear()
                    spinnerAdapter.notifyDataSetChanged()
                }
                else -> {}
            }
        }

        viewModel.classifyList.collectWithLifecycle(this) { data ->
            when (data) {
                is DataState.Success -> {
                    classifyAdapter.dataList = data.data
                    mBinding.tbClassifyActivity.title =
                        if (viewModel.classifyTabTitle.isEmpty()) "${getString(R.string.anime_classify)}  ${viewModel.classifyTitle}"
                        else "${getString(R.string.anime_classify)}  ${
                            if (viewModel.classifyTabTitle.endsWith(":") ||
                                viewModel.classifyTabTitle.endsWith("：")
                            ) {
                                viewModel.classifyTabTitle.substring(
                                    0, viewModel.classifyTabTitle.length - 1
                                )
                            } else {
                                viewModel.classifyTabTitle
                            }
                        }：${viewModel.classifyTitle}"
                }
                is DataState.Error -> {
                    classifyAdapter.dataList = emptyList()
                }
                else -> {}
            }
            mBinding.srlClassifyActivity.closeHeaderOrFooter()
        }

        viewModel.setActivity(this)

        if (viewModel.classifyTabList.value is DataState.Empty) viewModel.getClassifyTabData()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearActivity()
    }

    override fun getBinding(): ActivityClassifyBinding =
        ActivityClassifyBinding.inflate(layoutInflater)

    private fun tabSelected(partUrl: String) {
        viewModel.currentPartUrl = partUrl
        mBinding.srlClassifyActivity.autoRefresh()
    }

    private fun classifyTabClicked(data: ClassifyTab1Bean) {
        viewModel.classifyTabTitle = spinnerAdapter.getItem(
            mBinding.spinnerClassifyActivity.selectedItemPosition
        ).toString()
        viewModel.classifyTitle = data.title
        tabSelected(data.route)
    }
}
