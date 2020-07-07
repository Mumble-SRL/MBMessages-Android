package mumble.mburger.mbmessages.mpush.MBPushAsyncTasks;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import androidx.annotation.NonNull;

import java.lang.ref.WeakReference;
import java.util.Map;

import mumble.mburger.mbmessages.mpush.MBPushConstants.MBPushAPIConstants;
import mumble.mburger.mbmessages.mpush.MBPushConstants.MBPushUserConstants;
import mumble.mburger.mbmessages.mpush.MBPushResultsListener.MBPushSendTokenListener;
import mumble.mburger.sdk.kt.Common.MBApiManager.MBAMActivityUtils;
import mumble.mburger.sdk.kt.Common.MBApiManager.MBAPIManager4;
import mumble.mburger.sdk.kt.Common.MBApiManager.MBApiManagerConfig;
import mumble.mburger.sdk.kt.Common.MBApiManager.MBApiManagerUtils;
import mumble.mburger.sdk.kt.Common.MBCommonMethods;

public class MBPushAsyncTask_SendToken extends AsyncTask<Void, Void, Void> {

    /**
     * Context reference used to send data to Activity/Fragment
     */
    @NonNull
    private WeakReference<Context> weakContext;

    /**
     * Android device id
     */
    @NonNull
    private String device_id;

    /**
     * Push token obtained from Firebase
     */
    @NonNull
    private String token;

    /**
     * If you wish to change the action that accompanies the API result
     */
    private String action = MBPushAPIConstants.ACTION_SEND_TOKEN;

    /**
     * If you wish to use a listener to retrieve the data instead of the ApiListener
     */
    private MBPushSendTokenListener listener;

    private int result = MBApiManagerConfig.Companion.getCOMMON_INTERNAL_ERROR();
    private String error;
    private Map<String, Object> map;

    public MBPushAsyncTask_SendToken(Context context, String device_id, String token) {
        this.weakContext = new WeakReference<>(context);
        this.device_id = device_id;
        this.token = token;
    }

    public MBPushAsyncTask_SendToken(Context context, String cutom_action, String device_id, String token) {
        this.weakContext = new WeakReference<>(context);
        this.action = cutom_action;
        this.device_id = device_id;
        this.token = token;
    }

    public MBPushAsyncTask_SendToken(Context context, MBPushSendTokenListener listener, String device_id, String token) {
        this.weakContext = new WeakReference<>(context);
        this.listener = listener;
        this.device_id = device_id;
        this.token = token;
    }

    @Override
    protected Void doInBackground(Void... arg0) {
        putValuesAndCall();
        if (MBApiManagerUtils.Companion.hasMapOkResults(map, false)) {
            result = MBApiManagerConfig.Companion.getRESULT_OK();
        } else {
            if (map.containsKey(MBApiManagerConfig.Companion.getAM_RESULT())) {
                result = (int) map.get(MBApiManagerConfig.Companion.getAM_RESULT());
            } else {
                result = MBApiManagerConfig.Companion.getCOMMON_INTERNAL_ERROR();
            }

            if (map.containsKey(MBApiManagerConfig.Companion.getAM_ERROR())) {
                error = (String) map.get(MBApiManagerConfig.Companion.getAM_ERROR());
            } else {
                error = MBCommonMethods.Companion.getErrorMessageFromResult(weakContext.get(), result);
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (listener == null) {
            Intent i = new Intent(action);
            i.putExtra("result", result);
            i.putExtra("error", error);
            MBAMActivityUtils.Companion.sendBroadcastMessage(weakContext.get(), i);
        } else {
            if (error != null) {
                listener.onTokenSentError(error);
            } else {
                listener.onTokenSent();
            }
        }
    }

    public void putValuesAndCall() {
        ContentValues valuesHeaders = new ContentValues();
        valuesHeaders.put("X-MPush-Token", MBPushUserConstants.pushKey);

        ContentValues values = new ContentValues();
        values.put("token", token);
        values.put("device_id", device_id);
        values.put("platform", "and");
        map = MBAPIManager4.Companion.callApi(weakContext.get(),
                MBPushAPIConstants.API_TOKENS_PUSH, values, MBApiManagerConfig.Companion.getMODE_POST(), false,
                false, MBPushAPIConstants.endpoint_push, MBPushAPIConstants.SERVER_HOSTNAME_PUSH, true, valuesHeaders, null);
    }
}
