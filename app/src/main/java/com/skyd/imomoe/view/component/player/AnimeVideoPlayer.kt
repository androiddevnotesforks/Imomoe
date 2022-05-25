package com.skyd.imomoe.view.component.player

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Matrix
import android.os.Build
import android.util.AttributeSet
import android.view.*
import android.view.View.OnClickListener
import android.widget.*
import androidx.annotation.WorkerThread
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shuyu.gsyvideoplayer.utils.CommonUtil
import com.shuyu.gsyvideoplayer.utils.Debuger
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYBaseVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoPlayer
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.BaseBean
import com.skyd.imomoe.ext.*
import com.skyd.imomoe.ext.theme.getAttrColor
import com.skyd.imomoe.util.Util.dp
import com.skyd.imomoe.util.Util.getResDrawable
import com.skyd.imomoe.util.Util.getScreenBrightness
import com.skyd.imomoe.util.Util.getScreenHeight
import com.skyd.imomoe.util.Util.getScreenWidth
import com.skyd.imomoe.util.Util.openVideoByExternalPlayer
import com.skyd.imomoe.util.showToast
import com.skyd.imomoe.view.activity.DlnaActivity
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.VideoSpeed1Proxy
import com.skyd.imomoe.view.component.ZoomView
import com.skyd.imomoe.view.listener.dsl.setOnSeekBarChangeListener
import kotlinx.coroutines.*
import java.io.File
import java.io.Serializable
import kotlin.math.abs


open class AnimeVideoPlayer : StandardGSYVideoPlayer {
    companion object {
        val mScaleStrings = listOf(
            "默认比例" to GSYVideoType.SCREEN_TYPE_DEFAULT,
            "16:9" to GSYVideoType.SCREEN_TYPE_16_9,
            "4:3" to GSYVideoType.SCREEN_TYPE_4_3,
            "全屏" to GSYVideoType.SCREEN_TYPE_FULL,
            "拉伸全屏" to GSYVideoType.SCREEN_MATCH_FULL
        )

        const val NO_REVERSE = 0
        const val HORIZONTAL_REVERSE = 1
        const val VERTICAL_REVERSE = 2

        // 夜间屏幕最大Alpha
        const val NIGHT_SCREEN_MAX_ALPHA: Int = 0xAA

        val coroutineScope by lazy(LazyThreadSafetyMode.NONE) {
            CoroutineScope(Dispatchers.Default)
        }
    }

    // 番剧名称（不是每一集的名称）
    var animeTitle: String = ""

    /**
     * 进度记忆最小时间，默认5秒后的进度才记忆
     */
    var playPositionMemoryTimeLimit = 5000L

    var playPositionMemoryStore: PlayPositionMemoryDataStore? = null
    private var playPositionViewJob: Job? = null

    // 预跳转进度
    private var preSeekPlayPosition: Long? = null

    // 正在双指缩放移动
    private var doublePointerZoomingMoving = false

    private var initFirstLoad = true

    // 记住切换数据源类型
    private var mScaleIndex = 0

    // 4:3  16:9等
    private var tvMoreScale: TextView? = null

    // 倍速按钮
    private var tvSpeed: TextView? = null
    private var rvSpeed: RecyclerView? = null

    // 速度
    private var mPlaySpeed = 1f

    // 投屏按钮
    private var ivCling: ImageView? = null

    // 下一集按钮
    private var ivNextEpisode: ImageView? = null

    // 如何播放下一集
    var onPlayNextEpisode: () -> Unit = {}
        set(value) {
            ivNextEpisode?.setOnClickListener { value() }
            field = value
        }

    // 进度记忆组
    private var vgPlayPosition: ViewGroup? = null

    // 进度文字
    private var tvPlayPosition: TextView? = null

    // 关闭进度提示ImageView
    private var ivClosePlayPositionTip: ImageView? = null

    // 选集
    private var tvEpisode: TextView? = null
    private var mEpisodeTextViewVisibility: Int = View.VISIBLE
    private var mEpisodeButtonOnClickListener: OnClickListener? = null
    var rvEpisode: RecyclerView? = null
    private var mEpisodeAdapter: VarietyAdapter? = null

    // 设置
    protected var vgSettingContainer: ViewGroup? = null
    private var ivSetting: ImageView? = null

    // 镜像RadioGroup
    private var rgReverse: RadioGroup? = null
    private var mReverseValue: Int? = null
    private var mTextureViewTransform: Int =
        NO_REVERSE

    // 底部进度条CheckBox
    private var cbBottomProgress: CheckBox? = null

    // 自动播放下一集CheckBox
    private var cbAutoPlayNextEpisode: CheckBox? = null

    // 底部进度调
    private var pbBottomProgress: ProgressBar? = null

    // 外部播放器打开
    private var tvOpenByExternalPlayer: TextView? = null

    // 右侧弹出栏
    protected var vgRightContainer: ViewGroup? = null

    // 按住高速播放的tv
    private var tvTouchDownHighSpeed: TextView? = null
    private var mLongPressing: Boolean = false

    // 还原屏幕
    private var tvRestoreScreen: TextView? = null

    // 投屏
    private var tvDlna: TextView? = null

    // 屏幕已经双指放大移动了
    private var mDoublePointerZoomMoved: Boolean = false

    // 屏幕已经双指放大移动了
    private var vgBiggerSurface: ViewGroup? = null

    // 控件没有显示
    private var mUiCleared: Boolean = true

    // 显示系统时间
    private var tcSystemTime: TextClock? = null

    // top阴影
    private var viewTopContainerShadow: View? = null

