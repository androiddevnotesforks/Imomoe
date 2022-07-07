package com.skyd.imomoe.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.config.Const
import com.skyd.imomoe.database.getAppDataBase
import com.skyd.imomoe.databinding.ActivityAnimeDetailBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.model.DataSourceManager
import com.skyd.imomoe.route.Router.buildRouteUri
import com.skyd.imomoe.route.Router.route
import com.skyd.imomoe.route.processor.PlayActivityProcessor
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.coil.CoilUtil.loadImage
import com.skyd.imomoe.util.coil.DarkBlurTransformation
import com.skyd.imomoe.util.compare.EpisodeTitleSort.sortEpisodeTitle
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.*
import com.skyd.imomoe.view.fragment.dialog.EpisodeDialogFragment
import com.skyd.imomoe.view.fragment.dialog.ShareDialogFragment
import com.skyd.imomoe.viewmodel.AnimeDetailViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URL
import kotlin.random.Random

@AndroidEntryPoint
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
                        getAppDataBase().historyDao().getHistoryFlow(viewModel.partUrl).also {
                            setOnClickListener { v ->
                                val url = v.tag
                                if (url is String) {
                                    val const = DataSourceManager.getConst()
                                    if (const != null) {
                                        PlayActivityProcessor.route.buildRouteUri {
                                            appendQueryParameter("partUrl", url)
                                            appendQueryParameter("detailPartUrl", viewModel.partUrl)
                                        }.route(this@AnimeDetailActivity)
                                    }
                                }
                            }
                            visible()
                        }.collectWithLifecycle(this@AnimeDetailActivity) { hb ->
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
                        EpisodeDialogFragment {
                            title = getString(R.string.play_list)
                            dataList = data.episodeList.toMutableList().sortEpisodeTitle()
                            onEpisodeClick { _, data, _ ->
                                data.route.route(context)
                                dismiss()
                            }
                        }.show(supportFragmentManager, EpisodeDialogFragment.TAG)
                    },
                    onAnimeEpisodeClickListener = { _, data, _ ->
                        data.route.route(this)
                    })
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.partUrl = intent.getStringExtra("partUrl").orEmpty()

        mBinding.tbAnimeDetailActivity.run {
            addFitsSystemWindows(right = true, top = true)

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
            viewModel.favorite.collectWithLifecycle(this@AnimeDetailActivity) {
                menu.findItem(R.id.menu_item_anime_detail_activity_favorite).apply {
                    isVisible = true
                    isChecked = it
                    setIcon(if (it) R.drawable.ic_star_24 else R.drawable.ic_star_border_24)
                }
            }
        }

        mBinding.run {
            rvAnimeDetailActivityInfo.addFitsSystemWindows(right = true, bottom = true)

            rvAnimeDetailActivityInfo.layoutManager = GridLayoutManager(
                this@AnimeDetailActivity,
                AnimeShowSpanSize.MAX_SPAN_SIZE
            ).apply { spanSizeLookup = AnimeShowSpanSize(adapter) }
            // 复用AnimeShow的ItemDecoration
            rvAnimeDetailActivityInfo.addItemDecoration(AnimeShowItemDecoration())
            rvAnimeDetailActivityInfo.adapter = adapter

            srlAnimeDetailActivity.setOnRefreshListener { viewModel.getAnimeDetailData() }
        }

        viewModel.animeDetailList.collectWithLifecycle(this) { data ->
            mBinding.srlAnimeDetailActivity.isRefreshing = false
            when (data) {
                is DataState.Success -> {
                    adapter.dataList = data.data
                }
                else -> {
                    adapter.dataList = emptyList()
                }
            }

            if (viewModel.cover.url.isNullOrBlank()) return@collectWithLifecycle
            mBinding.ivAnimeDetailActivityBackground.loadImage(viewModel.cover.url) {
                transformations(DarkBlurTransformation(this@AnimeDetailActivity))
                viewModel.cover.referer?.let { referer ->
                    addHeader("Referer", referer)
                }
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
        }

        mBinding.srlAnimeDetailActivity.isRefreshing = true
        if (viewModel.animeDetailList.value is DataState.Empty) viewModel.getAnimeDetailData()
    }

    override fun getBinding(): ActivityAnimeDetailBinding =
        ActivityAnimeDetailBinding.inflate(layoutInflater)
}
