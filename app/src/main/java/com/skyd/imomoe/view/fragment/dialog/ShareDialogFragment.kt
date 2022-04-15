package com.skyd.imomoe.view.fragment.dialog

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skyd.imomoe.R
import com.skyd.imomoe.databinding.FragmentShareDialogBinding
import com.skyd.imomoe.util.Share.SHARE_LINK
import com.skyd.imomoe.util.Share.SHARE_QQ
import com.skyd.imomoe.util.Share.SHARE_WECHAT
import com.skyd.imomoe.util.Share.SHARE_WEIBO
import com.skyd.imomoe.util.Share.share

open class ShareDialogFragment : BaseBottomSheetDialogFragment<FragmentShareDialogBinding>() {
    private lateinit var shareContent: String
    private lateinit var attachedActivity: Activity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let { act ->
            attachedActivity = act
            mBinding.tvToQq.setOnClickListener {
                share(attachedActivity, shareContent, SHARE_QQ)
                dismiss()
            }
            mBinding.tvToWechat.setOnClickListener {
                share(attachedActivity, shareContent, SHARE_WECHAT)
                dismiss()
            }
            mBinding.tvToWeibo.setOnClickListener {
                share(attachedActivity, shareContent, SHARE_WEIBO)
                dismiss()
            }
            mBinding.tvCopyLink.setOnClickListener {
                share(attachedActivity, shareContent, SHARE_LINK)
                dismiss()
            }
            mBinding.tvCancelShare.setOnClickListener {
                dismiss()
            }
        }
    }

    fun setShareContent(shareContent: String): BottomSheetDialogFragment {
        this.shareContent = shareContent
        return this
    }

    override fun getBinding(inflater: LayoutInflater, container: ViewGroup?) =
        FragmentShareDialogBinding.inflate(layoutInflater)
}