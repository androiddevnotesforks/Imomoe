package com.skyd.imomoe.view.component.player

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import com.kuaishou.akdanmaku.DanmakuConfig
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.data.DanmakuItemData.Companion.DANMAKU_STYLE_ICON_UP
import com.kuaishou.akdanmaku.ecs.component.filter.*
import com.kuaishou.akdanmaku.render.SimpleRenderer
import com.kuaishou.akdanmaku.ui.DanmakuPlayer
import com.kuaishou.akdanmaku.ui.DanmakuView
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.component.player.danmaku.DanmakuType
import com.skyd.imomoe.view.component.player.danmaku.anime.AnimeDanmakuParser
import com.skyd.imomoe.view.component.player.danmaku.anime.AnimeDanmakuParser.Companion.toDanmakuItemData
import com.skyd.imomoe.view.component.player.danmaku.anime.AnimeDanmakuRequester
import com.skyd.imomoe.view.component.player.danmaku.anime.AnimeDanmakuSender
import com.skyd.imomoe.view.component.player.danmaku.bili.BilibiliDanmakuParser
import com.skyd.imomoe.view.component.player.danmaku.bili.BilibiliDanmakuRequester
import com.skyd.imomoe.view.listener.dsl.setOnSeekBarChangeListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL
import java.util.zip.Inflater
import java.util.zip.InflaterInputStream


open class DanmakuVideoPlayer : AnimeVideoPlayer {
    companion object {
        const val ANIME_DANMAKU_URL = Api.DANMAKU_URL
    }

    private var mDanmakuUrl: String = ""
    private lateinit var mDanmakuView: DanmakuView          //弹幕view
    private lateinit var mDanmakuPlayer: DanmakuPlayer
    private val colorFilter = TextColorFilter()
    private var dataFilters = emptyMap<Int, DanmakuFilter>()
    private var config = DanmakuConfig().apply {
        dataFilter = createDataFilters()
        dataFilters = dataFilter.associateBy { it.filterParams }
        layoutFilter = createLayoutFilters()
    }

    // 是否在显示弹幕
    private var mDanmakuShow = true

    // 弹幕源类型
    private var mDanmakuSourceType: DanmakuType = DanmakuType.AnimeType()

    // 弹幕输入文本框
    private var etDanmakuInput: EditText? = null

    // 弹幕开关
    private var ivShowDanmaku: ImageView? = null

    private var vgDanmakuController: ViewGroup? = null

    // 自定义弹幕链接
    private var tvInputCustomDanmakuUrl: TextView? = null

    private var mDanmakuControllerHeight: Int = 0

    // 弹幕进度-2s
    private var tvRewindDanmakuProgress: TextView? = null

    // 弹幕进度恢复正常
    private var tvResetDanmakuProgress: TextView? = null

    // 弹幕进度+2s
    private var tvForwardDanmakuProgress: TextView? = null

    // 弹幕进度delta
    private var mDanmakuProgressDelta: Long = 0L

    // 弹幕字号缩放百分比SeekBar
    private var sbDanmakuTextScale: SeekBar? = null

    // "弹幕字号"TextView
    private var tvDanmakuTextScaleHeader: TextView? = null

    // 显示弹幕字号缩放百分比TextView
    private var tvDanmakuTextScale: TextView? = null

    // 弹幕字号缩放最小百分比
    private val mDanmakuTextScaleMinPercent: Int = 70

