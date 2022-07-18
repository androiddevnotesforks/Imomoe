package com.skyd.imomoe.view.activity

import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.appbar.AppBarLayout
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.databinding.ActivityPlayBinding
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.ext.theme.getAttrColor
import com.skyd.imomoe.state.DataState
import com.skyd.imomoe.util.Util
import com.skyd.imomoe.util.Util.openVideoByExternalPlayer
import com.skyd.imomoe.util.download.downloadanime.AnimeDownloadHelper
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.AnimeShowSpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.*
import com.skyd.imomoe.view.component.player.AnimeVideoPlayer
import com.skyd.imomoe.view.component.player.AnimeVideoPositionMemoryStore
import com.skyd.imomoe.view.component.player.DanmakuVideoPlayer
import com.skyd.imomoe.view.component.player.DetailPlayerActivity
import com.skyd.imomoe.view.component.player.danmaku.DanmakuManager
import com.skyd.imomoe.view.fragment.dialog.EpisodeDialogFragment
import com.skyd.imomoe.view.fragment.dialog.MoreDialogFragment
import com.skyd.imomoe.view.fragment.dialog.ShareDialogFragment
import com.skyd.imomoe.viewmodel.PlayViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import kotlin.math.abs


@AndroidEntryPoint
class PlayActivity : DetailPlayerActivity<DanmakuVideoPlayer, ActivityPlayBinding>() {
    private val viewModel: PlayViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(
            mutableListOf(
                Header1Proxy(),
                AnimeCover1Proxy(),
                AnimeCover2Proxy(),
                HorizontalRecyclerView1Proxy(onMoreButtonClickListener = { _, _, _ ->
                    EpisodeDialogFragment(
                        backgroundDim = false,
                        offsetFromTop = if (screenIsLand) {
                            null
                        } else {
                            mBinding.avpPlayActivity.height
                        }
                    ) {
                        title = getString(R.string.play_list)
                        dataList = viewModel.episodesList.value.readOrNull().orEmpty()
                        onEpisodeClick { _, data, index ->
                            viewModel.playAnotherEpisode(data.route, index)
                            dismiss()
                        }
                        val job = viewModel.episodesList.collectWithLifecycle(this) {
                            dataList = it.readOrNull().orEmpty()
                        }
                        onDismissListener = { job.cancel() }
                    }.show(supportFragmentManager, EpisodeDialogFragment.TAG)
                }, onAnimeEpisodeClickListener = { _, data, index ->
                    viewModel.playAnotherEpisode(data.route, index)
                })
            )
        )
    }
    private var currentNightMode: Int = 0
    private var lastCanCollapsed: Boolean? = null

    override fun transparentSystemBar(): Boolean = false

    private fun initView() {
        mBinding.apply {
            root.addFitsSystemWindows(right = true, top = true)
            rvPlayActivity.addFitsSystemWindows(right = true, bottom = true)

            if (ctlPlayActivity != null && ablPlayActivity != null) {
                ablPlayActivity.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                    when {
                        abs(verticalOffset) > ctlPlayActivity.scrimVisibleHeightTrigger -> {
                            tvPlayActivityToolbarTitle?.visible(animate = true, dur = 200L)
                        }
                        else -> {
                            tvPlayActivityToolbarTitle?.gone()
                        }
                    }
                })
            }

            tbPlayActivity.setNavigationOnClickListener { finish() }
            tbPlayActivity.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.menu_item_play_activity_share -> {
                        ShareDialogFragment().setShareContent(Api.MAIN_URL + viewModel.partUrl)
                            .show(supportFragmentManager, "share_dialog")
                        true
                    }
                    R.id.menu_item_play_activity_download -> {
                        EpisodeDialogFragment(
                            backgroundDim = false,
                            offsetFromTop = if (screenIsLand) {
                                null
                            } else {
                                mBinding.avpPlayActivity.height
                            }
                        ) {
                            title = this@PlayActivity.getString(R.string.download_anime)
                            dataList = viewModel.episodesList.value.readOrNull().orEmpty()
                            onEpisodeClick { _, data, index ->
                                getString(R.string.parsing_video).showToast()
                                viewModel.getAnimeDownloadUrl(data.route, index)
                            }
                            val job = viewModel.episodesList.collectWithLifecycle(this) {
                                dataList = it.readOrNull().orEmpty()
                            }
                            onDismissListener = { job.cancel() }
                        }.show(supportFragmentManager, EpisodeDialogFragment.TAG)
                        true
                    }
                    R.id.menu_item_play_activity_more -> {
                        MoreDialogFragment().apply {
                            show(supportFragmentManager, MoreDialogFragment.TAG)
                            onCancelButtonClick { dismiss() }
                            onDlnaButtonClick {
                                val url = avpPlayActivity.getUrl()
                                if (url == null) {
                                    getString(R.string.please_wait_video_loaded).showToast()
                                    return@onDlnaButtonClick
                                }
                                startActivity(
                                    Intent(this@PlayActivity, DlnaActivity::class.java)
                                        .putExtra("url", url)
                                        .putExtra("title", avpPlayActivity.getTitle())
                                )
                                dismiss()
                            }
                            onOpenInOtherPlayerButtonClick {
                                if (!openVideoByExternalPlayer(
                                        this@PlayActivity,
                                        viewModel.animeEpisodeDataBean.videoUrl
                                    )
                                ) getString(R.string.matched_app_not_found).showToast()
                                dismiss()
                            }
                        }
                        true
                    }
                    else -> false
                }
            }

            tvPlayActivityToolbarTitle?.setOnClickListener {
                (avpPlayActivity.currentPlayer as AnimeVideoPlayer).clickStartIcon()
            }

            avpPlayActivity.setTopContainer(tbPlayActivity as? ViewGroup)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        window.statusBarColor = Color.BLACK
        WindowInsetsControllerCompat(window, mBinding.root).isAppearanceLightStatusBars = false

        initView()

        viewModel.setActivity(this)

        initVideoBuilderMode()

        viewModel.partUrl = intent.getStringExtra("partUrl").orEmpty()

        mBinding.apply {
            rvPlayActivity.layoutManager = GridLayoutManager(
                this@PlayActivity,
                AnimeShowSpanSize.MAX_SPAN_SIZE
            ).apply {
                spanSizeLookup = AnimeShowSpanSize(adapter = adapter, enableLandScape = false)
            }
            rvPlayActivity.addItemDecoration(AnimeShowItemDecoration())
            rvPlayActivity.adapter = adapter

            srlPlayActivity.setOnRefreshListener { viewModel.getPlayData() }

            avpPlayActivity.playPositionMemoryStore = AnimeVideoPositionMemoryStore
        }

        viewModel.favorite.collectWithLifecycle(this) {
            mBinding.ivPlayActivityFavorite.setImageResource(
                if (it) R.drawable.ic_star_24
                else R.drawable.ic_star_border_24
            )
        }

        mBinding.ivPlayActivityFavorite.setOnClickListener {
            when (viewModel.favorite.value) {
                true -> viewModel.deleteFavorite()
                false -> viewModel.insertFavorite()
            }
        }

        viewModel.playDataList.collectWithLifecycle(this) {
            when (it) {
                is DataState.Refreshing -> {
                    mBinding.srlPlayActivity.isRefreshing = true
                }
                is DataState.Success -> {
                    mBinding.srlPlayActivity.isRefreshing = false
                    mBinding.tvPlayActivityTitle.text = viewModel.playBean.title.title
                    adapter.dataList = it.data
                    if (viewModel.isFirstTimeToPlay) {
                        mBinding.avpPlayActivity.currentPlayer.startPlay()
                        viewModel.isFirstTimeToPlay = false
                    }
                }
                else -> {
                    mBinding.srlPlayActivity.isRefreshing = false
                    adapter.dataList = emptyList()

                }
            }
        }

        viewModel.animeDownloadUrl.collectWithLifecycle(this) {
            AnimeDownloadHelper.downloadAnime(
                this@PlayActivity,
                url = it.videoUrl,
                animeTitle = viewModel.playBean.title.title,
                animeEpisode = it.title
            )
        }

        viewModel.playAnotherEpisodeEvent.collectWithLifecycle(this) {
            if (it) mBinding.avpPlayActivity.currentPlayer.startPlay()
        }

        viewModel.episodesList.collectWithLifecycle(this) {
            mBinding.avpPlayActivity.setEpisodeAdapter(
                VarietyAdapter(
                    mutableListOf(
                        PlayerEpisode1Proxy(onBindViewHolder = { holder, data, index, _ ->
                            holder.tvTitle.text = data.title
                            if (data.route == viewModel.animeEpisodeDataBean.route) {
                                holder.tvTitle.setTextColor(getAttrColor(R.attr.colorPrimary))
                                (mBinding.avpPlayActivity.currentPlayer as AnimeVideoPlayer)
                                    .rvEpisode?.scrollToPosition(index)
                            } else {
                                holder.tvTitle.setTextColor(
                                    ContextCompat.getColor(
                                        this@PlayActivity,
                                        android.R.color.white
                                    )
                                )
                            }
                            holder.itemView.setOnClickListener {
                                mBinding.avpPlayActivity.currentPlayer.run {
                                    if (this is AnimeVideoPlayer) {
                                        getRightContainer()?.gone()
                                        // 因为右侧界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                                        enableDismissControlViewTimer(true)
                                    }
                                }
                                viewModel.playAnotherEpisode(data.route, index)
                            }
                            true
                        })
                    )
                ).apply { dataList = viewModel.episodesList.value.readOrNull().orEmpty() }
            )
        }

        viewModel.loadingEpisodeData.collectWithLifecycle(this) {
            mBinding.avpPlayActivity.currentPlayer.release()
        }

        mBinding.avpPlayActivity.onPlayNextEpisode = {
            if (!viewModel.playNextEpisode()) {
                getString(R.string.have_no_next_episode).showToast()
            }
        }

        if (viewModel.isFirstTimeToPlay) {
            mBinding.srlPlayActivity.isRefreshing = true
            viewModel.getPlayData()
        }

        val videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        GSYVideoManager.instance().optionModelList = listOf(videoOptionModel)
    }

    override fun getBinding() = ActivityPlayBinding.inflate(layoutInflater)

    private fun GSYBaseVideoPlayer.startPlay() {
        if (isDestroyed) return
        DanmakuManager.enableDanmaku = true
        currentPlayer.apply {
            val videoUrl = viewModel.animeEpisodeDataBean.videoUrl
            val episodeTitle = viewModel.animeEpisodeDataBean.title
            val animeTitle = viewModel.playBean.title.title
            if (this is AnimeVideoPlayer) {
                this.animeTitle = animeTitle
            }
            mBinding.tbPlayActivity.title = episodeTitle
            // 设置播放URL
            viewModel.updateFavoriteData()
            viewModel.insertHistoryData()
            setUp(videoUrl, false, episodeTitle)
            lifecycleScope.launch {
                val playPosition = AnimeVideoPositionMemoryStore.getPlayPosition(videoUrl)
                // 若用户设置了自动跳转 且 没有播放完
                if (playPosition != null && playPosition != -1L && sharedPreferences()
                        .getBoolean("autoJumpToLastPosition", true)
                ) seekOnStart = playPosition
                withContext(Dispatchers.Main) {
                    if (!activityInBackground) {
                        // 开始播放
                        startPlayLogic()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearActivity()
    }

    override fun onVideoSizeChanged() {
        resizePlayer()
    }

    override fun onDanmakuStart() {
        resizePlayer()
    }

    /**
     * 根据视频和是否显示弹幕调整播放器高度
     */
    private fun resizePlayer() {
        mBinding.apply {
            avpPlayActivity.currentPlayer.post {
                val tag = avpPlayActivity.tag
                val state = avpPlayActivity.currentPlayer.currentState
                if (avpPlayActivity.isIfCurrentIsFullscreen ||
                    state == GSYVideoView.CURRENT_STATE_ERROR ||
                    state == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE ||
                    state == GSYVideoView.CURRENT_STATE_PREPAREING ||
                    (tag is String && tag == "sw600dp-land")
                ) {
                    return@post
                }
                val videoHeight: Int = avpPlayActivity.currentVideoHeight
                val videoWidth: Int = avpPlayActivity.currentVideoWidth
                if (videoHeight <= 10 || videoWidth <= 10) return@post
                val ratio = videoWidth.toDouble() / videoHeight
                if (ratio < 0.001) return@post
                avpPlayActivity.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                )
                val playerWidth: Int = avpPlayActivity.width
                var playerHeight = playerWidth / ratio
                avpPlayActivity.currentPlayer.let {
                    if (it is DanmakuVideoPlayer) playerHeight += it.getDanmakuControllerHeight()
                }
                val parentHeight = Util.getScreenHeight(true)
                if (playerHeight > parentHeight * 0.75) playerHeight = parentHeight * 0.75
                val layoutParams: ViewGroup.LayoutParams = avpPlayActivity.layoutParams
                avpPlayActivity.requestLayout()
                ValueAnimator.ofInt(layoutParams.height, playerHeight.toInt())
                    .setDuration(200)
                    .apply {
                        addUpdateListener { animation ->
                            layoutParams.height = animation.animatedValue as Int
                            avpPlayActivity.requestLayout()
                        }
                        start()
                    }
            }
        }
    }

    override fun onPlayError(url: String?, vararg objects: Any?) {
        super.onPlayError(url, *objects)
        "${objects[0].toString()}, ${getString(R.string.get_data_failed)}".showToast()
    }

    override fun onPrepared(url: String?, vararg objects: Any?) {
        super.onPrepared(url, *objects)
        //调整触摸滑动快进的比例
        //毫秒,刚好划一屏1分35秒
        mBinding.avpPlayActivity.currentPlayer.apply {
            seekRatio = duration / 90_000f
        }
    }

    override fun videoPlayStatusChanged(playing: Boolean) {
        super.videoPlayStatusChanged(playing)
        mBinding.apply {
            canCollapsed(!playing)
            tvPlayActivityToolbarTitle?.text =
                if (avpPlayActivity.currentPlayer.currentState ==
                    GSYVideoView.CURRENT_STATE_AUTO_COMPLETE
                ) getString(R.string.replay_video)
                else getString(R.string.play_video_now)
        }
    }

    /**
     * 是否需要必须显示工具栏
     *
     * @param show false：不需要显示；true：需要显示
     */
    private fun needShowToolbar(show: Boolean) {
        mBinding.apply {
            if (show) {
                avpPlayActivity.setTopContainer(null)
                tbPlayActivity.visible()
            } else {
                avpPlayActivity.setTopContainer(tbPlayActivity as? ViewGroup)
            }
        }
    }

    private fun canCollapsed(enable: Boolean) {
        if (lastCanCollapsed == enable) return
        needShowToolbar(enable)
        lastCanCollapsed = enable
        mBinding.ablPlayActivity?.let {
            val mAppBarChildAt: View = it.getChildAt(0)
            val mAppBarParams = mAppBarChildAt.layoutParams as AppBarLayout.LayoutParams
            mAppBarParams.scrollFlags = if (enable) {
                AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                        AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
            } else {
                AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
            }
            mAppBarChildAt.layoutParams = mAppBarParams
            Handler(Looper.getMainLooper()).postDelayed({
                if (!enable) it.setExpanded(true)
            }, 500)
        }
    }

    override fun getGSYVideoPlayer(): DanmakuVideoPlayer = mBinding.avpPlayActivity

    override val gsyVideoOptionBuilder = GSYVideoOptionBuilder().apply {
        setReleaseWhenLossAudio(false)         // 音频焦点冲突时是否释放
        setPlayTag(this.javaClass.simpleName)  // 防止错位设置
        setIsTouchWiget(true)
        setRotateViewAuto(false)
        setLockLand(false)
        setShowFullAnimation(false)            // 打开动画
        setNeedLockFull(true)
        setDismissControlTime(5000)
    }

    override fun clickForFullScreen() {}

    override val detailOrientationRotateAuto = true
}