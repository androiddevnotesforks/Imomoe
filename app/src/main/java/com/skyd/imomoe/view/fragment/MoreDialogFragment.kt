package com.skyd.imomoe.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentMoreDialogBinding

open class MoreDialogFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMoreDialogBinding? = null
    private val mBinding get() = _binding!!
    private var onCancelButtonClick: ((v: View) -> Unit)? = null
    private var onDlnaButtonClick: ((v: View) -> Unit)? = null
    private var onOpenInOtherPlayerButtonClick: ((v: View) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMoreDialogBinding.inflate(inflater, container, false).apply {
            tvCancelMore.setOnClickListener { onCancelButtonClick?.invoke(it) }
            tvDlna.setOnClickListener { onDlnaButtonClick?.invoke(it) }
            tvOpenInOtherPlayer.setOnClickListener { onOpenInOtherPlayerButtonClick?.invoke(it) }
        }
        return mBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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
}