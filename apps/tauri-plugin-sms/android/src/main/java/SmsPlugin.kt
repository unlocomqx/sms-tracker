package com.plugin.smstracker

import android.Manifest
import android.app.Activity
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.Permission
import app.tauri.annotation.PermissionCallback
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.Invoke
import app.tauri.plugin.JSObject
import app.tauri.plugin.Plugin

private const val ALIAS_SMS: String = "sms"

@TauriPlugin(
    permissions = [
        Permission(
            strings = [
                Manifest.permission.READ_SMS,
                Manifest.permission.RECEIVE_SMS
            ],
            alias = ALIAS_SMS
        )
    ]
)
class SmsPlugin(private val activity: Activity) : Plugin(activity) {
    private val implementation = Sms()

    @Command
    override fun checkPermissions(invoke: Invoke) {
        super.checkPermissions(invoke)
    }

    @PermissionCallback
    private fun smsPermissionCallback(invoke: Invoke) {
        val permissionsResultJSON = JSObject()
        permissionsResultJSON.put("sms", getPermissionState(ALIAS_SMS))
        invoke.resolve(permissionsResultJSON)
    }
}
