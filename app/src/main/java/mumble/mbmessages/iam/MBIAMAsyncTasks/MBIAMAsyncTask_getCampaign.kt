package mumble.mbmessages.iam.MBIAMAsyncTasks

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import mumble.mbmessages.iam.MBIAMConstants.MBIAMAPIConstants
import mumble.mbmessages.iam.MBIAMResultsListener.MBIAMCampaignListener
import mumble.mburger.sdk.kt.Common.MBApiManager.MBAMActivityUtils
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
class MBIAMAsyncTask_getCampaign : AsyncTask<Void, Void, Void> {

    /**
     * Context reference used to send data to Activity/Fragment
     */
    var weakContext: WeakReference<Context>

    /**
     * If you wish to change the action that accompanies the API result
     */
    private var action = MBIAMAPIConstants.ACTION_GET_CAMPAIGNS

    /**
     * If you wish to use a listener to retrieve the data
     */
    private var listener: MBIAMCampaignListener? = null

    private var result = MBApiManagerConfig.COMMON_INTERNAL_ERROR
    private var error: String? = null
    private var map: MutableMap<String, Any?>? = null

    constructor(context: Context) {
        this.weakContext = WeakReference(context)
    }

    constructor(context: Context, custom_action: String) {
        this.weakContext = WeakReference(context)
        this.action = custom_action
    }

    constructor(context: Context, listener: MBIAMCampaignListener?) {
        this.weakContext = WeakReference(context)
        this.listener = listener
    }

    override fun doInBackground(vararg params: Void?): Void? {
        putValuesAndCall()
        if (MBApiManagerUtils.hasMapOkResults(map, true)) {
            getPayload(map!![MBApiManagerConfig.AM_PAYLOAD] as String)
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
        if (listener == null) {
            val i = Intent(action)
            i.putExtra("result", result)
            i.putExtra("error", error)
            MBAMActivityUtils.sendBroadcastMessage(weakContext.get()!!, i)
        } else {
            if (error != null) {
                listener!!.onCampaignError(error!!)
            }
        }
    }

    fun putValuesAndCall() {
        val values = ContentValues()
        map = MBAPIManager4.callApi(weakContext.get()!!, MBIAMAPIConstants.API_GET_CAMPAIGNS, values,
                MBApiManagerConfig.MODE_GET, true, false)
    }

    fun getPayload(sPayload: String) {
        try {
            val jPayload = JSONObject(sPayload)
            val jBody = jPayload.getJSONObject("body")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
