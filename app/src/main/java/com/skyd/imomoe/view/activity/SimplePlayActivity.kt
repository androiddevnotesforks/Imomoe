package com.skyd.imomoe.view.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView.CURRENT_STATE_NORMAL
import com.shuyu.gsyvideoplayer.video.base.GSYVideoView.CURRENT_STATE_PAUSE
import com.skyd.imomoe.databinding.ActivitySimplePlayBinding
import com.skyd.imomoe.ext.fileName
import com.skyd.imomoe.ext.getMediaTitle
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.util.Util.setFullScreen
import com.skyd.imomoe.view.component.player.danmaku.DanmakuManager
import tv.danmaku.ijk.media.player.IjkMediaPlayer


class SimplePlayActivity : BaseActivity<ActivitySimplePlayBinding>() {
    companion object {
        const val URL = "url"
        const val ANIME_TITLE = "animeTitle"
        const val EPISODE_TITLE = "episodeTitle"
    }

    private var url = ""
    private var animeTitle = ""
    private var episodeTitle = ""
    private lateinit var orientationUtils: OrientationUtils

    // 是否是在onPause方法里自动暂停的
    private var isPause = false

    private var onPausePosition: Long = 0
    private var onPauseState: Int = 0

    // 是否播放过视频
    private var startedPlayVideo: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(window)

        val uri: Uri? = intent.data
        animeTitle = intent.getStringExtra(ANIME_TITLE)
            .orEmpty()
            .ifBlank { uri?.fileName(contentResolver).orEmpty() }

        url = intent.getStringExtra(URL)
            .orEmpty()
            .ifBlank {
                if (uri != null) {
                    getMediaTitle(uri)?.let { animeTitle = it }
                    uri.toString()
                } else ""
            }

        episodeTitle = intent.getStringExtra(EPISODE_TITLE)
            .orEmpty()
            .ifBlank { animeTitle }

        init()

        mBinding.avpSimplePlayActivity.run {
            val title = episodeTitle
            titleTextView?.text = title
            fullWindowPlayer?.titleTextView?.text = title
            startPlayLogic()
            startWindowFullscreen(
                this@SimplePlayActivity,
                actionBar = true,
                statusBar = true
            )
        }

        val videoOptionModel =
            VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1)
        GSYVideoManager.instance().optionModelList = listOf(videoOptionModel)
    }

    override fun getBinding() = ActivitySimplePlayBinding.inflate(layoutInflater)

    private fun init() {
        mBinding.avpSimplePlayActivity.run {
            // 设置是否启用自带弹幕功能
            DanmakuManager.enableDanmaku =
                sharedPreferences().getBoolean("enableDanmakuInLocalVideo", false)
            // 设置旋转
            orientationUtils = OrientationUtils(this@SimplePlayActivity, this)
            // 进横屏旋转，不会竖屏
//            orientationUtils.isOnlyRotateLand = true
            // 锁定后不随屏幕旋转而旋转视频
            setLockClickListener { _, lock ->
                orientationUtils.isEnable = !lock
                currentPlayer.isRotateViewAuto = !lock
            }
            setEpisodeButtonVisibility(View.GONE)
            fullscreenButton.gone()
            dismissControlTime = 5000
            // 是否开启自动旋转
            isRotateViewAuto = false
            // 是否需要全屏锁定屏幕功能
            isIfCurrentIsFullscreen = true
            isNeedLockFull = true
            // 不要全屏时从左上角放大的动画
            isShowFullAnimation = false
            // 设置退出全屏的监听器
            setBackFromFullScreenListener { finish() }
            // 是否可以滑动调整
            setIsTouchWiget(true)
            setVideoAllCallBack(object : GSYSampleCallBack() {
                override fun onPrepared(url: String?, vararg objects: Any?) {
                    super.onPrepared(url, *objects)
                    startedPlayVideo = true
                    isPause = false
                    this@run.currentPlayer.seekRatio = this@run.currentPlayer.duration / 90_000f
                }
            })
            this.animeTitle = this@SimplePlayActivity.animeTitle
            setUp(url, false, episodeTitle)
        }
    }

    override fun onPause() {
        super.onPause()

        mBinding.avpSimplePlayActivity.currentPlayer.apply {
            onPauseState = currentState
            onPausePosition = currentPositionWhenPlaying

            if (currentState != CURRENT_STATE_PAUSE) {
                onVideoPause()
                orientationUtils.setIsPause(true)
                isPause = true
            }
        }
    }

    override fun onResume() {
        super.onResume()

        orientationUtils.setIsPause(false)
        mBinding.avpSimplePlayActivity.currentPlayer.apply {
            if (currentState == CURRENT_STATE_NORMAL &&
                onPausePosition != -1L &&
                onPauseState != -1 &&
                startedPlayVideo
            ) {
                seekOnStart = onPausePosition
                startPlayLogic()
                isPause = false
                if (onPauseState == CURRENT_STATE_PAUSE) {
                    onVideoPause()
                    isPause = true
                }
            } else {
                if (isPause) {
                    onVideoResume()
                    orientationUtils.setIsPause(false)
                    isPause = false
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.avpSimplePlayActivity.currentPlayer.release()
        mBinding.avpSimplePlayActivity.setVideoAllCallBack(null)
        orientationUtils.releaseListener()
    }
}
