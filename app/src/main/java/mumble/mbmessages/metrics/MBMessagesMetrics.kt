package mumble.mbmessages.metrics

import android.content.Context
import mumble.mbmessages.metrics.MBMetricsAsyncTask.MBIAMAsyncTask_MetricCreate

class MBMessagesMetrics {

    companion object {

        private val type_push = "push"
        private val type_message = "message"

        private val metric_view = "view"
        private val metric_interaction = "interaction"

        fun trackShowMessage(context: Context, message_id:String){
            MBIAMAsyncTask_MetricCreate(context, type_message, metric_view, null, message_id).execute()
        }

        fun trackInteractionMessage(context: Context, message_id:String){
            MBIAMAsyncTask_MetricCreate(context, type_message, metric_interaction, null, message_id).execute()
        }

        fun trackShowPush(context: Context, push_id:String){
            MBIAMAsyncTask_MetricCreate(context, type_push, metric_view, push_id, null).execute()
        }

        fun trackInteractionMPush(context: Context, push_id:String){
            MBIAMAsyncTask_MetricCreate(context, type_push, metric_interaction, push_id, null).execute()
        }
    }
}