package com.skyd.imomoe.view.fragment.dialog

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentSendDanmakuFontDialogBinding
import com.skyd.imomoe.view.component.player.danmaku.DanmakuMode
import vadiole.colorpicker.ColorModel
import vadiole.colorpicker.ColorPickerDialog


class SendDanmakuFontDialogFragment(
    var danmakuMode: DanmakuMode = DanmakuMode.Scroll,
    var danmakuColor: Int = Color.WHITE,
    private val callback: ((DanmakuMode, Int) -> Unit)? = null
) : BaseDialogFragment<FragmentSendDanmakuFontDialogBinding>() {
    companion object {
        const val TAG = "SendDanmakuFontDialogFragment"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dialog?.window?.apply {
            // make dialog itself transparent
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            // background dim
            setDimAmount(0f)
        }

        mBinding.apply {
            tgDanmakuMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (isChecked) {
                    when (checkedId) {
                        R.id.btn_danmaku_mode_scroll -> danmakuMode = DanmakuMode.Scroll
                        R.id.btn_danmaku_mode_top -> danmakuMode = DanmakuMode.Top
                        R.id.btn_danmaku_mode_bottom -> danmakuMode = DanmakuMode.Bottom
                    }
                }
            }

            tgDanmakuMode.check(
                when (danmakuMode) {
                    DanmakuMode.Scroll -> R.id.btn_danmaku_mode_scroll
                    DanmakuMode.Top -> R.id.btn_danmaku_mode_top
                    DanmakuMode.Bottom -> R.id.btn_danmaku_mode_bottom
                }
            )
            cvDanmakuColor.setCardBackgroundColor(danmakuColor)
            cvDanmakuColor.setOnClickListener {
                ColorPickerDialog.Builder()
                    .setInitialColor(danmakuColor)
                    .setColorModel(ColorModel.HSV)
                    .setColorModelSwitchEnabled(true)
                    .setButtonOkText(R.string.ok)
                    .setButtonCancelText(R.string.cancel)
                    .onColorSelected { color: Int ->
                        danmakuColor = color
                        cvDanmakuColor.setCardBackgroundColor(color)
                    }
                    .create()
                    .show(childFragmentManager, "color_picker")
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callback?.invoke(danmakuMode, danmakuColor)
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentSendDanmakuFontDialogBinding.inflate(layoutInflater)
}