    // 夜间屏幕View
    private var viewNightScreen: View? = null

    // 夜间屏幕seekbar
    private var sbNightScreen: SeekBar? = null

    // 夜间屏幕SeekBar值
    private var mNightScreenSeekBarProgress: Int = 0

    // 全屏手动滑动下拉状态栏的起始偏移位置
    protected open var mStatusBarOffset: Int = 50.dp

    constructor(context: Context) : super(context)

    constructor(context: Context, fullFlag: Boolean?) : super(context, fullFlag)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun getLayoutId() = if (mIfCurrentIsFullscreen)
        R.layout.layout_anime_video_player_land else R.layout.layout_anime_video_player

    @SuppressLint("ClickableViewAccessibility", "SetTextI18n")
    override fun init(context: Context?) {
        super.init(context)

        tvMoreScale = findViewById(R.id.tv_more_scale)
        tvSpeed = findViewById(R.id.tv_speed)
//        mClingImageView = findViewById(R.id.iv_cling)
        vgRightContainer = findViewById(R.id.layout_right)
        rvSpeed = findViewById(R.id.rv_right)
        rvEpisode = findViewById(R.id.rv_right)
        ivNextEpisode = findViewById(R.id.iv_next)
        tvEpisode = findViewById(R.id.tv_episode)
        ivSetting = findViewById(R.id.iv_setting)
        vgSettingContainer = findViewById(R.id.layout_setting)
        rgReverse = findViewById(R.id.rg_reverse)
        cbBottomProgress = findViewById(R.id.cb_bottom_progress)
        cbAutoPlayNextEpisode = findViewById(R.id.cb_auto_play_next_episode)
        pbBottomProgress = super.mBottomProgressBar
        tvOpenByExternalPlayer = findViewById(R.id.tv_open_by_external_player)
        tvRestoreScreen = findViewById(R.id.tv_restore_screen)
        tvTouchDownHighSpeed = findViewById(R.id.tv_touch_down_high_speed)
        vgBiggerSurface = findViewById(R.id.bigger_surface)
        tcSystemTime = findViewById(R.id.tc_system_time)
        viewTopContainerShadow = findViewById(R.id.view_top_container_shadow)
        viewNightScreen = findViewById(R.id.view_player_night_screen)
        sbNightScreen = findViewById(R.id.sb_player_night_screen)
        tvDlna = findViewById(R.id.tv_dlna)
        vgPlayPosition = findViewById(R.id.ll_play_position_view)
        tvPlayPosition = findViewById(R.id.tv_play_position_time)
        ivClosePlayPositionTip = findViewById(R.id.iv_close_play_position_tip)

        vgRightContainer?.gone()
        vgSettingContainer?.gone()
        tvTouchDownHighSpeed?.gone()
        vgPlayPosition?.gone()

        vgBiggerSurface?.setOnClickListener(this)
        vgBiggerSurface?.setOnTouchListener(this)

        ivClosePlayPositionTip?.setOnClickListener {
            playPositionViewJob?.cancel()
            vgPlayPosition?.gone(true, 200L)
        }
        vgPlayPosition?.setOnClickListener {
            preSeekPlayPosition?.also { if (it > 0L) seekTo(it) }
            vgPlayPosition?.gone(true, 200L)
        }

        tvRestoreScreen?.setOnClickListener {
            mTextureViewContainer?.run {
                if (this is ZoomView) restore()
                else {
                    translationX = 0f
                    translationY = 0f
                    scaleX = 1f
                    scaleY = 1f
                    rotation = 0f
                }
                mDoublePointerZoomMoved = false
                it.gone()
            }
        }
        tvSpeed?.setOnClickListener {
            vgRightContainer?.let {
                val adapter = VarietyAdapter(
                    mutableListOf(VideoSpeed1Proxy(onBindViewHolder = { holder, data, _, _ ->
                        if (data.title.toFloat() == speed) {
                            holder.tvTitle.setTextColor(mContext.getAttrColor(R.attr.colorPrimary))
                        }
                        holder.tvTitle.text = data.title
                        holder.tvTitle.setOnClickListener {
                            if (data.title == "1") {
                                tvSpeed?.text = mContext.getString(R.string.play_speed)
                            } else {
                                tvSpeed?.text = "${data.title}X"
                            }
                            mPlaySpeed = data.title.toFloat()
                            setSpeed(mPlaySpeed, true)
                            vgRightContainer?.gone()
                            //因为右侧界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                            startDismissControlViewTimer()
                        }
                        true
                    }))
                ).apply {
                    dataList = mutableListOf(
                        Speed1Bean("", "0.5"),
                        Speed1Bean("", "0.75"),
                        Speed1Bean("", "1"),
                        Speed1Bean("", "1.25"),
                        Speed1Bean("", "1.5"),
                        Speed1Bean("", "2")
                    )
                }
                rvSpeed?.layoutManager = LinearLayoutManager(context)
                rvSpeed?.adapter = adapter
            }
            showRightContainer()
        }
        tvEpisode?.setOnClickListener {
            vgRightContainer?.let {
                rvEpisode?.layoutManager = LinearLayoutManager(context)
                rvEpisode?.adapter = mEpisodeAdapter
                mEpisodeAdapter?.notifyDataSetChanged()
            }
            showRightContainer()
        }
        ivSetting?.setOnClickListener { showSettingContainer() }
        mReverseValue = rgReverse?.getChildAt(0)?.id
        rgReverse?.children?.forEach {
            (it as RadioButton).apply {
                setOnCheckedChangeListener { _, isChecked ->
                    if (!isChecked) return@setOnCheckedChangeListener
                    mReverseValue = id
                    when (id) {
                        R.id.rb_no_reverse -> resolveTransform(NO_REVERSE)
                        R.id.rb_horizontal_reverse -> resolveTransform(HORIZONTAL_REVERSE)
                        R.id.rb_vertical_reverse -> resolveTransform(VERTICAL_REVERSE)
                    }
                }
            }
        }
        cbBottomProgress?.setOnCheckedChangeListener { _, isChecked ->
            setBottomProgressBarVisibility(isChecked)
            sharedPreferences().editor {
                putBoolean("showPlayerBottomProgressbar", isChecked)
            }
        }
        cbBottomProgress?.isChecked = sharedPreferences()
            .getBoolean("showPlayerBottomProgressbar", false)

        cbAutoPlayNextEpisode?.setOnCheckedChangeListener { _, isChecked ->
            sharedPreferences().editor {
                putString("switchVideoMode", if (isChecked) "AutoPlayNextEpisode" else "StopPlay")
            }
        }
        cbAutoPlayNextEpisode?.isChecked = sharedPreferences()
            .getString("switchVideoMode", "StopPlay") == "AutoPlayNextEpisode"

        //重置视频比例
        GSYVideoType.setShowType(mScaleStrings[mScaleIndex].second)
        changeTextureViewShowType()
        if (mTextureView != null) mTextureView.requestLayout()

        tvMoreScale?.text = mScaleStrings[mScaleIndex].first

        //切换视频比例
        tvMoreScale?.setOnClickListener(OnClickListener {
            startDismissControlViewTimer()      //重新开始ui消失时间计时
            if (!mHadPlay) {
                return@OnClickListener
            }
            mScaleIndex = (mScaleIndex + 1) % mScaleStrings.size
            resolveTypeUI()
        })

        ivCling?.setOnClickListener {
            mContext.startActivity(
                Intent(mContext, DlnaActivity::class.java)
                    .putExtra("url", mUrl)
                    .putExtra("title", mTitle)
            )
            mOriginUrl
        }

        tvOpenByExternalPlayer?.setOnClickListener {
            if (!openVideoByExternalPlayer(mContext, mUrl))
                mContext.getString(R.string.matched_app_not_found).showToast()
        }

        sbNightScreen?.setOnSeekBarChangeListener {
            onProgressChanged { seekBar, progress, _ ->
                seekBar ?: return@onProgressChanged
                mNightScreenSeekBarProgress = progress
                viewNightScreen?.setBackgroundColor((NIGHT_SCREEN_MAX_ALPHA * progress / seekBar.max) shl 24)
            }
        }

        tvDlna?.setOnClickListener {
            val url = getUrl()
            if (url == null) {
                mContext.getString(R.string.please_wait_video_loaded).showToast()
                return@setOnClickListener
            }
            startActivity(
                mContext, Intent(mContext, DlnaActivity::class.java)
                    .putExtra("url", url)
                    .putExtra("title", getTitle()), null
            )
        }
    }

