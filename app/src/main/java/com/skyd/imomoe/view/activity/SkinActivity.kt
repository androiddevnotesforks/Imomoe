package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SkinCover1Bean
import com.skyd.imomoe.databinding.ActivitySkinBinding
import com.skyd.imomoe.util.Util.getDefaultResColor
import com.skyd.imomoe.view.adapter.decoration.SkinItemDecoration
import com.skyd.imomoe.view.adapter.spansize.SkinSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.SkinCover1Proxy
import com.skyd.skin.core.SkinResourceProcessor

class SkinActivity : BaseActivity<ActivitySkinBinding>() {
    private val list: MutableList<Any> = ArrayList()
    private val adapter: VarietyAdapter = VarietyAdapter(mutableListOf(SkinCover1Proxy()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSkinData()
        mBinding.run {
            atbSkinActivityToolbar.setBackButtonClickListener { finish() }

            rvSkinActivity.layoutManager = GridLayoutManager(this@SkinActivity, 3)
                .apply { spanSizeLookup = SkinSpanSize(adapter) }
            rvSkinActivity.addItemDecoration(SkinItemDecoration())
            rvSkinActivity.adapter = adapter
        }
    }

    override fun getBinding(): ActivitySkinBinding = ActivitySkinBinding.inflate(layoutInflater)

    private fun usingSkin(skinPath: String, skinSuffix: String): Boolean {
        return SkinResourceProcessor.instance.skinPath == skinPath &&
                SkinResourceProcessor.instance.skinSuffix == skinSuffix
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        super.onChangeSkin()
        initSkinData()
        adapter.notifyDataSetChanged()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        initSkinData()
        adapter.notifyDataSetChanged()
    }

    private fun initSkinData() {
        list.clear()
        list += SkinCover1Bean(
            "",
            getDefaultResColor(R.color.main_color_2_skin),
            "粉色少女🎀",
            usingSkin("", ""),
            "",
            ""
        )
        list += SkinCover1Bean(
            "",
            getDefaultResColor(R.color.black),
            "deep♂️dark♂️fantasy",
            usingSkin("", "_dark"),
            "",
            "_dark"
        )
        list += SkinCover1Bean(
            "",
            getDefaultResColor(R.color.main_color_2_skin_blue),
            "♂️深蓝幻想",
            usingSkin("", "_blue"),
            "",
            "_blue"
        )
        list += SkinCover1Bean(
            "",
            getDefaultResColor(R.color.main_color_2_skin_lemon),
            "柠檬酸🍋",
            usingSkin("", "_lemon"),
            "",
            "_lemon"
        )
        list += SkinCover1Bean(
            "",
            getDefaultResColor(R.color.main_color_2_skin_sweat_soybean),
            "流汗黄豆😅",
            usingSkin("", "_sweat_soybean"),
            "",
            "_sweat_soybean"
        )
        adapter.dataList = list
    }
}