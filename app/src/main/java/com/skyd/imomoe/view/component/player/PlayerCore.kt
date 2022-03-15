package com.skyd.imomoe.view.component.player

import android.app.Activity
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.list.listItemsSingleChoice
import com.shuyu.gsyvideoplayer.player.IPlayerManager
import com.shuyu.gsyvideoplayer.player.IjkPlayerManager
import com.shuyu.gsyvideoplayer.player.PlayerFactory
import com.shuyu.gsyvideoplayer.player.SystemPlayerManager
import com.shuyu.gsyvideoplayer.utils.GSYVideoType
import com.skyd.imomoe.App
import com.skyd.imomoe.R
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.util.logI
import tv.danmaku.ijk.media.exo2.Exo2PlayerManager
import java.lang.IllegalStateException


object PlayerCore {
    class Core(val coreName: String, val playManager: Class<out IPlayerManager>) : CharSequence {
        override val length: Int
            get() = playManager.name.length

        override fun get(index: Int): Char = playManager.name[index]

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
            return playManager.name.subSequence(startIndex, endIndex)
        }

        override fun toString(): String = coreName

        override fun equals(other: Any?): Boolean {
            return when (other) {
                null -> false
                is String -> other == playManager.name
                is Core -> other.coreName == this.coreName && other.playManager == this.playManager
                else -> false
            }
        }

        override fun hashCode(): Int {
            var result = coreName.hashCode()
            result = 31 * result + playManager.hashCode()
            return result
        }
    }

    private infix fun String.to(that: Class<out IPlayerManager>): Core = Core(this, that)

    val playerCores: List<Core> = listOf(
        "ijk内核 (默认)" to IjkPlayerManager::class.java,
        "ExoPlayer内核" to Exo2PlayerManager::class.java,
        "系统内核" to SystemPlayerManager::class.java
    )

    var playerCore: Core = playerCores.first {
        it.equals(App.context.sharedPreferences().getString("playerCore", null)
            .run { this ?: IjkPlayerManager::class.java.name })
    }.also { PlayerFactory.setPlayManager(it.playManager) }
        set(value) {
            if (value == field) return
            App.context.sharedPreferences()
                .editor { putString("playerCore", value.playManager.name) }
            field = value
            PlayerFactory.setPlayManager(value.playManager)
        }

    // 保证这个函数内调用一次applyMediaCodec
    fun onAppCreate() {
        applyMediaCodec()
        logI("PlayerCore initialized: ${playerCore.coreName} ${playerCore.playManager.name}")
    }

    fun Activity.selectPlayerCore(onPositive: ((Core) -> Unit)? = null) {
        var initialSelection = 0
        playerCores.forEachIndexed { index, s ->
            if (s == playerCore) initialSelection = index
        }
        MaterialDialog(this).listItemsSingleChoice(
            items = playerCores,
            initialSelection = initialSelection
        ) { _, index, _ ->
            playerCore = playerCores[index]
            onPositive?.invoke(playerCores[index])
        }.positiveButton(R.string.ok).negativeButton(R.string.cancel).show {
            title(res = R.string.select_player_core)
        }
    }

    /**
     * 设置硬解码，只有ijk内核生效
     */
    fun setMediaCodec(enable: Boolean) {
        if (GSYVideoType.isMediaCodec() == enable && GSYVideoType.isMediaCodecTexture() == enable) {
            return
        }
        if (enable) {
            GSYVideoType.enableMediaCodec()
            GSYVideoType.enableMediaCodecTexture()
        } else {
            GSYVideoType.disableMediaCodec()
            GSYVideoType.disableMediaCodecTexture()
        }
        App.context.sharedPreferences().editor { putBoolean("mediaCodec", enable) }
    }

    fun applyMediaCodec() {
        setMediaCodec(App.context.sharedPreferences().getBoolean("mediaCodec", false))
    }
}