    fun getUrl(): String? = mUrl

    fun getTitle(): String = mTitle

    private fun setBottomProgressBarVisibility(show: Boolean) {
        if (show) {
            pbBottomProgress?.let {
                mBottomProgressBar = it
                it.visible()
            }
        } else {
            mBottomProgressBar?.let {
                pbBottomProgress = it
                it.gone()
                mBottomProgressBar = null
            }
        }
    }

    private fun showSettingContainer() {
        vgSettingContainer?.let {
            hideAllWidget()
            it.translationX = 150f.dp
            it.visible()
            val animator = ObjectAnimator.ofFloat(
                it, "translationX", 170f.dp, 0f
            )
            animator.duration = 300
            animator.start()
            //取消xx秒后隐藏控制界面
            cancelDismissControlViewTimer()
            if (mReverseValue == null) mReverseValue = rgReverse?.getChildAt(0)?.id
            mReverseValue?.let { id -> findViewById<RadioButton>(id).isChecked = true }
            cbBottomProgress?.isChecked = sharedPreferences()
                .getBoolean("showPlayerBottomProgressbar", false)

//            mMediaCodecCheckBox?.isChecked = GSYVideoType.isMediaCodec()
//            mMediaCodecCheckBox?.setOnCheckedChangeListener { buttonView, isChecked ->
//                if (isChecked) GSYVideoType.enableMediaCodec()
//                else GSYVideoType.disableMediaCodec()
//                startPlayLogic()
//            }
        }
    }

    fun setTopContainer(top: ViewGroup?) {
        mTopContainer = top
        viewTopContainerShadow = if (top == null) {
            viewTopContainerShadow?.visible()
            null
        } else {
            findViewById(R.id.view_top_container_shadow)
        }
        restartTimerTask()
    }

    private fun showRightContainer() {
        vgRightContainer?.let {
            hideAllWidget()
            it.translationX = 150f.dp
            it.visible()
            val animator = ObjectAnimator.ofFloat(it, "translationX", 170f.dp, 0f)
            animator.duration = 300
            animator.start()
            //取消xx秒后隐藏控制界面
            cancelDismissControlViewTimer()
        }
    }

    override fun hideAllWidget() {
        super.hideAllWidget()
//        setViewShowState(vgRightContainer, INVISIBLE)
//        setViewShowState(vgSettingContainer, INVISIBLE)
        setViewShowState(tvRestoreScreen, View.GONE)
        setViewShowState(viewTopContainerShadow, View.INVISIBLE)
    }

