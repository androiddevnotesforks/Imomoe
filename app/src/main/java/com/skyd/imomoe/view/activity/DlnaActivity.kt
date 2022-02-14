package com.skyd.imomoe.view.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.skyd.imomoe.databinding.ActivityDlnaBinding
import com.skyd.imomoe.util.Util.getRedirectUrl
import com.skyd.imomoe.util.dlna.Utils.isLocalMediaAddress
import com.skyd.imomoe.util.dlna.dmc.DLNACastManager
import com.skyd.imomoe.util.dlna.dmc.OnDeviceRegistryListenerDsl
import com.skyd.imomoe.util.dlna.dmc.registerDeviceListener
import com.skyd.imomoe.util.dlna.dmc.unregisterListener
import com.skyd.imomoe.util.logI
import com.skyd.imomoe.view.adapter.variety.VarietyAdapter
import com.skyd.imomoe.view.adapter.variety.proxy.UpnpDevice1Proxy
import com.skyd.imomoe.viewmodel.UpnpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DlnaActivity : BaseActivity<ActivityDlnaBinding>() {
    private val viewModel: UpnpViewModel by viewModels()
    private val adapter: VarietyAdapter by lazy {
        VarietyAdapter(mutableListOf(UpnpDevice1Proxy(
            onClickListener = { _, data, _ ->
                val key = System.currentTimeMillis().toString()
                DlnaControlActivity.deviceHashMap[key] = data
                startActivity(
                    Intent(this, DlnaControlActivity::class.java)
                        .putExtra("url", url)
                        .putExtra("title", title)
                        .putExtra("deviceKey", key)
                )
            }
        )), viewModel.deviceList)
    }
    lateinit var title: String
    lateinit var url: String
    private val deviceRegistryListener: OnDeviceRegistryListenerDsl.() -> Unit = {
        onDeviceRemoved { device ->
            val index = viewModel.deviceList.indexOf(device)
            if (index != -1) {
                viewModel.deviceList.removeAt(index)
                adapter.notifyItemRemoved(index)
            }
        }

        onDeviceAdded { device ->
            val index = viewModel.deviceList.indexOf(device)
            if (index == -1) {
                viewModel.deviceList.add(device)
                adapter.notifyItemInserted(viewModel.deviceList.size - 1)
            }
        }
    }

    companion object {
        const val TAG = "DlnaActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        url = intent.getStringExtra("url").orEmpty()
        title = intent.getStringExtra("title").orEmpty()

        logI(TAG, url)

        mBinding.run {
            atbDlnaActivity.setBackButtonClickListener { finish() }

            rvDlnaActivityDevice.layoutManager = LinearLayoutManager(this@DlnaActivity)
            rvDlnaActivityDevice.adapter = adapter
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // 视频不是本地文件
            if (!url.isLocalMediaAddress()) {
                url = getRedirectUrl(this@DlnaActivity.url)
            }

            DLNACastManager.instance.registerDeviceListener(deviceRegistryListener)
            DLNACastManager.instance.search(DLNACastManager.DEVICE_TYPE_DMR)
        }
    }

    override fun onStart() {
        super.onStart()
        DLNACastManager.instance.bindCastService(this)
    }

    override fun onStop() {
        DLNACastManager.instance.bindCastService(this)
        super.onStop()
    }

    override fun onDestroy() {
        DLNACastManager.instance.unregisterListener(deviceRegistryListener)
        super.onDestroy()
    }

    override fun getBinding(): ActivityDlnaBinding = ActivityDlnaBinding.inflate(layoutInflater)

    @SuppressLint("NotifyDataSetChanged")
    override fun onChangeSkin() {
        super.onChangeSkin()
        adapter.notifyDataSetChanged()
    }
}