package com.skyd.imomoe.view.component.player

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.kuaishou.akdanmaku.data.DanmakuItemData.Companion.DANMAKU_STYLE_ICON_UP
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.R
import com.skyd.imomoe.config.Api
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.component.player.danmaku.DanmakuManager
import com.skyd.imomoe.view.component.player.danmaku.DanmakuType
import com.skyd.imomoe.view.component.player.danmaku.anime.AnimeDanmakuRepository
import com.skyd.imomoe.view.component.player.danmaku.anime.AnimeDanmakuRepository.Companion.toDanmakuItemData
import com.skyd.imomoe.view.component.player.danmaku.bili.BilibiliDanmakuRepository
import com.skyd.imomoe.view.fragment.dialog.DanmakuSettingDialogFragment
import com.skyd.imomoe.view.fragment.dialog.SendDanmakuFontDialogFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL


open class DanmakuVideoPlayer : AnimeVideoPlayer {
    companion object {
        const val ANIME_DANMAKU_URL = Api.DANMAKU_URL
    }

    private lateinit var danmakuManager: DanmakuManager

    // 弹幕输入文本框
    private var etDanmakuInput: EditText? = null

    // 弹幕开关
    private var ivShowDanmaku: ImageView? = null

    // 弹幕设置
    private var ivDanmakuSetting: ImageView? = null

    // 发送弹幕样式按钮
    private var ivSendDanmakuFont: ImageView? = null

    private var vgDanmakuController: ViewGroup? = null

    // 自定义弹幕链接
    private var tvInputCustomDanmakuUrl: TextView? = null

    // 弹幕进度-2s
    private var tvRewindDanmakuProgress: TextView? = null

    // 弹幕进度恢复正常
    private var tvResetDanmakuProgress: TextView? = null

    // 弹幕进度+2s
    private var tvForwardDanmakuProgress: TextView? = null

    // 弹幕进度delta
    private var mDanmakuProgressDelta: Long = 0L

