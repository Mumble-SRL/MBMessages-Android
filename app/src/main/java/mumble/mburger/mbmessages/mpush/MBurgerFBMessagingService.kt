package mumble.mburger.mbmessages.mpush

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import mumble.mburger.sdk.kt.Common.MBCommonMethods
import org.json.JSONException
import org.json.JSONObject

abstract class MBurgerFBMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val intent = Intent()

        val map = remoteMessage.data
        var push_id: String? = null
        if (map.containsKey("custom")) {
            val custom: String = map["custom"]!!
            if (custom.startsWith("{")) {
                try {
                    val jObj = JSONObject(custom)
                    if (MBCommonMethods.isJSONOk(jObj, "push_id")) {
                        push_id = jObj.getString("push_id")
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }

        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("onMBNotificationStart", true)
        intent.putExtra("MBPushId", push_id)
        onMBMessageReceived(remoteMessage, intent)
    }

    abstract fun onMBMessageReceived(remoteMessage: RemoteMessage, intent: Intent)
}