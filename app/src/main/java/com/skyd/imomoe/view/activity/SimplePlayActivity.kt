package com.skyd.imomoe.view.activity

import android.net.Uri
import android.os.Bundle
import android.view.View
import com.shuyu.gsyvideoplayer.GSYVideoManager
import com.shuyu.gsyvideoplayer.listener.GSYSampleCallBack
import com.shuyu.gsyvideoplayer.model.VideoOptionModel
import com.shuyu.gsyvideoplayer.utils.OrientationUtils
import com.skyd.imomoe.databinding.ActivitySimplePlayBinding
import com.skyd.imomoe.ext.fileName
import com.skyd.imomoe.ext.gone
import com.skyd.imomoe.util.Util.setFullScreen
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFullScreen(window)

        val uri: Uri? = intent.data
        url = intent.getStringExtra(URL)
            .orEmpty()
            .ifBlank { uri?.toString().orEmpty() }

        animeTitle = intent.getStringExtra(ANIME_TITLE)
            .orEmpty()
            .ifBlank { uri?.fileName(contentResolver).orEmpty() }

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
                    this@run.currentPlayer.seekRatio = this@run.currentPlayer.duration / 90_000f
                }
            })
            this.animeTitle = this@SimplePlayActivity.animeTitle
            setUp(url, false, episodeTitle)
        }
    }

    override fun onPause() {
        super.onPause()
        orientationUtils.setIsPause(true)
        mBinding.avpSimplePlayActivity.currentPlayer.onVideoPause()
    }

    override fun onResume() {
        super.onResume()
        orientationUtils.setIsPause(false)
        mBinding.avpSimplePlayActivity.currentPlayer.onVideoResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.avpSimplePlayActivity.currentPlayer.release()
        mBinding.avpSimplePlayActivity.setVideoAllCallBack(null)
        GSYVideoManager.releaseAllVideos()
        orientationUtils.releaseListener()
    }
}
