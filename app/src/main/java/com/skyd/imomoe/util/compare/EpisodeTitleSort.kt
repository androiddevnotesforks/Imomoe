package com.skyd.imomoe.util.compare

import android.app.Activity
import com.skyd.imomoe.R
import com.skyd.imomoe.appContext
import com.skyd.imomoe.ext.editor
import com.skyd.imomoe.ext.sharedPreferences
import com.skyd.imomoe.ext.showListDialog


object EpisodeTitleSort {

    sealed class EpisodeTitleSortMode : CharSequence {
        object Ascending : EpisodeTitleSortMode()
        object Descending : EpisodeTitleSortMode()
        object Default : EpisodeTitleSortMode()

        companion object {
            fun getFromKey(s: String): EpisodeTitleSortMode = when (s) {
                "Ascending" -> Ascending
                "Descending" -> Descending
                "Default" -> Default
                else -> Ascending
            }
        }

        override fun toString(): String = name

        override fun get(index: Int): Char = name[index]

        override fun subSequence(startIndex: Int, endIndex: Int): CharSequence =
            name.subSequence(startIndex, endIndex)

        override val length: Int
            get() = name.length

        val name by lazy {
            when (this) {
                is Ascending -> appContext.getString(R.string.episode_title_sort_ascending)
                is Descending -> appContext.getString(R.string.episode_title_sort_descending)
                is Default -> appContext.getString(R.string.episode_title_sort_default)
            }
        }

        val key by lazy {
            when (this) {
                is Ascending -> "Ascending"
                is Descending -> "Descending"
                is Default -> "Default"
            }
        }
    }

    fun <T : Comparable<T>> MutableList<T>.sortEpisodeTitle(mode: EpisodeTitleSortMode = episodeTitleSortMode): List<T> {
        when (mode) {
            is EpisodeTitleSortMode.Ascending -> {
                EpisodeTitleCompareUtil.asc = true
                sort()
            }
            is EpisodeTitleSortMode.Descending -> {
                EpisodeTitleCompareUtil.asc = false
                sort()
            }
            is EpisodeTitleSortMode.Default -> {}
        }
        return this
    }

    var episodeTitleSortMode: EpisodeTitleSortMode =
        EpisodeTitleSortMode.getFromKey(
            sharedPreferences().getString("episodeTitleSortMode", null).orEmpty()
        )
        set(value) {
            if (value == field) return
            sharedPreferences().editor { putString("episodeTitleSortMode", value.key) }
            field = value
        }

    fun Activity.selectEpisodeTitleSortMode(onPositive: ((EpisodeTitleSortMode) -> Unit)? = null) {
        var initialSelection = 0
        val items = listOf(
            EpisodeTitleSortMode.Default,
            EpisodeTitleSortMode.Ascending,
            EpisodeTitleSortMode.Descending
        )
        items.forEachIndexed { index, s -> if (s == episodeTitleSortMode) initialSelection = index }
        showListDialog(
            title = getString(R.string.select_episode_title_sort_mode),
            items = items,
            checkedItem = initialSelection
        ) { _, _, itemIndex ->
            episodeTitleSortMode = items[itemIndex]
            onPositive?.invoke(items[itemIndex])
        }
    }
}