    override fun onClickUiToggle(e: MotionEvent?) {
        vgRightContainer?.let {
            //如果右侧栏显示，则隐藏
            if (it.visibility == View.VISIBLE) {
                it.gone()
                //因为右侧界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                startDismissControlViewTimer()
                return
            }
        }
        vgSettingContainer?.let {
            // 如果显示，则隐藏
            if (it.visibility == View.VISIBLE) {
                it.gone()
                // 因为设置界面显示时，不在xx秒后隐藏界面，所以要恢复xx秒后隐藏控制界面
                startDismissControlViewTimer()
                return
            }
        }
        super.onClickUiToggle(e)
        setRestoreScreenTextViewVisibility()
    }

    /**
     * 全屏时将对应处理参数逻辑赋给全屏播放器
     *
     * @param context
     * @param actionBar
     * @param statusBar
     * @return
     */
    override fun startWindowFullscreen(
        context: Context?,
        actionBar: Boolean,
        statusBar: Boolean
    ): GSYBaseVideoPlayer {
        val player = super.startWindowFullscreen(
            context,
            actionBar,
            statusBar
        ) as AnimeVideoPlayer
        player.seekOnStart = seekOnStart
        player.mScaleIndex = mScaleIndex
        player.tvSpeed?.text = tvSpeed?.text
        player.mFullscreenButton.visibility = mFullscreenButton.visibility
        player.mEpisodeTextViewVisibility = mEpisodeTextViewVisibility
        player.tvEpisode?.visibility = mEpisodeTextViewVisibility
        player.mEpisodeAdapter = mEpisodeAdapter
        player.mTextureViewTransform = mTextureViewTransform
        player.mReverseValue = mReverseValue
        player.mPlaySpeed = mPlaySpeed
        player.sbNightScreen?.progress = mNightScreenSeekBarProgress
        player.onPlayNextEpisode = onPlayNextEpisode
        player.animeTitle = animeTitle
        if (player.mBottomProgressBar != null) player.pbBottomProgress = player.mBottomProgressBar
        player.setBottomProgressBarVisibility(
            sharedPreferences().getBoolean("showPlayerBottomProgressbar", false)
        )
        touchSurfaceUp()
        player.setRestoreScreenTextViewVisibility()
        player.resolveTypeUI()
        player.supportDisplayCutouts()
        return player
    }

    private fun setRestoreScreenTextViewVisibility() {
        if (mUiCleared) {
            tvRestoreScreen?.gone()
        } else {
            if (mDoublePointerZoomMoved) tvRestoreScreen?.visible()
            else tvRestoreScreen?.gone()
        }
    }

    /**
     * 退出全屏时将对应处理参数逻辑返回给非播放器
     *
     * @param oldF
     * @param vp
     * @param gsyVideoPlayer
     */
    override fun resolveNormalVideoShow(
        oldF: View?,
        vp: ViewGroup?,
        gsyVideoPlayer: GSYVideoPlayer?
    ) {
        super.resolveNormalVideoShow(oldF, vp, gsyVideoPlayer)
        if (gsyVideoPlayer != null) {
            val player = gsyVideoPlayer as AnimeVideoPlayer
            seekOnStart = player.seekOnStart
            mScaleIndex = player.mScaleIndex
            mFullscreenButton.visibility = player.mFullscreenButton.visibility
            tvSpeed?.text = player.tvSpeed?.text
            mEpisodeTextViewVisibility = player.mEpisodeTextViewVisibility
            tvEpisode?.visibility = mEpisodeTextViewVisibility
            mEpisodeAdapter = player.mEpisodeAdapter
            mTextureViewTransform = player.mTextureViewTransform
            mReverseValue = player.mReverseValue
            mPlaySpeed = player.mPlaySpeed
            mNightScreenSeekBarProgress = player.sbNightScreen?.progress ?: 0
            onPlayNextEpisode = player.onPlayNextEpisode
            animeTitle = player.animeTitle
            if (mBottomProgressBar != null) pbBottomProgress = mBottomProgressBar
            setBottomProgressBarVisibility(
                sharedPreferences().getBoolean("showPlayerBottomProgressbar", false)
            )
            player.touchSurfaceUp()
            setRestoreScreenTextViewVisibility()
            resolveTypeUI()
            supportDisplayCutouts()
        }
    }

    fun setShowType(index: Int) {
        if (!mHadPlay || index !in mScaleStrings.indices) {
            return
        }
        mScaleIndex = index
        resolveTypeUI()
    }

    /**
     * 全屏/退出全屏，显示比例
     * 注意，GSYVideoType.setShowType是全局静态生效，除非重启APP。
     */
    @SuppressLint("SetTextI18n")
    private fun resolveTypeUI() {
        if (!mHadPlay) {
            return
        }
        tvMoreScale?.text = mScaleStrings[mScaleIndex].first
        GSYVideoType.setShowType(mScaleStrings[mScaleIndex].second)
        changeTextureViewShowType()
        if (mTextureView != null) mTextureView.requestLayout()
        setSpeed(mPlaySpeed, true)
        tvTouchDownHighSpeed?.gone()
        mLongPressing = false
    }

    override fun setSpeed(speed: Float, soundTouch: Boolean) {
        super.setSpeed(speed, soundTouch)
        onSpeedChanged(speed)
    }

    override fun setSpeed(speed: Float) {
        super.setSpeed(speed)
        onSpeedChanged(speed)
    }

    /**
     * 视频播放速度改变后回调
     */
    protected open fun onSpeedChanged(speed: Float) {

    }