    @Suppress("unused")
    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun init(context: Context?) {
        super.init(context)
        danmakuManager = DanmakuManager(findViewById(R.id.danmaku_view))
        ivShowDanmaku = findViewById(R.id.iv_show_danmaku)
        ivDanmakuSetting = findViewById(R.id.iv_danmaku_setting)
        ivSendDanmakuFont = findViewById(R.id.iv_send_danmaku_font)
        etDanmakuInput = findViewById(R.id.et_input_danmaku)
        vgDanmakuController = findViewById(R.id.vg_danmaku_controller)
        tvInputCustomDanmakuUrl = findViewById(R.id.tv_input_custom_danmaku_url)
        tvRewindDanmakuProgress = findViewById(R.id.tv_player_rewind_danmaku_progress)
        tvResetDanmakuProgress = findViewById(R.id.tv_player_reset_danmaku_progress)
        tvForwardDanmakuProgress = findViewById(R.id.tv_player_forward_danmaku_progress)
        vgDanmakuController?.gone()

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

        ivSendDanmakuFont?.setOnClickListener {
            val fragmentActivity =
                mContext.activity as? FragmentActivity ?: return@setOnClickListener
            SendDanmakuFontDialogFragment(
                danmakuMode = DanmakuManager.sendDanmakuMode,
                danmakuColor = DanmakuManager.sendDanmakuColor
            ) { danmakuMode, danmakuColor ->
                DanmakuManager.sendDanmakuMode = danmakuMode
                DanmakuManager.sendDanmakuColor = danmakuColor
            }.show(fragmentActivity.supportFragmentManager, SendDanmakuFontDialogFragment.TAG)
        }

        ivShowDanmaku?.setOnClickListener {
            startDismissControlViewTimer()
            DanmakuManager.showDanmaku = !DanmakuManager.showDanmaku
            resolveDanmakuShow()
        }

        ivDanmakuSetting?.setOnClickListener {
            val fragmentActivity =
                mContext.activity as? FragmentActivity ?: return@setOnClickListener
            DanmakuSettingDialogFragment(
                filter = DanmakuManager.showDanmakuType,
                allowOverlap = DanmakuManager.allowOverlap,
                danmakuAlpha = DanmakuManager.alpha,
                danmakuScale = DanmakuManager.danmakuScale,
                danmakuBold = DanmakuManager.danmakuBold,
                onDanmakuFilterChanged = { danmakuManager.switchTypeFilter(it) },
                onAllowOverlapChanged = { danmakuManager.allowOverlap(it) },
                onDanmakuAlphaChanged = { danmakuManager.danmakuAlpha(it) },
                onDanmakuScaleChanged = { danmakuManager.danmakuScale(it) },
                onDanmakuBoldChanged = { danmakuManager.danmakuBold(it) }
            ).show(fragmentActivity.supportFragmentManager, DanmakuSettingDialogFragment.TAG)
        }

        tvInputCustomDanmakuUrl?.setOnClickListener {
            (mContext as? Activity)?.showInputDialog(
                hint = mContext.getString(R.string.input_danmaku_url)
            ) { _, _, text ->
                try {
                    val url = URL(text.toString()).toString()
                    if (url.contains("bili", true)) {
                        DanmakuManager.enableDanmaku = true
                        setDanmakuUrl(url, DanmakuType.BilibiliType())
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
            seekDanmaku(currentPlayer.currentPositionWhenPlaying)
        }

        tvForwardDanmakuProgress?.setOnClickListener {
            if (mDanmakuProgressDelta > 60000L) {
                mContext.getString(R.string.cannot_forward_over_10s).showToast()
                return@setOnClickListener
            }
            mDanmakuProgressDelta += 2000L
            seekDanmaku(currentPlayer.currentPositionWhenPlaying)
        }

        tvResetDanmakuProgress?.setOnClickListener {
            mDanmakuProgressDelta = 0L
            seekDanmaku(currentPlayer.currentPositionWhenPlaying)
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
        if (DanmakuManager.enableDanmaku) {
            setDanmakuUrl()
            seekDanmaku(0L)
//        playDanmaku()
        }
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
        danmakuManager.danmakuPlayer.updatePlaySpeed(speed)
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
        player.vgDanmakuController?.visibility = vgDanmakuController?.visibility ?: View.GONE

        // 重建一个DanmakuPlayer，以便清除上次播放的弹幕
        player.danmakuManager.recreatePlayer()
        player.resolveDanmakuShow()
        player.updatePlayerDanmakuState()
        player.seekDanmaku(currentPositionWhenPlaying)
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
            vgDanmakuController?.visibility = player.vgDanmakuController?.visibility ?: View.GONE

            // 重建一个DanmakuPlayer，以便清除上次播放的弹幕
            danmakuManager.recreatePlayer()
            resolveDanmakuShow()
            updatePlayerDanmakuState()
            seekDanmaku(player.currentPositionWhenPlaying)
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

    /**
     * 将old状态赋值给new
     */
    fun updatePlayerDanmakuState() {
        if (!DanmakuManager.enableDanmaku) return
        danmakuManager.danmakuPlayer.updateData(DanmakuManager.danmakuDataList)
        danmakuManager.playDanmaku()
        onDanmakuStart()
    }

    fun setDanmakuUrl(
        url: String = ANIME_DANMAKU_URL,
        danmakuType: DanmakuType<*> = DanmakuType.AnimeType(),
        autoPlayIfVideoIsPlaying: Boolean = true    // 调用此方法后若视频在播放，则自动播放弹幕
    ) {
        if (url.isEmpty()) return

        DanmakuManager.danmakuDataList.clear()
        // 重建一个DanmakuPlayer，以便清除上次播放的弹幕
        (currentPlayer as DanmakuVideoPlayer).danmakuManager.recreatePlayer()

        coroutineScope.launch(Dispatchers.IO) {
            runCatching {
                var success = false
                val dataList: MutableList<DanmakuItemData> = arrayListOf()
                DanmakuManager.danmakuType = danmakuType

                when (danmakuType) {
                    is DanmakuType.AnimeType -> {
                        danmakuType.repository = AnimeDanmakuRepository(animeTitle, mTitle).apply {
                            dataList += parse()
                        }
                        success = true
                    }
                    is DanmakuType.BilibiliType -> {
                        danmakuType.repository = BilibiliDanmakuRepository(url).apply {
                            dataList += parse()
                        }
                        success = true
                    }
                }
                DanmakuManager.danmakuUrl = url
                if (success) {
                    withContext(Dispatchers.Main) {
                        (currentPlayer as DanmakuVideoPlayer).also {
                            it.danmakuManager.danmakuPlayer.updateData(dataList)
                            DanmakuManager.danmakuDataList.clear()
                            DanmakuManager.danmakuDataList += dataList
                            it.updatePlayerDanmakuState()
                            if (autoPlayIfVideoIsPlaying &&
                                it.mCurrentState == GSYVideoView.CURRENT_STATE_PLAYING
                            ) {
                                it.playDanmaku()
                            }
                            it.seekDanmaku(currentPlayer.currentPositionWhenPlaying)
                        }
                    }
                }
            }.onFailure {
                it.printStackTrace()
                it.message?.showToast()
            }
        }
    }

    /**
     * 弹幕的显示与关闭
     */
    private fun resolveDanmakuShow() {
        post {
            danmakuManager.setDanmakuVisibility(DanmakuManager.showDanmaku)
            ivShowDanmaku?.isSelected = DanmakuManager.showDanmaku
        }
    }

    /**
     * 开始播放弹幕
     */
    private fun onDanmakuStart() {
        vgDanmakuController?.visible()
        tvRewindDanmakuProgress?.visible()
        tvResetDanmakuProgress?.visible()
        tvForwardDanmakuProgress?.visible()
        if (DanmakuManager.danmakuType is DanmakuType.AnimeType) {
            etDanmakuInput?.enable()
            etDanmakuInput?.hint = mContext.getString(R.string.send_a_danmaku)
        } else {
            etDanmakuInput?.disable()
            etDanmakuInput?.hint = mContext.getString(R.string.send_a_danmaku_is_disabled)
        }
        mVideoAllCallBack.let {
            if (it is MyVideoAllCallBack) it.onDanmakuStart()
        }
    }

    /**
     * 播放弹幕，要保证只在次方法内调用mDanmakuPlayer.start(config)
     */
    protected open fun playDanmaku() {
        if (DanmakuManager.danmakuUrl.isBlank() || !DanmakuManager.enableDanmaku) return
        // 若不加下面的if，则切换横竖屏后不管是否暂停，弹幕都会自动播放
        if (currentPlayer.isInPlayingState) danmakuManager.playDanmaku()
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
            runCatching {
                when (val danmakuType = DanmakuManager.danmakuType) {
                    is DanmakuType.AnimeType -> {
                        danmakuType.repository?.send(
                            content = content,
                            time = time,
                            mode = DanmakuManager.sendDanmakuMode,
                            color = DanmakuManager.sendDanmakuColor
                        )?.let {
                            val data = it.toDanmakuItemData(DANMAKU_STYLE_ICON_UP)
                            withContext(Dispatchers.Main) {
                                danmakuManager.danmakuPlayer.send(data)
                            }
                        }
                    }
                    else -> {
                        context.getString(R.string.danmaku_video_player_unsupport_send_danmaku)
                            .showToast()
                    }
                }
            }.onFailure {
                it.printStackTrace()
                it.message?.showToast()
            }
        }
    }

    /**
     * 暂停弹幕
     */
    protected open fun pauseDanmaku() {
        danmakuManager.danmakuPlayer.pause()
    }

    /**
     * 停止弹幕
     */
    protected open fun stopDanmaku() {
        danmakuManager.danmakuPlayer.stop()
        // 加此句是因为stop后会seek 0，又会播放
        danmakuManager.danmakuPlayer.pause()
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
        danmakuManager.danmakuPlayer.seekTo(time + mDanmakuProgressDelta)
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
     * 释放弹幕控件
     */
    private fun releaseDanmaku() {
        danmakuManager.danmakuPlayer.release()
    }

    /**
     * 调用此方法释放整个视频播放器
     */
    override fun release() {
        super.release()
        releaseDanmaku()
    }
}