    // 弹幕字号百分比
    private var mDanmakuTextScalePercent: Int = mDanmakuTextScaleMinPercent + 60

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun init(context: Context?) {
        super.init(context)
        mDanmakuView = findViewById(R.id.danmaku_view)
        mDanmakuPlayer = DanmakuPlayer(SimpleRenderer()).also {
            it.bindView(mDanmakuView)
        }
        ivShowDanmaku = findViewById(R.id.iv_show_danmaku)
        etDanmakuInput = findViewById(R.id.et_input_danmaku)
        vgDanmakuController = findViewById(R.id.cl_danmaku_controller)
        tvInputCustomDanmakuUrl = findViewById(R.id.tv_input_custom_danmaku_url)
        tvRewindDanmakuProgress = findViewById(R.id.tv_player_rewind_danmaku_progress)
        tvResetDanmakuProgress = findViewById(R.id.tv_player_reset_danmaku_progress)
        tvForwardDanmakuProgress = findViewById(R.id.tv_player_forward_danmaku_progress)
        sbDanmakuTextScale = findViewById(R.id.sb_danmaku_text_size_scale)
        tvDanmakuTextScaleHeader = findViewById(R.id.tv_danmaku_text_size_scale_header)
        tvDanmakuTextScale = findViewById(R.id.tv_danmaku_text_size_scale)
        etDanmakuInput?.gone()
        ivShowDanmaku?.gone()
        // 设置高度是0
        hideBottomDanmakuController()

        etDanmakuInput?.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                val text = v.text.toString()
                if (text.isBlank()) {
                    mContext.getString(R.string.please_input_danmaku_text).showToast()
                    return@setOnEditorActionListener false
                }
                v.text = ""
                v.hideKeyboard()
                sendDanmaku(text)
            }
            true
        }

        etDanmakuInput?.setOnFocusChangeListener { v, hasFocus ->
            if (mIfCurrentIsFullscreen) {
                if (hasFocus) cancelDismissControlViewTimer()
                else {
                    startDismissControlViewTimer()
                    if (v is EditText) v.hideKeyboard()
                }
            } else if (!hasFocus && v is EditText) {
                v.hideKeyboard()
            }
        }

        ivShowDanmaku?.setOnClickListener {
            startDismissControlViewTimer()
            mDanmakuShow = !mDanmakuShow
            resolveDanmakuShow()
        }

        tvInputCustomDanmakuUrl?.setOnClickListener {
            (mContext as? Activity)?.showInputDialog(
                hint = mContext.getString(R.string.input_danmaku_url)
            ) { _, _, text ->
                try {
                    val url = URL(text.toString()).toString()
//                        val url = URL("http://api.bilibili.com/x/v1/dm/list.so?oid=431625080").toString()
                    if (url.contains("bili", true)) {
                        setDanmakuUrl(url, DanmakuType.BilibiliType)
                    }
                } catch (e: Exception) {
                    mContext.getString(R.string.website_format_error).showToast()
                    e.printStackTrace()
                }
            }
            vgSettingContainer?.invisible()
        }

        tvRewindDanmakuProgress?.setOnClickListener {
            if (mDanmakuProgressDelta < -60000L) {
                mContext.getString(R.string.cannot_rewind_over_10s).showToast()
                return@setOnClickListener
            }
            mDanmakuProgressDelta -= 2000L
            seekDanmaku(currentPlayer.currentPositionWhenPlaying.toLong())
        }

        tvForwardDanmakuProgress?.setOnClickListener {
            if (mDanmakuProgressDelta > 60000L) {
                mContext.getString(R.string.cannot_forward_over_10s).showToast()
                return@setOnClickListener
            }
            mDanmakuProgressDelta += 2000L
            seekDanmaku(currentPlayer.currentPositionWhenPlaying.toLong())
        }

        tvResetDanmakuProgress?.setOnClickListener {
            mDanmakuProgressDelta = 0L
            seekDanmaku(currentPlayer.currentPositionWhenPlaying.toLong())
        }

        sbDanmakuTextScale?.setOnSeekBarChangeListener {
            onProgressChanged { seekBar, progress, _ ->
                seekBar ?: return@onProgressChanged
                mDanmakuTextScalePercent = progress + mDanmakuTextScaleMinPercent
                setTextSizeScale(mDanmakuTextScalePercent / 100f)
                tvDanmakuTextScale?.text = mDanmakuTextScalePercent.percentage
            }
        }
    }

    override fun onCompletion() {
        super.onCompletion()
        stopDanmaku()
    }

    override fun onAutoCompletion() {
        super.onAutoCompletion()
        stopDanmaku()
    }

