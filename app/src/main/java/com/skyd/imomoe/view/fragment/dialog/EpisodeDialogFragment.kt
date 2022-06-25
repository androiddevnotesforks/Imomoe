package com.skyd.imomoe.view.fragment.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.skyd.imomoe.R
import com.skyd.imomoe.bean.AnimeEpisode1Bean
import com.skyd.imomoe.databinding.FragmentEpisodeDialogBinding
import com.skyd.imomoe.util.AnimeEpisode1ViewHolder
import com.skyd.imomoe.view.adapter.decoration.AnimeEpisodeItemDecoration
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.AnimeEpisode1Proxy

open class EpisodeDialogFragment(
    private val backgroundDim: Boolean = true,
    private val offsetFromTop: Int? = null,
    private val afterViewCreated: (EpisodeDialogFragment.() -> Unit)? = null
) : BaseBottomSheetDialogFragment<FragmentEpisodeDialogBinding>() {
    companion object {
        const val TAG = "EpisodeDialogFragment"
    }

    private var onEpisodeClick: ((
        holder: AnimeEpisode1ViewHolder,
        data: AnimeEpisode1Bean,
        index: Int
    ) -> Unit)? = null

    private val adapter = VarietyAdapter(
        mutableListOf(AnimeEpisode1Proxy(onClickListener = { holder, data, index ->
            onEpisodeClick?.invoke(holder, data, index)
        }, width = ViewGroup.LayoutParams.MATCH_PARENT))
    )

    var title: String
        get() = mBinding.tvTitleEpisodeDialogFragment.text.toString()
        set(value) {
            mBinding.tvTitleEpisodeDialogFragment.text = value
        }

    var dataList: List<Any>
        get() = adapter.dataList
        set(value) {
            adapter.dataList = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(
            STYLE_NORMAL,
            if (backgroundDim) R.style.BottomSheetDialogTheme
            else R.style.BottomSheetDialogTheme_NoBackgroundDim
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            btnDismissEpisodeDialogFragment.setOnClickListener { dismiss() }
            rvTitleEpisodeDialogFragment.layoutManager = GridLayoutManager(activity, 3)
            if (rvTitleEpisodeDialogFragment.itemDecorationCount == 0) {
                rvTitleEpisodeDialogFragment.addItemDecoration(AnimeEpisodeItemDecoration())
            }
            rvTitleEpisodeDialogFragment.adapter = adapter
        }
        if (offsetFromTop != null) {
            (dialog as? BottomSheetDialog)?.behavior?.maxHeight =
                requireActivity().findViewById<View>(android.R.id.content).height - offsetFromTop
        }
        afterViewCreated?.invoke(this)
    }

    fun onEpisodeClick(
        listener: ((
            holder: AnimeEpisode1ViewHolder,
            data: AnimeEpisode1Bean,
            index: Int
        ) -> Unit)? = null
    ) {
        onEpisodeClick = listener
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentEpisodeDialogBinding.inflate(layoutInflater)
}