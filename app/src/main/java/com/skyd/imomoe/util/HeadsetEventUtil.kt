package com.skyd.imomoe.util

import android.content.*
import android.media.AudioManager
import com.shuyu.gsyvideoplayer.GSYVideoManager

//lateinit var mediaSession: MediaSession

// 断开耳机后暂停播放视频、点击耳机按钮控制播放
fun Context.initHeadsetEventReceiver() {
    registerReceiver(
        HeadsetEventReceiver(),
        IntentFilter().apply {
            addAction(AudioManager.ACTION_AUDIO_BECOMING_NOISY)
        }
    )

//    mediaSession = MediaSession(this, packageName)
//    val mediaComponent = ComponentName(packageName, MediaButtonReceiver::class.java.name)
//    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//        mediaSession.setMediaButtonBroadcastReceiver(mediaComponent)
//    } else {
//        (getSystemService(AUDIO_SERVICE) as? AudioManager)
//            ?.registerMediaButtonEventReceiver(mediaComponent)
//    }
//    /* set flags to handle media buttons */
//    mediaSession.setFlags(
//        MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or
//                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS
//    )
//    mediaSession.setPlaybackState(
//        PlaybackState
//            .Builder()
//            .setState(
//                playerState2PlaybackState(GSYVideoManager.instance().lastState),
//                GSYVideoManager.instance().currentPosition,
//                1f
//            )
//            .build()
//    )
//    mediaSession.setCallback(object : MediaSession.Callback() {
//        override fun onMediaButtonEvent(intent: Intent): Boolean {
//            if (Intent.ACTION_MEDIA_BUTTON != intent.action) {
//                return super.onMediaButtonEvent(intent)
//            }
//            val event: KeyEvent? = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT)
//            return if (event == null || event.action != KeyEvent.ACTION_UP) {
//                super.onMediaButtonEvent(intent)
//            } else true
//        }
//
//        override fun onPlay() {
//            super.onPlay()
//            GSYVideoManager.onResume()
//        }
//
//        override fun onPause() {
//            super.onPause()
//            GSYVideoManager.onPause()
//        }
//    })
//
//    if (!mediaSession.isActive) {
//        mediaSession.isActive = true
//    }
}
//
//private fun playerState2PlaybackState(playerState: Int): Int {
//    return when (playerState) {
//        GSYVideoView.CURRENT_STATE_PLAYING -> PlaybackState.STATE_PLAYING
//        GSYVideoView.CURRENT_STATE_ERROR -> PlaybackState.STATE_ERROR
//        GSYVideoView.CURRENT_STATE_PAUSE -> PlaybackState.STATE_PAUSED
//        GSYVideoView.CURRENT_STATE_PREPAREING -> PlaybackState.STATE_CONNECTING
//        GSYVideoView.CURRENT_STATE_NORMAL -> PlaybackState.STATE_NONE
//        GSYVideoView.CURRENT_STATE_PLAYING_BUFFERING_START -> PlaybackState.STATE_BUFFERING
//        GSYVideoView.CURRENT_STATE_AUTO_COMPLETE -> PlaybackState.STATE_STOPPED
//        else -> PlaybackState.STATE_NONE
//    }
//}

// 使用代码动态注册
class HeadsetEventReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        intent ?: return
        when (intent.action) {
            AudioManager.ACTION_AUDIO_BECOMING_NOISY -> {
                GSYVideoManager.onPause()
            }
        }
    }
}