package mumble.mburger.mbmessages.metrics

import android.content.Context
import android.content.Intent
import mumble.mburger.mbmessages.metrics.MBMetricsAsyncTask.MBIAMAsyncTask_MetricCreate

class MBMessagesMetrics {

    companion object {

        private val type_push = "push"
        private val type_message = "message"

        private val metric_view = "view"
        private val metric_interaction = "interaction"

        fun trackShowMessage(context: Context, message_id: String) {
            MBIAMAsyncTask_MetricCreate(context, type_message, metric_view, message_id).execute()
        }

        fun trackInteractionMessage(context: Context, message_id: String) {
            MBIAMAsyncTask_MetricCreate(context, type_message, metric_interaction, message_id).execute()
        }

        fun trackShowPush(context: Context, intent: Intent) {
            if (intent.extras != null) {
                if (intent.getBooleanExtra("onMBNotificationStart", false)) {
                    val pushId = intent.getStringExtra("MBMessageId")
                    trackShowPush(context, pushId)
                }
            }
        }

        fun trackShowPush(context: Context, pushID: String?) {
            if (pushID != null) {
                MBIAMAsyncTask_MetricCreate(context, type_push, metric_view, pushID).execute()
            }
        }

        fun checkOpenedFromPush(context: Context, intent: Intent) {
            if (intent.extras != null) {
                if (intent.getBooleanExtra("onMBNotificationStart", false)) {
                    val pushId = intent.getStringExtra("MBMessageId")
                    checkOpenedFromPush(context, pushId)
                }
            }
        }

        fun checkOpenedFromPush(context: Context, pushID: String?) {
            if (pushID != null) {
                MBIAMAsyncTask_MetricCreate(context, type_message, metric_interaction, pushID).execute()
            }
        }
    }
}