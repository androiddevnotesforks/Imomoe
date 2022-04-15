package com.skyd.imomoe.view.fragment.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentMoreDialogBinding

open class MoreDialogFragment : BaseBottomSheetDialogFragment<FragmentMoreDialogBinding>() {
    companion object {
        const val TAG = "MoreDialogFragment"
    }

    private var onCancelButtonClick: ((v: View) -> Unit)? = null
    private var onDlnaButtonClick: ((v: View) -> Unit)? = null
    private var onOpenInOtherPlayerButtonClick: ((v: View) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.apply {
            tvCancelMore.setOnClickListener { onCancelButtonClick?.invoke(it) }
            tvDlna.setOnClickListener { onDlnaButtonClick?.invoke(it) }
            tvOpenInOtherPlayer.setOnClickListener { onOpenInOtherPlayerButtonClick?.invoke(it) }
        }
    }

    fun onCancelButtonClick(listener: ((v: View) -> Unit)? = null) {
        onCancelButtonClick = listener
    }

    fun onDlnaButtonClick(listener: ((v: View) -> Unit)? = null) {
        onDlnaButtonClick = listener
    }

    fun onOpenInOtherPlayerButtonClick(listener: ((v: View) -> Unit)? = null) {
        onOpenInOtherPlayerButtonClick = listener
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentMoreDialogBinding.inflate(layoutInflater)
}