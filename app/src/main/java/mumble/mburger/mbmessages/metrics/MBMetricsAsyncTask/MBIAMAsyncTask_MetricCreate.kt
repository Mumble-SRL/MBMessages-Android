package mumble.mburger.mbmessages.metrics.MBMetricsAsyncTask

import android.content.ContentValues
import android.content.Context
import android.os.AsyncTask
import mumble.mburger.mbmessages.iam.MBIAMConstants.MBIAMAPIConstants
import mumble.mburger.sdk.kt.Common.MBApiManager.MBAPIManager4
import mumble.mburger.sdk.kt.Common.MBApiManager.MBApiManagerConfig
import mumble.mburger.sdk.kt.Common.MBApiManager.MBApiManagerUtils
import mumble.mburger.sdk.kt.Common.MBCommonMethods
import java.lang.ref.WeakReference

/**
 * Task which create metrics associated with push notifications and iam
 *
 * @author Enrico Ori
 * @version {@value MBIAMConstants#version}
 */
internal class MBIAMAsyncTask_MetricCreate : AsyncTask<Void, Void, Void> {

    /**
     * Context reference used to send data to Activity/Fragment
     */
    var weakContext: WeakReference<Context>

    var type: String
    var metric: String
    var message_id: String

    private var result = MBApiManagerConfig.COMMON_INTERNAL_ERROR
    private var error: String? = null
    private var map: MutableMap<String, Any?>? = null

    constructor(context: Context, type: String, metric: String, message_id: String) {
        this.weakContext = WeakReference(context)
        this.type = type
        this.metric = metric
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
        values.put("message_id", message_id)
        map = MBAPIManager4.callApi(weakContext.get()!!, MBIAMAPIConstants.API_CREATE_METRICS, values,
                MBApiManagerConfig.MODE_POST, false, false)
    }
}
