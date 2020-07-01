package mumble.mbmessages.metrics.MBMetricsAsyncTask

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import mumble.mbmessages.iam.MBIAMConstants.MBIAMAPIConstants
import mumble.mbmessages.iam.MBMessagesParser
import mumble.mburger.sdk.kt.Common.MBApiManager.MBAPIManager4
import mumble.mburger.sdk.kt.Common.MBApiManager.MBApiManagerConfig
import mumble.mburger.sdk.kt.Common.MBApiManager.MBApiManagerUtils
import mumble.mburger.sdk.kt.Common.MBCommonMethods
import org.json.JSONException
import org.json.JSONObject
import java.lang.ref.WeakReference

/**
 * Task which returns the messages in the app, those are also shown automatically or manually
 *
 * @author Enrico Ori
 * @version {@value MBIAMConstants#version}
 */
class MBIAMAsyncTask_MetricCreate : AsyncTask<Void, Void, Void> {

    /**
     * Context reference used to send data to Activity/Fragment
     */
    var weakContext: WeakReference<Context>

    lateinit var type: String
    lateinit var metric: String
    var push_id: String? = null
    var message_id: String? = null

    private var result = MBApiManagerConfig.COMMON_INTERNAL_ERROR
    private var error: String? = null
    private var map: MutableMap<String, Any?>? = null

    constructor(context: Context, type: String, metric: String, push_id: String?, message_id: String?) {
        this.weakContext = WeakReference(context)
        this.type = type
        this.metric = metric
        this.push_id = push_id
        this.message_id = message_id
    }

    override fun doInBackground(vararg params: Void?): Void? {
        putValuesAndCall()
        if (MBApiManagerUtils.hasMapOkResults(map, false)) {
            result = MBApiManagerConfig.RESULT_OK
        } else {
            if (map!!.containsKey(MBApiManagerConfig.AM_RESULT)) {
                result = map!![MBApiManagerConfig.AM_RESULT] as Int
            } else {
                result = MBApiManagerConfig.COMMON_INTERNAL_ERROR
            }

            if (map!!.containsKey(MBApiManagerConfig.AM_ERROR)) {
                error = map!![MBApiManagerConfig.AM_ERROR] as String
            } else {
                error = MBCommonMethods.getErrorMessageFromResult(weakContext.get()!!, result)
            }
        }
        return null
    }

    override fun onPostExecute(postResult: Void?) {
    }

    fun putValuesAndCall() {
        val values = ContentValues()
        values.put("device_id", MBCommonMethods.getDeviceId(weakContext.get()!!))
        values.put("type", type)
        values.put("metric", metric)

        if (push_id != null) {
            values.put("push_id", push_id)
        }

        if (message_id != null) {
            values.put("message_id", message_id)
        }

        map = MBAPIManager4.callApi(weakContext.get()!!, MBIAMAPIConstants.API_CREATE_METRICS, values,
                MBApiManagerConfig.MODE_POST, false, false)
    }
}
