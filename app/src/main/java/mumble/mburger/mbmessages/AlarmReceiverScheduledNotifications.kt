package mumble.mburger.mbmessages

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class AlarmReceiverScheduledNotifications : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val channel_id = intent.getStringExtra("channel_id")
            val small_icon = intent.getIntExtra("small_icon", -1)

            val title = intent.getStringExtra("title")
            val body = intent.getStringExtra("body")
            val id = intent.getLongExtra("id", -1)

            MBMessagesManager.showLocal(context!!, channel_id!!, small_icon, id, title!!, body!!)
        }
    }
}