//    override fun onBufferingUpdate(percent: Int) {
//        super.onBufferingUpdate(percent)
//        pauseDanmaku()
//    }

    override fun onPrepared() {
        super.onPrepared()
        setDanmakuUrl()
        seekDanmaku(0L)
//        playDanmaku()
    }

    override fun onVideoPause() {
        super.onVideoPause()
        pauseDanmaku()
    }

    override fun onVideoResume(seek: Boolean) {
        super.onVideoResume(seek)
        playDanmaku()
    }

    override fun onSeekComplete() {
        super.onSeekComplete()
        // 虽然此方法叫做onSeekComplete，但是这时候多半还没有缓冲完（为GSYPlayer库设计缺陷）
        // 因此不能开始弹幕播放，要传入pauseDanmaku = true，强行暂停播放
        seekDanmaku(gsyVideoManager.currentPosition, true)
    }

    override fun changeUiToPlayingShow() {
        super.changeUiToPlayingShow()
        // 弥补上述onSeekComplete方法内的缺陷
        playDanmaku()
    }

    override fun changeUiToPauseShow() {
        super.changeUiToPauseShow()
        pauseDanmaku()
    }

    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
        pauseDanmaku()
    }

    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        stopDanmaku()
    }

    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        pauseDanmaku()
    }

    override fun changeUiToError() {
        super.changeUiToError()
        pauseDanmaku()
    }

    /**
     * 视频播放速度改变后回调
     */
    override fun onSpeedChanged(speed: Float) {
        super.onSpeedChanged(speed)
        mDanmakuPlayer.updatePlaySpeed(speed)
    }

    /**
     * 处理播放器在全屏切换时，弹幕显示的逻辑
     * 需要格外注意的是，因为全屏和小屏，是切换了播放器，所以需要同步之间的弹幕状态
     */
    override fun startWindowFullscreen(
        context: Context?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val player =
            super.startWindowFullscreen(context, actionBar, statusBar) as DanmakuVideoPlayer
        player.ivShowDanmaku?.visibility = ivShowDanmaku?.visibility ?: View.GONE
        player.etDanmakuInput?.visibility = etDanmakuInput?.visibility ?: View.GONE
        player.mDanmakuTextScalePercent = mDanmakuTextScalePercent
        player.sbDanmakuTextScale?.progress = mDanmakuTextScalePercent - mDanmakuTextScaleMinPercent
        player.setTextSizeScale(mDanmakuTextScalePercent / 100f)

        player.mDanmakuShow = mDanmakuShow
        player.resolveDanmakuShow()
        if (player.mDanmakuUrl != mDanmakuUrl) {
            player.setDanmakuUrl(
                mDanmakuUrl, mDanmakuSourceType,
                mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING
            )
        }
        player.seekDanmaku(currentPositionWhenPlaying.toLong())
        pauseDanmaku()

        return player
    }

    /**
     * 处理播放器在退出全屏时，弹幕显示的逻辑
     * 需要格外注意的是，因为全屏和小屏，是切换了播放器，所以需要同步之间的弹幕状态
     */
    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer?
    ) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        gsyVideoPlayer?.let {
            val player = it as DanmakuVideoPlayer
            if (player.etDanmakuInput?.visibility == View.VISIBLE) showBottomDanmakuController()
            else hideBottomDanmakuController()
            ivShowDanmaku?.visibility = player.ivShowDanmaku?.visibility ?: View.GONE
            etDanmakuInput?.visibility = player.etDanmakuInput?.visibility ?: View.GONE
            mDanmakuTextScalePercent = player.mDanmakuTextScalePercent
            sbDanmakuTextScale?.progress =
                player.mDanmakuTextScalePercent - player.mDanmakuTextScaleMinPercent
            setTextSizeScale(player.mDanmakuTextScalePercent / 100f)

            mDanmakuShow = player.mDanmakuShow
            resolveDanmakuShow()
            if (mDanmakuUrl != player.mDanmakuUrl) {
                setDanmakuUrl(
                    player.mDanmakuUrl,
                    player.mDanmakuSourceType,
                    player.mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING
                )
            }
            seekDanmaku(player.currentPositionWhenPlaying.toLong())
            player.pauseDanmaku()
        }
    }

    fun getDanmakuControllerHeight(): Int {
        vgDanmakuController?.measure(
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
        )
        return vgDanmakuController?.height ?: 0
    }

    fun setDanmakuUrl(
        url: String = ANIME_DANMAKU_URL,
        danmakuSourceType: DanmakuType = DanmakuType.AnimeType(),
        autoPlayIfVideoIsPlaying: Boolean = true    // 调用此方法后若视频在播放，则自动播放弹幕
    ) {
        if (url.isEmpty()) return

        runCatching {
            coroutineScope.launch {
                var success = false
                val dataList: MutableList<DanmakuItemData> = arrayListOf()

                when (danmakuSourceType) {
                    is DanmakuType.AnimeType -> {
                        val danmakuData = AnimeDanmakuRequester.request(animeTitle, mTitle)
                        danmakuSourceType.data = danmakuData
                        dataList += AnimeDanmakuParser(danmakuData?.data.orEmpty()).parse()
                        success = true
                    }
                    is DanmakuType.BilibiliType -> {
                        val inputStream = InflaterInputStream(
                            BilibiliDanmakuRequester.request(url),
                            Inflater(true)
                        )
                        dataList += BilibiliDanmakuParser(inputStream.string()).parse()
                        success = true
                    }
                }
                if (success) {
                    withContext(Dispatchers.Main) {
                        mDanmakuPlayer.updateData(dataList)
                        mDanmakuView.post {
                            if (autoPlayIfVideoIsPlaying && mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING) {
                                seekDanmaku(currentPlayer.currentPositionWhenPlaying.toLong())
                                playDanmaku()
                            }
                        }
                    }
                }
                mDanmakuSourceType = danmakuSourceType
                mDanmakuUrl = url
            }
        }.onFailure {
            it.printStackTrace()
            it.message?.showToast()
        }
    }

    /**
     * 弹幕的显示与关闭
     */
    private fun resolveDanmakuShow() {
        post {
            if (mDanmakuShow) {
                setDanmakuVisibility(true)
                ivShowDanmaku?.isSelected = true
            } else {
                setDanmakuVisibility(false)
                ivShowDanmaku?.isSelected = false
            }
        }
    }

    private fun setDanmakuVisibility(visible: Boolean) {
        config = config.copy(visibility = visible)
        mDanmakuPlayer.updateConfig(config)
    }

    /**
     * 开始播放弹幕
     */
    private fun onDanmakuStart() {
        etDanmakuInput?.visible()
        ivShowDanmaku?.visible()
        tvRewindDanmakuProgress?.visible()
        tvResetDanmakuProgress?.visible()
        tvForwardDanmakuProgress?.visible()
        sbDanmakuTextScale?.visible()
        tvDanmakuTextScaleHeader?.visible()
        tvDanmakuTextScale?.visible()
        if (mDanmakuSourceType is DanmakuType.AnimeType) {
            etDanmakuInput?.enable()
            etDanmakuInput?.hint = mContext.getString(R.string.send_a_danmaku)
        } else {
            etDanmakuInput?.disable()
            etDanmakuInput?.hint = mContext.getString(R.string.send_a_danmaku_is_disabled)
        }
        showBottomDanmakuController()
        setTextSizeScale(mDanmakuTextScalePercent / 100f)

        mVideoAllCallBack.let {
            if (it is MyVideoAllCallBack) it.onDanmakuStart()
        }
    }

    /**
     * 播放弹幕，要保证只在次方法内调用mDanmakuPlayer.start(config)
     */
    protected open fun playDanmaku() {
        if (mDanmakuUrl.isBlank()) return
        // 若不加下面的if，则切换横竖屏后不管是否暂停，弹幕都会自动播放
        mDanmakuPlayer.start(config)
        onDanmakuStart()
    }

    /**
     * 发送弹幕
     */
    protected open fun sendDanmaku(
        content: String,
        time: Long = gsyVideoManager.currentPosition
    ) {
        coroutineScope.launch {
            when (val danmakuSourceType = mDanmakuSourceType) {
                is DanmakuType.AnimeType -> {
                    val episode = danmakuSourceType.data?.episode ?: return@launch
                    AnimeDanmakuSender.send(
                        content = content,
                        episodeId = episode.id,
                        time = time
                    )?.let {
                        val data = it.toDanmakuItemData(DANMAKU_STYLE_ICON_UP)
                        withContext(Dispatchers.Main) {
                            mDanmakuPlayer.send(data)
                        }
                    }
                }
                else -> {
                    context.getString(R.string.danmaku_video_player_unsupport_send_danmaku)
                        .showToast()
                }
            }
        }
    }

    /**
     * 暂停弹幕
     */
    protected open fun pauseDanmaku() {
        mDanmakuPlayer.pause()
    }

    /**
     * 停止弹幕
     */
    protected open fun stopDanmaku() {
        mDanmakuPlayer.stop()
        // 加此句是因为stop后会seek 0，又会播放
        mDanmakuPlayer.pause()
    }

    /**
     * 弹幕偏移
     * @param pauseDanmaku 传入true则不管当前状态都会停止播放弹幕
     */
    private fun seekDanmaku(time: Long, pauseDanmaku: Boolean = false) {
        // 若mDanmakuProgressDelta<0即左移了，并且总进度也小于0，则重置mDanmakuProgressDelta
        if (time + mDanmakuProgressDelta < 0L) {
            mDanmakuProgressDelta = 0L - time
        }
        mDanmakuPlayer.seekTo(time + mDanmakuProgressDelta)
        // 由于上面一条语句会导致弹幕开始播放，因此要判断是否暂停
        if (pauseDanmaku || mCurrentState == GSYVideoView.CURRENT_STATE_PAUSE ||
            mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START
        )
            pauseDanmaku()
        if (mCurrentState == GSYVideoView.CURRENT_STATE_AUTO_COMPLETE ||
            mCurrentState == GSYVideoView.CURRENT_STATE_ERROR ||
            mCurrentState == GSYVideoView.CURRENT_STATE_NORMAL
        )
            stopDanmaku()
    }

    /**
     * 更改弹幕字号缩放百分比
     * @param scale 缩放倍数，例如2.7f指的是弹幕字号乘2.7
     */
    private fun setTextSizeScale(scale: Float) {
        config = config.copy(textSizeScale = scale)
        mDanmakuPlayer.updateConfig(config)
    }

    /**
     * 释放弹幕控件
     */
    private fun releaseDanmaku() {
        mDanmakuPlayer.release()
    }

    /**
     * 调用此方法释放整个视频播放器
     */
    override fun release() {
        super.release()
        releaseDanmaku()
    }

    /**
     * 显示非全屏模式下播放器下方弹幕控制部分
     */
    private fun showBottomDanmakuController() {
        vgDanmakuController?.let { danmakuController ->
            if (danmakuController.layoutParams.height == 0) {
                danmakuController.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                danmakuController.requestLayout()
                mDanmakuControllerHeight = danmakuController.height
                layoutParams.height = height + danmakuController.height
                requestLayout()
            }
        }
    }

    /**
     * 隐藏非全屏模式下播放器下方弹幕控制部分
     */
    private fun hideBottomDanmakuController() {
        vgDanmakuController?.let { danmakuController ->
            if (danmakuController.layoutParams.height != 0) {
                if (danmakuController.height > 0) {
                    mDanmakuControllerHeight = danmakuController.height
                    layoutParams.height = height - danmakuController.height
                    requestLayout()
                }
                danmakuController.layoutParams.height = 0
                danmakuController.requestLayout()
            }
        }
    }

    private fun createDataFilters(): List<DanmakuDataFilter> =
        listOf(
            TypeFilter(),
            colorFilter,
            UserIdFilter(),
            GuestFilter(),
            BlockedTextFilter { it == 0L },
            DuplicateMergedFilter()
        )

    private fun createLayoutFilters(): List<DanmakuLayoutFilter> = emptyList()
}