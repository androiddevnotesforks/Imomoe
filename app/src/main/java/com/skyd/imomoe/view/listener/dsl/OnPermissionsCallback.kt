package com.skyd.imomoe.view.listener.dsl

import com.hjq.permissions.XXPermissions

fun XXPermissions.requestPermissions(init: OnPermissionsCallback.() -> Unit) {
    val listener = OnPermissionsCallback()
    listener.init()
    this.request(listener)
}

fun XXPermissions.requestSinglePermission(init: OnSinglePermissionCallback.() -> Unit) {
    val listener = OnSinglePermissionCallback()
    listener.init()
    this.request(listener)
}

private typealias OnGranted = (permissions: MutableList<String>?, all: Boolean) -> Unit
private typealias OnDenied = (permissions: MutableList<String>?, never: Boolean) -> Unit

class OnPermissionsCallback : com.hjq.permissions.OnPermissionCallback {
    private var onGranted: OnGranted? = null
    private var onDenied: OnDenied? = null

    fun onGranted(onGranted: OnGranted?) {
        this.onGranted = onGranted
    }

    fun onDenied(denied: OnDenied?) {
        this.onDenied = denied
    }

    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
        onGranted?.invoke(permissions, all)
    }

    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
        onDenied?.invoke(permissions, never)
    }
}

private typealias OnSingleGranted = () -> Unit
private typealias OnSingleDenied = (never: Boolean) -> Unit

class OnSinglePermissionCallback : com.hjq.permissions.OnPermissionCallback {
    private var onSingleGranted: OnSingleGranted? = null
    private var onSingleDenied: OnSingleDenied? = null

    fun onGranted(onSingleGranted: OnSingleGranted?) {
        this.onSingleGranted = onSingleGranted
    }

    fun onDenied(onSingleDenied: OnSingleDenied?) {
        this.onSingleDenied = onSingleDenied
    }

    override fun onGranted(permissions: MutableList<String>?, all: Boolean) {
        onSingleGranted?.invoke()
    }

    override fun onDenied(permissions: MutableList<String>?, never: Boolean) {
        onSingleDenied?.invoke(never)
    }
}
