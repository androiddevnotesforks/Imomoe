package com.skyd.imomoe.view.activity

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.builder.GSYVideoOptionBuilder
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.databinding.ActivityPlayBinding
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.toMD5
import com.skyd.imomoe.ext.visible
import com.skyd.imomoe.util.*
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.util.Util.getResColor
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.Util.getSkinResourceId
import com.skyd.imomoe.util.Util.openVideoByExternalPlayer
import com.skyd.imomoe.util.Util.setColorStatusBar
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.util.downloadanime.AnimeDownloadHelper
import com.skyd.imomoe.view.adapter.decoration.AnimeEpisodeItemDecoration
import com.skyd.imomoe.view.adapter.decoration.AnimeShowItemDecoration
import com.skyd.imomoe.view.adapter.spansize.PlaySpanSize
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.*
import com.skyd.imomoe.view.component.player.AnimeVideoPlayer
import com.skyd.imomoe.view.component.player.AnimeVideoPositionMemoryStore
import com.skyd.imomoe.view.component.player.DanmakuVideoPlayer
import com.skyd.imomoe.view.component.player.DetailPlayerActivity
import com.skyd.imomoe.view.fragment.MoreDialogFragment
import com.skyd.imomoe.view.fragment.ShareDialogFragment
import com.skyd.imomoe.viewmodel.PlayViewModel
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer
import kotlin.math.abs


