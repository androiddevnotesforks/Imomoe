package com.skyd.imomoe.view.activity

import android.os.Bundle
import com.skyd.imomoe.databinding.ActivityNoticeBinding
import com.skyd.imomoe.ext.toHtml
import java.io.UnsupportedEncodingException
import java.net.URLDecoder

class NoticeActivity : BaseActivity<ActivityNoticeBinding>() {
    companion object {
        const val PARAM = "param"
        const val TOOLBAR_TITLE = "toolbarTitle"
        const val TITLE = "title"
        const val CONTENT = "content"
    }

    private val paramMap: HashMap<String, String> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (intent.getStringExtra(PARAM) ?: "").split("&").forEachIndexed { index, s ->
            s.split("=").let {
                if (it.size != 2) return@let
                try {
                    // 此处URL解码，因此要求传入的参数需要经过URL编码！！！
                    paramMap[it[0]] = URLDecoder.decode(it[1], "UTF-8")
                } catch (e: UnsupportedEncodingException) {
                    e.printStackTrace()
                }
            }
        }

        mBinding.run {
            tbNoticeActivity.run {
                setNavigationOnClickListener { finish() }
                title = paramMap[TOOLBAR_TITLE] ?: "通知"
            }
            tvNoticeActivityTitle.text = paramMap[TITLE].orEmpty()
            tvNoticeActivityContent.text = (paramMap[CONTENT].orEmpty()).toHtml()
        }
    }

    override fun getBinding(): ActivityNoticeBinding = ActivityNoticeBinding.inflate(layoutInflater)
}