    /**
     * 需要在尺寸发生变化的时候重新处理
     */
    override fun onSurfaceSizeChanged(
        surface: Surface?,
        width: Int,
        height: Int
    ) {
        super.onSurfaceSizeChanged(surface, width, height)
        resolveTransform(mTextureViewTransform)
    }

    override fun onSurfaceAvailable(surface: Surface?) {
        super.onSurfaceAvailable(surface)
//        resolveRotateUI()
        resolveTransform(mTextureViewTransform)
    }

    /**
     * 处理镜像旋转
     */
    private fun resolveTransform(transformSize: Int) {
        if (mTextureView == null) return
        val transform = Matrix()
        when (transformSize) {
            NO_REVERSE -> {  // 正常
                transform.setScale(1f, 1f, mTextureView.width / 2.toFloat(), 0f)
            }
            HORIZONTAL_REVERSE -> {  // 左右镜像
                transform.setScale(-1f, 1f, mTextureView.width / 2.toFloat(), 0f)
            }
            VERTICAL_REVERSE -> {  // 上下镜像
                transform.setScale(1f, -1f, 0f, mTextureView.height / 2.toFloat())
            }
            else -> return
        }
        mTextureViewTransform = transformSize
        mTextureView.setTransform(transform)
        mTextureView.invalidate()
    }

    override fun setUp(
        url: String?,
        cacheWithPlay: Boolean,
        cachePath: File?,
        title: String?
    ): Boolean {
        return super.setUp(url, cacheWithPlay, cachePath, title)
    }

    override fun updateStartImage() {
        if (mStartButton is ImageView) {
            val imageView = mStartButton as ImageView
            when (mCurrentState) {
                GSYVideoView.CURRENT_STATE_PLAYING -> {
                    imageView.setImageDrawable(getResDrawable(R.drawable.ic_pause_white_24))
                }
                GSYVideoView.CURRENT_STATE_ERROR -> {
                    imageView.setImageDrawable(getResDrawable(R.drawable.ic_play_24))
                }
                GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> {
                    imageView.setImageDrawable(getResDrawable(R.drawable.ic_refresh_24))
                }
                else -> {
                    imageView.setImageDrawable(getResDrawable(R.drawable.ic_play_24))
                }
            }
        } else {
            super.updateStartImage()
        }
    }

    override fun onBrightnessSlide(percent: Float) {
        val activity = mContext as Activity
        val lpa = activity.window.attributes
        val mBrightnessData = lpa.screenBrightness
        if (mBrightnessData <= 0.00f) {
            getScreenBrightness(activity)?.div(255.0f)?.let {
                lpa.screenBrightness = it
                activity.window.attributes = lpa
            }
        }
        super.onBrightnessSlide(percent)
    }

    override fun onVideoSizeChanged() {
        super.onVideoSizeChanged()
        mVideoAllCallBack.let {
            if (it is MyVideoAllCallBack) it.onVideoSizeChanged()
        }
    }

    //正常
    override fun changeUiToNormal() {
        super.changeUiToNormal()
        viewTopContainerShadow?.visible()
        initFirstLoad = true
        mUiCleared = false
    }

    override fun changeUiToPauseShow() {
        // 防止锁定后从桌面进入仍然显示界面的问题
        if (mLockCurScreen && mNeedLockFull) {
            mLockScreen?.visible()
            return
        }
        super.changeUiToPauseShow()
        viewTopContainerShadow?.visible()
        mUiCleared = false
    }

    override fun changeUiToClear() {
        super.changeUiToClear()
        viewTopContainerShadow?.invisible()
        mUiCleared = true

        if (vgPlayPosition?.isVisible == true) ivClosePlayPositionTip?.callOnClick()
    }

    //准备中
    override fun changeUiToPreparingShow() {
        super.changeUiToPreparingShow()
        viewTopContainerShadow?.visible()
        mUiCleared = false
    }

    //播放中
    override fun changeUiToPlayingShow() {
        initFirstLoad = false
        // 防止锁定后从桌面进入仍然显示界面的问题
        if (mLockCurScreen && mNeedLockFull) {
            mLockScreen?.visible()
            return
        }
        super.changeUiToPlayingShow()
        viewTopContainerShadow?.visible()
        mUiCleared = false
    }

    //自动播放结束
    override fun changeUiToCompleteShow() {
        super.changeUiToCompleteShow()
        viewTopContainerShadow?.visible()
        mBottomContainer.gone()
        tvTouchDownHighSpeed?.gone()
        mUiCleared = false

        if (vgPlayPosition?.isVisible == true) ivClosePlayPositionTip?.callOnClick()
    }

    override fun changeUiToError() {
        super.changeUiToError()
        viewTopContainerShadow?.invisible()

        if (vgPlayPosition?.isVisible == true) ivClosePlayPositionTip?.callOnClick()
    }

    override fun changeUiToPrepareingClear() {
        super.changeUiToPrepareingClear()
        viewTopContainerShadow?.invisible()
    }

    override fun changeUiToPlayingBufferingClear() {
        super.changeUiToPlayingBufferingClear()
        viewTopContainerShadow?.invisible()
    }

    override fun changeUiToCompleteClear() {
        super.changeUiToCompleteClear()
        viewTopContainerShadow?.invisible()
    }

    override fun changeUiToPlayingBufferingShow() {
        super.changeUiToPlayingBufferingShow()
        viewTopContainerShadow?.visible()
    }

    override fun onVideoPause() {
        super.onVideoPause()
        mVideoAllCallBack.let {
            if (it is MyVideoAllCallBack) it.onVideoPause()
        }
    }

