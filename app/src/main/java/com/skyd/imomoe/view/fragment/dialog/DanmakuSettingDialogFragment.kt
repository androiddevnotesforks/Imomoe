package com.skyd.imomoe.view.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.annotation.IntRange
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.skyd.imomoe.databinding.FragmentDanmakuSettingDialogBinding
import com.skyd.imomoe.ext.percentage
import com.skyd.imomoe.view.listener.dsl.setOnSeekBarChangeListener
import java.io.Serializable


class DanmakuSettingDialogFragment(
    private val fillParentWidth: Boolean = true,
    private var filter: ShowDanmakuType = ShowDanmakuType(),
    private var allowOverlap: Boolean = true,
    @IntRange(from = 0, to = 100)
    private var danmakuAlpha: Int = 100,
    @IntRange(from = MIN_DANMAKU_SCALE.toLong())
    private var danmakuScale: Int = MIN_DANMAKU_SCALE + 60,
    private var danmakuBold: Boolean = true,
    private val onDanmakuFilterChanged: ((filter: ShowDanmakuType) -> Unit)? = null,
    private val onAllowOverlapChanged: ((allowOverlap: Boolean) -> Unit)? = null,
    private val onDanmakuAlphaChanged: ((danmakuAlpha: Int) -> Unit)? = null,
    private val onDanmakuScaleChanged: ((danmakuScale: Int) -> Unit)? = null,
    private val onDanmakuBoldChanged: ((danmakuBold: Boolean) -> Unit)? = null,
) : BaseDialogFragment<FragmentDanmakuSettingDialogBinding>() {
    companion object {
        const val TAG = "DanmakuSettingDialogFragment"
        const val MIN_DANMAKU_SCALE: Int = 70
    }

    override fun onStart() {
        super.onStart()
        if (fillParentWidth) {
            dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            // make dialog itself transparent
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // background dim
            setDimAmount(0f)
        }

        mBinding.apply {
            chipDanmakuTypeScroll.isChecked = !filter.scroll
            chipDanmakuTypeTop.isChecked = !filter.top
            chipDanmakuTypeBottom.isChecked = !filter.bottom
            chipDanmakuTypeColor.isChecked = !filter.color

            switchAllowOverlap.isChecked = allowOverlap

            switchBoldDanmaku.isChecked = danmakuBold

            sbDanmakuTextSizeScale.progress = danmakuScale - MIN_DANMAKU_SCALE
            tvDanmakuTextSizeScale.text = danmakuScale.percentage

            sbDanmakuAlpha.progress = danmakuAlpha
            tvDanmakuAlpha.text = danmakuAlpha.percentage

            chipDanmakuTypeScroll.setOnCheckedChangeListener { _, isChecked ->
                filter.scroll = !isChecked
                onDanmakuFilterChanged?.invoke(filter)
            }
            chipDanmakuTypeTop.setOnCheckedChangeListener { _, isChecked ->
                filter.top = !isChecked
                onDanmakuFilterChanged?.invoke(filter)
            }
            chipDanmakuTypeBottom.setOnCheckedChangeListener { _, isChecked ->
                filter.bottom = !isChecked
                onDanmakuFilterChanged?.invoke(filter)
            }
            chipDanmakuTypeColor.setOnCheckedChangeListener { _, isChecked ->
                filter.color = !isChecked
                onDanmakuFilterChanged?.invoke(filter)
            }

            switchAllowOverlap.setOnCheckedChangeListener { _, isChecked ->
                allowOverlap = isChecked
                onAllowOverlapChanged?.invoke(allowOverlap)
            }

            switchBoldDanmaku.setOnCheckedChangeListener { _, isChecked ->
                danmakuBold = isChecked
                onDanmakuBoldChanged?.invoke(danmakuBold)
            }

            sbDanmakuAlpha.setOnSeekBarChangeListener {
                onProgressChanged { seekBar, progress, _ ->
                    seekBar ?: return@onProgressChanged
                    danmakuAlpha = progress
                    tvDanmakuAlpha.text = danmakuAlpha.percentage
                    onDanmakuAlphaChanged?.invoke(danmakuAlpha)
                }
            }

            sbDanmakuTextSizeScale.setOnSeekBarChangeListener {
                onProgressChanged { seekBar, progress, _ ->
                    seekBar ?: return@onProgressChanged
                    danmakuScale = progress + MIN_DANMAKU_SCALE
                    tvDanmakuTextSizeScale.text = danmakuScale.percentage
                    onDanmakuScaleChanged?.invoke(danmakuScale)
                }
            }
        }
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentDanmakuSettingDialogBinding.inflate(layoutInflater)

    class ShowDanmakuType(
        // 显示是true，不显示是false
        var scroll: Boolean = true,
        var top: Boolean = true,
        var bottom: Boolean = true,
        var color: Boolean = true
    ) : Serializable {
        fun toList(): List<Pair<Boolean, Int>> {
            return arrayListOf(
                scroll to DanmakuItemData.DANMAKU_MODE_ROLLING,
                top to DanmakuItemData.DANMAKU_MODE_CENTER_TOP,
                bottom to DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM,
                color to -1
            )
        }
    }
}