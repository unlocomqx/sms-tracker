package com.plugin.smstracker

import SMSReceiver
import android.Manifest
import android.app.Activity
import android.content.IntentFilter
import android.provider.Telephony
import android.webkit.WebView
import app.tauri.annotation.Command
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
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.SEND_SMS
            ],
            alias = ALIAS_SMS
        )
    ]
)
class SmsPlugin(private val activity: Activity) : Plugin(activity) {
    private val implementation = Sms()
    private lateinit var smsBroadcastReceiver: SMSReceiver


    override fun load(webView: WebView) {
        super.load(webView)

        smsBroadcastReceiver = SMSReceiver()
        smsBroadcastReceiver.setOnSmsReceivedListener { data ->
            trigger("sms-received", data)
        }
        activity.registerReceiver(
            smsBroadcastReceiver,
            IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        )
    }

    @Command
    override fun checkPermissions(invoke: Invoke) {
        super.checkPermissions(invoke)
    }

    @Command
    override fun requestPermissions(invoke: Invoke) {
        super.requestPermissions(invoke)
    }

    @PermissionCallback
    private fun smsPermissionCallback(invoke: Invoke) {
        val permissionsResultJSON = JSObject()
        permissionsResultJSON.put("sms", getPermissionState(ALIAS_SMS))
        invoke.resolve(permissionsResultJSON)
    }
}