    override fun onVideoResume(seek: Boolean) {
//        super.onVideoResume(seek)
        mPauseBeforePrepared = false
        if (mCurrentState == GSYVideoView.CURRENT_STATE_PAUSE) {
            try {
                clickStartIcon()
                mVideoAllCallBack.let {
                    if (it is MyVideoAllCallBack) it.onVideoResume()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    public override fun clickStartIcon() {
        super.clickStartIcon()

        // 下面是处理完点击后的逻辑
        if (mCurrentState == CURRENT_STATE_PLAYING) {
            onVideoResume()
        }
    }

    override fun onClick(v: View) {
        super.onClick(v)

        when (v.id) {
            // bigger_surface代替原有的surface_container执行点击动作
            R.id.bigger_surface -> {
                vgSettingContainer?.gone()
                vgRightContainer?.gone()
                if (mCurrentState == GSYVideoView.CURRENT_STATE_ERROR) {
                    if (mVideoAllCallBack != null) {
                        Debuger.printfLog("onClickStartError")
                        mVideoAllCallBack.onClickStartError(mOriginUrl, mTitle, this)
                    }
                    prepareVideo()
                } else {
                    if (mVideoAllCallBack != null && isCurrentMediaListener) {
                        if (mIfCurrentIsFullscreen) {
                            Debuger.printfLog("onClickBlankFullscreen")
                            mVideoAllCallBack.onClickBlankFullscreen(mOriginUrl, mTitle, this)
                        } else {
                            Debuger.printfLog("onClickBlank")
                            mVideoAllCallBack.onClickBlank(mOriginUrl, mTitle, this)
                        }
                    }
                    startDismissControlViewTimer()
                }
            }
            R.id.thumb -> {
                vgSettingContainer?.gone()
                vgRightContainer?.gone()
            }
        }
    }

    /**
     * 双击的时候调用此方法
     */
    override fun touchDoubleUp(e: MotionEvent?) {
        // 处理双击前的逻辑
        val oldUiVisibilityState = mBottomContainer?.visibility ?: VISIBLE

        // 处理双击
        super.touchDoubleUp(e)

        // 下面是处理完双击后的逻辑
        if (mCurrentState == CURRENT_STATE_PLAYING) {       // 若双击后是播放状态
            //双击前Ui是什么可见性状态，则双击后Ui还是什么可见性状态，避免双击后Ui突然显示出来
            if (oldUiVisibilityState == VISIBLE) changeUiToPlayingShow()
            else changeUiToPlayingClear()
//            cancelDismissControlViewTimer()
        } else if (mCurrentState == CURRENT_STATE_PAUSE) {  // 若双击后是暂停状态
            //双击前Ui是什么可见性状态，则双击后Ui还是什么可见性状态，避免双击后Ui突然显示出来
            if (oldUiVisibilityState == VISIBLE) changeUiToPauseShow()
            else changeUiToPauseClear()
//            cancelDismissControlViewTimer()
        }
    }

    override fun touchLongPress(e: MotionEvent?) {
        e ?: return
        if (e.pointerCount == 1) {
            // 长按加速
            if (!mLongPressing && e.action == MotionEvent.ACTION_DOWN && !doublePointerZoomingMoving) {
                mLongPressing = true
                // 此处不能设置mPlaySpeed
                setSpeed(2f, true)
                tvTouchDownHighSpeed?.text =
                    mContext.getString(R.string.touch_down_high_speed, "2")
                tvTouchDownHighSpeed?.visible()
            }
        }
    }

    override fun touchSurfaceMoveFullLogic(absDeltaX: Float, absDeltaY: Float) {
        // 全屏下拉任务栏
        if (absDeltaY > mThreshold && absDeltaY > absDeltaX && mDownY <= mStatusBarOffset) {
            cancelProgressTimer()
            return
        }
        super.touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (mIfCurrentIsFullscreen && mLockCurScreen && mNeedLockFull) {
            onClickUiToggle(event)
            startDismissControlViewTimer()
            return true
        }

        // ---长按逻辑开始
        if (event.pointerCount == 1) {
            if (event.action == MotionEvent.ACTION_UP) {
                // 如果刚才在长按，则取消长按
                if (mLongPressing) {
                    mLongPressing = false
                    setSpeed(mPlaySpeed, true)
                    tvTouchDownHighSpeed?.gone()
                    return false
                }
            }
        }
        // ---长按逻辑结束
        // 不是全屏下，不使用双指操作
        if (!mIfCurrentIsFullscreen) return super.onTouch(v, event)
        if (v?.id == R.id.surface_container) {
            if (event.pointerCount > 1 && event.actionMasked == MotionEvent.ACTION_MOVE) {
                // 如果是surface_container并且触摸手指数大于1，则return false拦截
                // 不让super的代码执行，表明正在双指放大移动旋转
                doublePointerZoomingMoving = true
                mDoublePointerZoomMoved = true
                if (!mUiCleared) tvRestoreScreen?.visible()
                // 下面用bigger_surface代替原有的surface_container执行手势动作
                return false
            }
        }
        // 当正在双指操作时，禁止执行super的代码
        if (doublePointerZoomingMoving) {
            tvRestoreScreen?.visible()
            // 如果双指松开，则标志不是在移动
            if (event.action == MotionEvent.ACTION_UP) {
                doublePointerZoomingMoving = false
            }
            return false
        }
        return if (v?.id == R.id.bigger_surface || v?.id == R.id.surface_container) {
            val x = event.x
            val y = event.y
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    touchSurfaceDown(x, y)
                }
                MotionEvent.ACTION_MOVE -> {
                    val deltaX = x - mDownX
                    val deltaY = y - mDownY
                    val absDeltaX = abs(deltaX)
                    val absDeltaY = abs(deltaY)
                    if (mIfCurrentIsFullscreen && mIsTouchWigetFull
                        || mIsTouchWiget && !mIfCurrentIsFullscreen
                    ) {
                        if (!mChangePosition && !mChangeVolume && !mBrightness) {
                            touchSurfaceMoveFullLogic(absDeltaX, absDeltaY)
                        }
                    }
                    touchSurfaceMove(deltaX, deltaY, y)
                }
                MotionEvent.ACTION_UP -> {
                    startDismissControlViewTimer()
                    touchSurfaceUp()
                    Debuger.printfLog(
                        this.hashCode()
                            .toString() + "------------------------------ surface_container ACTION_UP"
                    )
                    startProgressTimer()
                    //不要和隐藏虚拟按键后，滑出虚拟按键冲突
                    if (mHideKey && mShowVKey) return true
                }
            }
            gestureDetector.onTouchEvent(event)
            return false
        } else {
            super.onTouch(v, event)
        }
    }

    override fun onBackFullscreen() {
        if (!mFullAnimEnd) {
            return
        }
        mIfCurrentIsFullscreen = false
        var delay = 0
        if (mOrientationUtils != null) {
            val orientationUtils = mOrientationUtils
            delay = if (orientationUtils is AnimeOrientationUtils)
                orientationUtils.backToProtVideo2()
            else
                orientationUtils.backToProtVideo()
            mOrientationUtils.isEnable = false
            if (mOrientationUtils != null) {
                mOrientationUtils.releaseListener()
                mOrientationUtils = null
            }
        }

        if (!mShowFullAnimation) {
            delay = 0
        }

        val vp = CommonUtil.scanForActivity(context)
            .findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup
        val oldF = vp.findViewById<View>(fullId)
        if (oldF != null) {
            //此处fix bug#265，推出全屏的时候，虚拟按键问题
            val gsyVideoPlayer = oldF as GSYVideoPlayer
            gsyVideoPlayer.isIfCurrentIsFullscreen = false
        }

        mInnerHandler.postDelayed({ backToNormal() }, delay.toLong())
    }

    /**
     * 准备好视频，开始查找进度
     */
    override fun onPrepared() {
        super.onPrepared()
        if (sharedPreferences().getBoolean("restorePlaySpeed", false)) {
            setSpeed(sharedPreferences().getFloat("playSpeed", 1f), true)
        }
        playPositionViewJob?.cancel()
        playPositionMemoryStore?.apply {
            coroutineScope.launch {
                getPlayPosition(mOriginUrl)?.also {
                    preSeekPlayPosition = it
                    playPositionViewJob = launch(Dispatchers.Main) {
                        // 若用户没有设置自动跳转 或者 看完了，才显示提示
                        if (!sharedPreferences()
                                .getBoolean("autoJumpToLastPosition", false) || it == -1L
                        ) {
                            tvPlayPosition?.text = positionFormat(it)
                            vgPlayPosition?.visible()
                            //展示5秒
                            delay(5000)
                            vgPlayPosition?.gone(true, 200L)
                        }
                    }
                }
            }
        }
    }

    /**
     * 1.退出界面时记忆进度
     */
    override fun onDetachedFromWindow() {
        storePlayPosition()
        sharedPreferences().editor { putFloat("playSpeed", mPlaySpeed) }
        super.onDetachedFromWindow()
    }

    /**
     * 2.切换选集时记忆进度
     */
    override fun setUp(
        url: String?,
        cacheWithPlay: Boolean,
        cachePath: File?,
        title: String?,
        changeState: Boolean
    ): Boolean {
        if (url != mOriginUrl) {
            vgPlayPosition?.gone()
            storePlayPosition()
        }

        return super.setUp(url, cacheWithPlay, cachePath, title, changeState)
    }

    /**
     * 1.退出界面时记忆进度
     * 2.切换选集时记忆进度
     *
     * @param position 小于0代表播放完毕（已看完），大于等于0代表正常进度
     *
     * 注意：记忆单位是每个视频而不是一部番剧；一部番剧里面的每集都有记录，并非只记录最后看的那一集
     */
    private fun storePlayPosition(position: Long = gsyVideoManager.currentPosition) {
        val url = mOriginUrl ?: return
        val duration = gsyVideoManager.duration
        var newPosition = position
        // 若还剩10s结束，则直接标记为“看完”
        if (newPosition > 0 && abs(newPosition - duration) <= 10000L) newPosition = -1L
        // 进度为负（已经播放完） 或 当前进度大于最小限制且小于最大限制（播放完时不记录），则记录
        if (newPosition < 0 || (newPosition > playPositionMemoryTimeLimit && duration > 0)) {
            playPositionMemoryStore?.apply {
                coroutineScope.launch {
                    putPlayPosition(url, newPosition)
                }
            }
        }
    }

    override fun onAutoCompletion() {
        super.onAutoCompletion()
        // 播放完毕
        storePlayPosition(-1L)
        if (
            sharedPreferences().getString("switchVideoMode", "StopPlay") ==
            "AutoPlayNextEpisode"
        ) onPlayNextEpisode()
    }

    fun setEpisodeButtonOnClickListener(listener: OnClickListener) {
        mEpisodeButtonOnClickListener = listener
    }

    fun setEpisodeAdapter(adapter: VarietyAdapter) {
        mEpisodeAdapter = adapter
    }

    fun getEpisodeButton() = tvEpisode

    fun getBottomContainer() = mBottomContainer

    fun getClingButton() = ivCling

    fun getRightContainer() = vgRightContainer

    fun setEpisodeButtonVisibility(visibility: Int) {
        tvEpisode?.visibility = visibility
        mEpisodeTextViewVisibility = visibility
    }

    fun enableDismissControlViewTimer(start: Boolean) {
        if (start) super.startDismissControlViewTimer()
        else super.cancelDismissControlViewTimer()
    }

    /**
     * 适配刘海屏，防止重要内容落入刘海内被遮挡
     */
    protected open fun supportDisplayCutouts() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val decorView: View = (activity ?: return).window.decorView
            decorView.post {
                val displayCutout = decorView.rootWindowInsets?.displayCutout ?: return@post
                mTopContainer?.updateSafeInset(displayCutout)
                mBottomContainer?.updateSafeInset(displayCutout)
            }
        }
    }

