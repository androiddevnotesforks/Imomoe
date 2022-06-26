package com.skyd.imomoe.view.component.player.danmaku

import com.skyd.imomoe.view.component.player.danmaku.anime.AnimeDanmakuRepository
import com.skyd.imomoe.view.component.player.danmaku.bili.BilibiliDanmakuRepository

sealed class DanmakuType<T : DanmakuRepository>(var repository: T? = null) {
    class AnimeType(
        repository: AnimeDanmakuRepository? = null
    ) : DanmakuType<AnimeDanmakuRepository>(repository)

    class BilibiliType(
        repository: BilibiliDanmakuRepository? = null
    ) : DanmakuType<BilibiliDanmakuRepository>(repository)
}

