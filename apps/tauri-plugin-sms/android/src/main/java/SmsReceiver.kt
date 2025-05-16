import AppConstants.AppConstants
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage

class SMSReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action != null && intent.action!!.equals("android.provider.Telephony.SMS_RECEIVED", ignoreCase = true)) {
            val bundle = intent.extras
            if (bundle != null) {
                val sms = bundle.get(SMS_BUNDLE) as Array<Any>?
                val smsMsg = StringBuilder()

                var smsMessage: SmsMessage
                if (sms != null) {
                    for (sm in sms) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            val format = bundle.getString("format")
                            smsMessage = SmsMessage.createFromPdu(sm as ByteArray, format)
                        } else {
                            smsMessage = SmsMessage.createFromPdu(sm as ByteArray)
                        }


                        val msgBody = smsMessage.messageBody.toString()
                        val msgAddress = smsMessage.originatingAddress

                        smsMsg.append("SMS from : ").append(msgAddress).append("\n")
                        smsMsg.append(msgBody).append("\n")
                    }

                    sendBroadcast(smsMsg.toString())
                }
            }
        }
    }

    private fun sendBroadcast(smsMSG: String) {
        val broadcastIntent = Intent()
        broadcastIntent.action = AppConstants.mBroadcastSMSUpdateAction
        broadcastIntent.putExtra(AppConstants.message, smsMSG)
//        EventBus.getDefault().post(EventIntent(broadcastIntent))
    }

    companion object {
        const val SMS_BUNDLE = "pdus"
    }
}