class PlayActivity : DetailPlayerActivity<DanmakuVideoPlayer, ActivityPlayBinding>() {
    override var statusBarSkin: Boolean = false
    private val viewModel: PlayViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(
            mutableListOf(
                Header1Proxy(),
                AnimeCover1Proxy(),
                AnimeCover2Proxy(),
                HorizontalRecyclerView1Proxy(onMoreButtonClickListener = { _, _, _ ->
                    getSheetDialog("play").show()
                }, onAnimeEpisodeClickListener = { _, data, index ->
                    viewModel.playAnotherEpisode(data.actionUrl, index)
                })
            )
        )
    }
    private var isFirstTime = true
    private var currentNightMode: Int = 0
    private var lastCanCollapsed: Boolean? = null

    private fun initView() {
        currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        setColorStatusBar(window, Color.BLACK)

        mBinding.apply {
            setSupportActionBar(tbPlayActivity)
            supportActionBar?.setDisplayShowTitleEnabled(false)

            if (ctlPlayActivity != null && ablPlayActivity != null) {
                ablPlayActivity.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { _, verticalOffset ->
                    when {
                        abs(verticalOffset) > ctlPlayActivity.scrimVisibleHeightTrigger -> {
                            tvPlayActivityToolbarVideoTitle.gone()
                            tvPlayActivityToolbarTitle?.visible(animate = true, dur = 200L)
                        }
                        else -> {
                            tvPlayActivityToolbarVideoTitle.visible(animate = true, dur = 200L)
                            tvPlayActivityToolbarTitle?.gone()
                        }
                    }
                })
            }

            ivPlayActivityToolbarBack.setOnClickListener { finish() }
            tvPlayActivityToolbarTitle?.setOnClickListener {
                (avpPlayActivity.currentPlayer as AnimeVideoPlayer).clickStartIcon()
            }

            avpPlayActivity.setTopContainer(tbPlayActivity)

            ivPlayActivityToolbarDownload.setOnClickListener { getSheetDialog("download").show() }
            ivPlayActivityToolbarBack.setOnClickListener { onBackPressed() }

            // 分享按钮
            ivPlayActivityToolbarShare.setOnClickListener {
                ShareDialogFragment().setShareContent(Api.MAIN_URL + viewModel.partUrl)
                    .show(supportFragmentManager, "share_dialog")
            }
            // 更多按钮
            ivPlayActivityToolbarMore.setOnClickListener {
                MoreDialogFragment().apply {
                    show(supportFragmentManager, "more_dialog")
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
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()

        viewModel.setActivity(this)

        initVideoBuilderMode()

        viewModel.partUrl = intent.getStringExtra("partUrl").orEmpty()
        viewModel.detailPartUrl = intent.getStringExtra("detailPartUrl").orEmpty()

        mBinding.apply {
            rvPlayActivity.layoutManager = GridLayoutManager(this@PlayActivity, 4)
                .apply { spanSizeLookup = PlaySpanSize(adapter) }
            // 复用AnimeShow的ItemDecoration
            rvPlayActivity.addItemDecoration(AnimeShowItemDecoration())
            rvPlayActivity.adapter = adapter

            srlPlayActivity.setOnRefreshListener { viewModel.getPlayData() }
            srlPlayActivity.setColorSchemeResources(getSkinResourceId(R.color.main_color_skin))

            avpPlayActivity.playPositionMemoryStore = AnimeVideoPositionMemoryStore
        }

        viewModel.mldFavorite.observe(this) {
            mBinding.ivPlayActivityFavorite.setImageDrawable(
                if (it) getResDrawable(R.drawable.ic_star_main_color_2_24_skin)
                else getResDrawable(R.drawable.ic_star_border_main_color_2_24_skin)
            )
        }

        mBinding.ivPlayActivityFavorite.setOnClickListener {
            when (viewModel.mldFavorite.value) {
                true -> viewModel.deleteFavorite()
                false -> viewModel.insertFavorite()
            }
        }

        viewModel.mldPlayDataList.observe(this) {
            mBinding.srlPlayActivity.isRefreshing = false

            mBinding.tvPlayActivityTitle.text = viewModel.playBean?.title?.title

            adapter.dataList = it ?: emptyList()

            if (isFirstTime) {
                mBinding.avpPlayActivity.startPlay()
                isFirstTime = false
            }
        }

        viewModel.mldAnimeDownloadUrl.observe(this) {
            AnimeDownloadHelper.instance.downloadAnime(
                this, it.videoUrl, it.videoUrl.toMD5(),
                "${viewModel.playBean?.title?.title}/${it.title}"
            )
        }

        viewModel.mldPlayAnotherEpisode.observe(this) {
            if (it) mBinding.avpPlayActivity.currentPlayer.startPlay()
        }

        viewModel.mldEpisodesList.observe(this) {
            mBinding.avpPlayActivity.setEpisodeAdapter(
                VarietyAdapter(
                    mutableListOf(
                        PlayerEpisode1Proxy(
                            onBindViewHolder = { holder, data, index, _ ->
                                holder.tvTitle.setTextColor(
                                    getResColor(
                                        if (data.title == viewModel.animeEpisodeDataBean.title)
                                            R.color.unchanged_main_color_2_skin
                                        else R.color.foreground_white_skin
                                    )
                                )
                                holder.tvTitle.text = data.title
                                if (index == viewModel.currentEpisodeIndex) {
                                    (mBinding.avpPlayActivity.currentPlayer as AnimeVideoPlayer)
                                        .rvEpisode?.scrollToPosition(index)
                                }
                                holder.itemView.setOnClickListener {
                                    mBinding.avpPlayActivity.currentPlayer.run {
                                        if (this is AnimeVideoPlayer) {
                                            getRightContainer()?.gone()
                                            // 因为右侧界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                                            enableDismissControlViewTimer(true)
                                        }
                                    }
                                    viewModel.playAnotherEpisode(data.actionUrl, index)
                                }
                                true
                            })
                    )
                ).apply { dataList = viewModel.episodesList }
            )
        }

        mBinding.srlPlayActivity.isRefreshing = true
        viewModel.getPlayData()

        val videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        GSYVideoManager.instance().optionModelList = listOf(videoOptionModel)
    }

    override fun getBinding() = ActivityPlayBinding.inflate(layoutInflater)

    private fun GSYBaseVideoPlayer.startPlay() {
        if (isDestroyed) return
        mBinding.tvPlayActivityToolbarVideoTitle.text = viewModel.animeEpisodeDataBean.title
        PlayerFactory.setPlayManager(Exo2PlayerManager().javaClass)
        GSYVideoType.disableMediaCodec()        // 关闭硬解码
        // 设置播放URL
        viewModel.updateFavoriteData()
        viewModel.insertHistoryData()
        setUp(
            viewModel.animeEpisodeDataBean.videoUrl,
            false, viewModel.animeEpisodeDataBean.title
        )
        // 开始播放
        startPlayLogic()
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clearActivity()
    }

    override fun onVideoSizeChanged() {
        mBinding.apply {
            val tag = avpPlayActivity.tag
            val state = avpPlayActivity.currentPlayer.currentState
            if (avpPlayActivity.isIfCurrentIsFullscreen ||
                state == GSYVideoView.CURRENT_STATE_ERROR ||
                state == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE ||
                state == GSYVideoView.CURRENT_STATE_PREPAREING ||
                (tag is String && tag == "sw600dp-land")
            ) {
                return
            }
            val videoHeight: Int = avpPlayActivity.currentVideoHeight
            val videoWidth: Int = avpPlayActivity.currentVideoWidth
            if (videoHeight <= 10 || videoWidth <= 10) return
            val ratio = videoWidth.toDouble() / videoHeight
            if (ratio < 0.001) return
            avpPlayActivity.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            )
            val playerWidth: Int = avpPlayActivity.width
            if (abs(playerWidth.toDouble() / avpPlayActivity.height - ratio) < 0.001) return
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

    override fun onPlayError(url: String?, vararg objects: Any?) {
        super.onPlayError(url, *objects)
        "${objects[0].toString()}, ${getString(R.string.get_data_failed)}".showToast()
    }

    override fun onQuitFullscreen(url: String?, vararg objects: Any?) {
        super.onQuitFullscreen(url, *objects)
        adapter.notifyDataSetChanged()
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
                avpPlayActivity.setTopContainer(tbPlayActivity)
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

    private fun getSheetDialog(action: String): BottomSheetDialog {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val contentView = View.inflate(this, R.layout.dialog_bottom_sheet_2, null)
        bottomSheetDialog.setContentView(contentView)
        val tvTitle =
            contentView.findViewById<TextView>(R.id.tv_dialog_bottom_sheet_2_title)
        tvTitle.text = when (action) {
            "play" -> getString(R.string.play_list)
            "download" -> getString(R.string.download_anime)
            else -> ""
        }
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.rv_dialog_bottom_sheet_2)
        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.post {
            recyclerView.setPadding(16.dp, 0, 16.dp, 16.dp)
            recyclerView.scrollToPosition(0)
        }
        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(AnimeEpisodeItemDecoration())
        }
        @Suppress("UNCHECKED_CAST") val adapter = VarietyAdapter(
            mutableListOf(AnimeEpisode1Proxy(onClickListener = { _, data, index ->
                if (action == "play") {
                    viewModel.playAnotherEpisode(data.actionUrl, index)
                    bottomSheetDialog.dismiss()
                } else if (action == "download") {
                    getString(R.string.parsing_video).showToast()
                    viewModel.getAnimeDownloadUrl(data.actionUrl, index)
                }
            }, width = ViewGroup.LayoutParams.MATCH_PARENT))
        ).apply { dataList = viewModel.episodesList }
        val observer = Observer<Boolean> {
            adapter.notifyDataSetChanged()
        }
        viewModel.mldEpisodesList.observe(this, observer)
        bottomSheetDialog.setOnDismissListener {
            viewModel.mldEpisodesList.removeObserver(observer)
        }
        recyclerView.adapter = adapter
        return bottomSheetDialog
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        (newConfig.uiMode and Configuration.UI_MODE_NIGHT_MASK).let {
            if (it != currentNightMode) {
                currentNightMode = it
                adapter.notifyDataSetChanged()
                mBinding.ivPlayActivityFavorite.setImageDrawable(
                    if (viewModel.mldFavorite.value == true) {
                        getResDrawable(R.drawable.ic_star_main_color_2_24_skin)
                    } else {
                        getResDrawable(R.drawable.ic_star_border_main_color_2_24_skin)
                    }
                )
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        super.onChangeSkin()
        adapter.notifyDataSetChanged()
    }
}