    @TargetApi(28)
    protected fun View.updateSafeInset(displayCutout: DisplayCutout) {
        coroutineScope.launch(Dispatchers.Main) {
            delay(500)
            val location = IntArray(2)
            getLocationOnScreen(location)
            val left = location[0]
            val right = location[0] + width
            val top = location[1]
            val bottom = location[1] + height

            val insetLeft: Int = getTag(R.id.inset_left) as? Int ?: 0
            val insetRight: Int = getTag(R.id.inset_right) as? Int ?: 0
            val insetTop: Int = getTag(R.id.inset_top) as? Int ?: 0
            val insetBottom: Int = getTag(R.id.inset_bottom) as? Int ?: 0
            val oldPaddingLeft = paddingLeft - insetLeft
            val oldPaddingRight = paddingRight - insetRight
            val oldPaddingTop = paddingTop - insetTop
            val oldPaddingBottom = paddingBottom - insetBottom
            var newPaddingLeft = oldPaddingLeft
            var newPaddingRight = oldPaddingRight
            var newPaddingTop = oldPaddingTop
            var newPaddingBottom = oldPaddingBottom

            // left
            if (!inSafeInset(displayCutout) &&
                left + oldPaddingLeft < displayCutout.safeInsetLeft
            ) {
                val deltaPadding = displayCutout.safeInsetLeft - left - oldPaddingLeft
                newPaddingLeft = oldPaddingLeft + deltaPadding
                setTag(R.id.inset_left, deltaPadding)
            }
            ValueAnimator.ofInt(paddingLeft, newPaddingLeft)
                .setDuration(200)
                .apply {
                    addUpdateListener { animation ->
                        updatePadding(left = animation.animatedValue as Int)
                    }
                }.start()

            // right
            if (!inSafeInset(displayCutout) &&
                right - oldPaddingRight >
                getScreenWidth(true) - displayCutout.safeInsetRight
            ) {
                val deltaPadding = right - oldPaddingRight -
                        (getScreenWidth(true) - displayCutout.safeInsetRight)
                newPaddingRight = oldPaddingRight + deltaPadding
                setTag(R.id.inset_right, deltaPadding)
            }
            ValueAnimator.ofInt(paddingRight, newPaddingRight)
                .setDuration(200)
                .apply {
                    addUpdateListener { animation ->
                        updatePadding(right = animation.animatedValue as Int)
                    }
                }.start()

            // top
            if (!inSafeInset(displayCutout) &&
                top + oldPaddingTop < displayCutout.safeInsetTop
            ) {
                val deltaPadding = displayCutout.safeInsetTop - top - oldPaddingTop
                newPaddingTop = oldPaddingTop + deltaPadding
                setTag(R.id.inset_top, deltaPadding)
            }
            ValueAnimator.ofInt(paddingTop, newPaddingTop)
                .setDuration(200)
                .apply {
                    addUpdateListener { animation ->
                        updatePadding(top = animation.animatedValue as Int)
                    }
                }.start()

            // bottom
            if (!inSafeInset(displayCutout) &&
                bottom - oldPaddingBottom >
                getScreenHeight(true) - displayCutout.safeInsetBottom
            ) {
                val deltaPadding = bottom - oldPaddingBottom -
                        (getScreenHeight(true) - displayCutout.safeInsetBottom)
                newPaddingBottom = oldPaddingBottom + deltaPadding
                setTag(R.id.inset_bottom, deltaPadding)
            }
            ValueAnimator.ofInt(paddingBottom, newPaddingBottom)
                .setDuration(200)
                .apply {
                    addUpdateListener { animation ->
                        updatePadding(bottom = animation.animatedValue as Int)
                    }
                }.start()
        }
    }

    @TargetApi(28)
    protected fun View.inSafeInset(displayCutout: DisplayCutout): Boolean {
        displayCutout.boundingRects.forEach {
            if (overlap(it)) return false
        }
        return true
    }

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        return super.onApplyWindowInsets(insets).also {
            supportDisplayCutouts()
        }
    }

    class Speed1Bean(
        override var route: String,
        var title: String
    ) : BaseBean, Serializable

    class RightRecyclerViewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle = view as TextView
    }

    interface PlayPositionMemoryDataStore {

        suspend fun getPlayPosition(url: String): Long?

        /**
         * @param position 播放进度毫秒，可用GSYVideoViewBridge::currentPosition获取
         */
        @WorkerThread
        suspend fun putPlayPosition(url: String, position: Long)

        @WorkerThread
        suspend fun deletePlayPosition(url: String)

        fun positionFormat(position: Long): String
    }
}