package com.plugin.smstracker

import SMSReceiver
import android.Manifest
import android.app.Activity
import android.content.IntentFilter
import android.provider.Telephony
import android.telephony.SmsManager
import android.util.Log
import android.webkit.WebView
import app.tauri.annotation.Command
import app.tauri.annotation.InvokeArg
import app.tauri.annotation.Permission
import app.tauri.annotation.PermissionCallback
import app.tauri.annotation.TauriPlugin
import app.tauri.plugin.Invoke
import app.tauri.plugin.JSObject
import app.tauri.plugin.Plugin

@InvokeArg
class SendSmsArgs {
    var to: String? = null
    var body: String? = null
}

private const val ALIAS_SMS: String = "sms"

@Suppress("unused")
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

    @Suppress("unused")
    @PermissionCallback
    private fun smsPermissionCallback(invoke: Invoke) {
        val permissionsResultJSON = JSObject()
        permissionsResultJSON.put("sms", getPermissionState(ALIAS_SMS))
        invoke.resolve(permissionsResultJSON)
    }

    @Suppress("unused")
    @Command
    fun sendSms(invoke: Invoke) {
        val sendSmsResultJSON = JSObject()
        val smsManager: SmsManager? = activity.getSystemService(SmsManager::class.java)

        if (smsManager == null) {
            sendSmsResultJSON.put("success", false)
            invoke.resolve(sendSmsResultJSON)
            return
        }

        val args = invoke.parseArgs(SendSmsArgs::class.java)

        if (args.to == null || args.body == null) {
            sendSmsResultJSON.put("success", false)
            invoke.resolve(sendSmsResultJSON)
            return
        }

        smsManager.sendTextMessage(
            args.to,
            "SmsTracker",
            args.body,
            null,
            null
        )

        sendSmsResultJSON.put("success", true)
        Log.d("SmsPlugin", "Sms sent")
        invoke.resolve(sendSmsResultJSON)
    }
}
