package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.ClassifyBean
import com.skyd.imomoe.bean.ClassifyTab1Bean
import com.skyd.imomoe.databinding.ActivityClassifyBinding
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeCover3Proxy
import com.skyd.imomoe.view.adapter.variety.proxy.ClassifyTab1Proxy
import com.skyd.imomoe.view.listener.dsl.setOnItemSelectedListener
import com.skyd.imomoe.viewmodel.ClassifyViewModel


class ClassifyActivity : BaseActivity<ActivityClassifyBinding>() {
    private val viewModel: ClassifyViewModel by viewModels()
    private var lastRefreshTime: Long = System.currentTimeMillis() - 500
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
    private var classifyTabTitle: String = ""       //如 地区
    private var classifyTitle: String = ""          //如 大陆
    private var currentPartUrl: String = ""

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentPartUrl = intent.getStringExtra("partUrl").orEmpty()
        classifyTabTitle = intent.getStringExtra("classifyTabTitle").orEmpty()
        classifyTitle = intent.getStringExtra("classifyTitle").orEmpty()

        mBinding.atbClassifyActivityToolbar.setBackButtonClickListener { finish() }

        mBinding.run {
            srlClassifyActivity.setOnRefreshListener {
                //避免刷新间隔太短
                if (System.currentTimeMillis() - lastRefreshTime > 500) {
                    lastRefreshTime = System.currentTimeMillis()
                    if (viewModel.mldClassifyTabList.value?.isEmpty() == false)
                        viewModel.getClassifyData(currentPartUrl)
                    else viewModel.getClassifyTabData()
                } else {
                    srlClassifyActivity.finishRefresh()
                }
            }
            srlClassifyActivity.setOnLoadMoreListener { viewModel.loadMoreClassifyData() }

            rvClassifyActivityTab.layoutManager =
                GridLayoutManager(this@ClassifyActivity, 2, GridLayoutManager.HORIZONTAL, false)
            rvClassifyActivityTab.adapter = classifyTabAdapter

            rvClassifyActivity.layoutManager = LinearLayoutManager(this@ClassifyActivity)
            rvClassifyActivity.adapter = classifyAdapter

            spinnerClassifyActivity.adapter = spinnerAdapter
            spinnerClassifyActivity.setOnItemSelectedListener {
                onItemSelected { _, view, position, _ ->
                    if (view is TextView) view.setTextColor(getResColor(R.color.foreground_main_color_2_skin))
                    classifyTabAdapter.dataList =
                        spinnerAdapter.getItem(position)?.classifyDataList.orEmpty()
                }
            }
        }

        viewModel.mldClassifyTabList.observe(this) {
            mBinding.srlClassifyActivity.finishRefresh()
            if (it != null && it.isEmpty()) {
                spinnerAdapter.clear()
                spinnerAdapter.notifyDataSetChanged()
            } else if (it != null) {
                spinnerAdapter.clear()
                spinnerAdapter.addAll(it)
                spinnerAdapter.notifyDataSetChanged()

                //自动选中第一个
                if (currentPartUrl.isEmpty() && it[0].classifyDataList.size > 0) {
                    val firstItem = it[0].classifyDataList[0]
                    currentPartUrl = firstItem.actionUrl
                    classifyTabTitle = it[0].toString()
                    classifyTitle = firstItem.title
                    tabSelected(currentPartUrl)
                } else {
                    var found = false
                    it.forEachIndexed { index, classifyBean ->
                        classifyBean.classifyDataList.forEach { item ->
                            if (item.actionUrl == currentPartUrl) {
                                mBinding.spinnerClassifyActivity.setSelection(index, true)
                                classifyTabTitle = classifyBean.name
                                classifyTitle = item.title
                                tabSelected(currentPartUrl)
                                found = true
                                return@forEachIndexed
                            }
                        }
                    }
                    if (!found) tabSelected(currentPartUrl)
                }
            }
        }

        viewModel.mldClassifyList.observe(this) {
            mBinding.srlClassifyActivity.closeHeaderOrFooter()
            classifyAdapter.dataList = it ?: emptyList()
            mBinding.atbClassifyActivityToolbar.titleText =
                if (classifyTabTitle.isEmpty()) "${getString(R.string.anime_classify)}  $classifyTitle"
                else "${getString(R.string.anime_classify)}  ${
                    if (classifyTabTitle.endsWith(":") ||
                        classifyTabTitle.endsWith("：")
                    ) classifyTabTitle.substring(0, classifyTabTitle.length - 1)
                    else classifyTabTitle
                }：$classifyTitle"
        }

        viewModel.mldLoadMoreClassifyList.observe(this) {
            mBinding.srlClassifyActivity.closeHeaderOrFooter()
            classifyAdapter.dataList += (it ?: emptyList())
        }

        viewModel.setActivity(this)

        viewModel.getClassifyTabData()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearActivity()
    }

    override fun getBinding(): ActivityClassifyBinding =
        ActivityClassifyBinding.inflate(layoutInflater)

    private fun tabSelected(partUrl: String) {
        currentPartUrl = partUrl
        mBinding.srlClassifyActivity.autoRefresh()
    }

    private fun classifyTabClicked(data: ClassifyTab1Bean) {
        classifyTabTitle = spinnerAdapter.getItem(
            mBinding.spinnerClassifyActivity.selectedItemPosition
        ).toString()
        classifyTitle = data.title
        tabSelected(data.actionUrl)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        super.onChangeSkin()
        classifyAdapter.notifyDataSetChanged()
        classifyTabAdapter.notifyDataSetChanged()
    }
}
