package com.skyd.imomoe.view.activity

import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.SkinCover1Bean
import com.skyd.imomoe.databinding.ActivitySkinBinding
import com.skyd.imomoe.ext.addFitsSystemWindows
import com.skyd.imomoe.ext.theme.appThemeRes
import com.skyd.imomoe.ext.theme.getAttrColor
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.SkinCover1Proxy

class SkinActivity : BaseActivity<ActivitySkinBinding>() {
    private val list: MutableList<Any> = ArrayList()
    private val adapter: VarietyAdapter = VarietyAdapter(mutableListOf(SkinCover1Proxy()))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initSkinData()
        mBinding.run {
            ablSkinActivityToolbar.addFitsSystemWindows(right = true, top = true)
            tbSkinActivityToolbar.setNavigationOnClickListener { finish() }

            rvSkinActivity.addFitsSystemWindows(bottom = true)
            rvSkinActivity.layoutManager = GridLayoutManager(
                this@SkinActivity,
                AnimeShowSpanSize.MAX_SPAN_SIZE
            ).apply { spanSizeLookup = AnimeShowSpanSize(adapter) }
            rvSkinActivity.addItemDecoration(AnimeShowItemDecoration())
            rvSkinActivity.adapter = adapter
        }
    }

    override fun getBinding(): ActivitySkinBinding = ActivitySkinBinding.inflate(layoutInflater)

    private fun initSkinData() {
        list.clear()
        list += SkinCover1Bean(
            "",
            ContextCompat.getColor(this, R.color.primary_pink),
            getString(R.string.theme_pink_title),
            appThemeRes == R.style.Theme_Anime_Pink,
            R.style.Theme_Anime_Pink
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            list += SkinCover1Bean(
                "",
                getAttrColor(R.attr.colorPrimary),
                getString(R.string.theme_dynamic_title),
                appThemeRes == R.style.Theme_Anime_Dynamic,
                R.style.Theme_Anime_Dynamic
            )
        }
        list += SkinCover1Bean(
            "",
            ContextCompat.getColor(this, R.color.primary_blue),
            getString(R.string.theme_blue_title),
            appThemeRes == R.style.Theme_Anime_Blue,
            R.style.Theme_Anime_Blue
        )
        list += SkinCover1Bean(
            "",
            ContextCompat.getColor(this, R.color.primary_lemon),
            getString(R.string.theme_lemon_title),
            appThemeRes == R.style.Theme_Anime_Lemon,
            R.style.Theme_Anime_Lemon
        )
        list += SkinCover1Bean(
            "",
            ContextCompat.getColor(this, R.color.primary_purple),
            getString(R.string.theme_purple_title),
            appThemeRes == R.style.Theme_Anime_Purple,
            R.style.Theme_Anime_Purple
        )
        list += SkinCover1Bean(
            "",
            ContextCompat.getColor(this, R.color.primary_green),
            getString(R.string.theme_green_title),
            appThemeRes == R.style.Theme_Anime_Green,
            R.style.Theme_Anime_Green
        )
        adapter.dataList = list
    }
}