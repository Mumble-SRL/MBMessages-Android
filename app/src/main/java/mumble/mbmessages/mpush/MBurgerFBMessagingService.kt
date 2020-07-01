package mumble.mbmessages.mpush

import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

abstract class MBurgerFBMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val intent = Intent()
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra("onNotificationStart", true)
        onMBMessageReceived(remoteMessage, intent)
    }

    abstract fun onMBMessageReceived(remoteMessage: RemoteMessage, intent: Intent)
}