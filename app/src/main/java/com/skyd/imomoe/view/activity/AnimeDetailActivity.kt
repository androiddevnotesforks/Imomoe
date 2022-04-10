package com.skyd.imomoe.view.activity

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisodeDataBean
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.databinding.ActivityAnimeDetailBinding
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.util.coil.DarkBlurTransformation
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.view.adapter.decoration.AnimeEpisodeItemDecoration
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeDetailSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.*
import com.skyd.imomoe.view.component.BottomSheetRecyclerView
import com.skyd.imomoe.util.compare.EpisodeTitleSort.sortEpisodeTitle
import com.skyd.imomoe.view.fragment.ShareDialogFragment
import com.skyd.imomoe.viewmodel.AnimeDetailViewModel
import java.net.URL
import kotlin.random.Random


class AnimeDetailActivity : BaseActivity<ActivityAnimeDetailBinding>() {
    private val viewModel: AnimeDetailViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(
            mutableListOf(
                Header1Proxy(color = Header1Proxy.WHITE),
                AnimeDescribe1Proxy(),
                AnimeInfo1Proxy(onBindViewHolder = { holder, _, _ ->
                    // 查找番剧播放历史决定是否可续播
                    holder.tvAnimeInfoContinuePlay.apply {
                        gone()
                        getAppDataBase().historyDao().getHistoryLiveData(viewModel.partUrl).also {
                            setOnClickListener { v ->
                                val url = v.tag
                                if (url is String) {
                                    val const = DataSourceManager.getConst()
                                    if (const != null && url.startsWith(const.actionUrl.ANIME_PLAY()))
                                        Util.process(
                                            this@AnimeDetailActivity,
                                            url + viewModel.partUrl, url
                                        )
                                    else Util.process(this@AnimeDetailActivity, url, url)
                                }
                            }
                            visible()
                        }.observe(this@AnimeDetailActivity) { hb ->
                            //FIX_TODO 2022/1/22 14:53 0 这里没有在打开播放后更新，原因未知，所以暂时只能手动刷新
                            if (hb != null) {
                                text = getString(R.string.play_last_time_episode, hb.lastEpisode)
                                tag = hb.lastEpisodeUrl
                            } else gone()       // 小心复用，所以主要主动隐藏
                        }
                    }
                    false
                }),
                AnimeCover1Proxy(color = AnimeCover1Proxy.WHITE),
                HorizontalRecyclerView1Proxy(
                    color = HorizontalRecyclerView1Proxy.WHITE,
                    onMoreButtonClickListener = { _, data, _ ->
                        showEpisodeSheetDialog(data.episodeList).show()
                    },
                    onAnimeEpisodeClickListener = { _, data, _ ->
                        val const = DataSourceManager.getConst()
                        if (const != null && data.actionUrl.startsWith(const.actionUrl.ANIME_PLAY()))
                            Util.process(this, data.actionUrl + viewModel.partUrl, data.actionUrl)
                        else Util.process(this, data.actionUrl, data.actionUrl)
                    })
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.partUrl = intent.getStringExtra("partUrl").orEmpty()

        mBinding.tbAnimeDetailActivity.run {
            setNavigationOnClickListener { finish() }
            menu.getItem(1).isVisible = false
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item_anime_detail_activity_share -> {
                        ShareDialogFragment().setShareContent(Api.MAIN_URL + viewModel.partUrl)
                            .show(supportFragmentManager, "share_dialog")
                        true
                    }
                    R.id.menu_item_anime_detail_activity_favorite -> {
                        when (item.isChecked) {
                            true -> {
                                item.setIcon(R.drawable.ic_star_border_24)
                                viewModel.deleteFavorite()
                            }
                            false -> {
                                item.setIcon(R.drawable.ic_star_24)
                                viewModel.insertFavorite()
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            // 收藏
            viewModel.mldFavorite.observe(this@AnimeDetailActivity) {
                menu.findItem(R.id.menu_item_anime_detail_activity_favorite).apply {
                    isVisible = true
                    isChecked = it
                    setIcon(if (it) R.drawable.ic_star_24 else R.drawable.ic_star_border_24)
                }
            }
        }

        mBinding.run {
            rvAnimeDetailActivityInfo.layoutManager = GridLayoutManager(this@AnimeDetailActivity, 4)
                .apply { spanSizeLookup = AnimeDetailSpanSize(adapter) }
            // 复用AnimeShow的ItemDecoration
            rvAnimeDetailActivityInfo.addItemDecoration(AnimeShowItemDecoration())
            rvAnimeDetailActivityInfo.adapter = adapter

            srlAnimeDetailActivity.setOnRefreshListener { viewModel.getAnimeDetailData() }
        }

        viewModel.mldAnimeDetailList.observe(this, Observer {
            mBinding.srlAnimeDetailActivity.isRefreshing = false
            adapter.dataList = it ?: emptyList()

            if (viewModel.cover.url.isBlank()) return@Observer
            mBinding.ivAnimeDetailActivityBackground.loadImage(viewModel.cover.url) {
                transformations(DarkBlurTransformation(this@AnimeDetailActivity))
                addHeader("Referer", viewModel.cover.referer)
                addHeader("Host", URL(viewModel.cover.url).host)
                addHeader("Accept", "*/*")
                addHeader("Accept-Encoding", "gzip, deflate")
                addHeader("Connection", "keep-alive")
                addHeader(
                    "User-Agent",
                    Const.Request.USER_AGENT_ARRAY[Random.nextInt(Const.Request.USER_AGENT_ARRAY.size)]
                )
            }
            mBinding.tbAnimeDetailActivity.title = viewModel.title
        })

        mBinding.srlAnimeDetailActivity.isRefreshing = true
        if (viewModel.mldAnimeDetailList.value == null) viewModel.getAnimeDetailData()
    }

    override fun getBinding(): ActivityAnimeDetailBinding =
        ActivityAnimeDetailBinding.inflate(layoutInflater)

    private fun showEpisodeSheetDialog(dataList: List<AnimeEpisodeDataBean>): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val contentView = View.inflate(this, R.layout.dialog_bottom_sheet_2, null)
        bottomSheetDialog.setContentView(contentView)
        val recyclerView =
            contentView.findViewById<BottomSheetRecyclerView>(R.id.rv_dialog_bottom_sheet_2)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.post {
            recyclerView.setPadding(16.dp, 0, 16.dp, 16.dp)
            recyclerView.scrollToPosition(0)
        }
        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(AnimeEpisodeItemDecoration())
        }
        recyclerView.adapter = VarietyAdapter(
            mutableListOf(AnimeEpisode1Proxy(onClickListener = { _, data, _ ->
                val const = DataSourceManager.getConst()
                if (const != null && data.actionUrl.startsWith(const.actionUrl.ANIME_PLAY()))
                    Util.process(this, data.actionUrl + viewModel.partUrl, data.actionUrl)
                else Util.process(this, data.actionUrl, data.actionUrl)
                bottomSheetDialog.dismiss()
            }, width = ViewGroup.LayoutParams.MATCH_PARENT)),
        ).apply { this.dataList = dataList.toMutableList().sortEpisodeTitle() }
        return bottomSheetDialog
    }
}
