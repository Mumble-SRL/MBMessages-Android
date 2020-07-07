package mumble.mburger.mbmessages.mpush;

import android.content.Context;

import org.json.JSONArray;

import mumble.mburger.mbmessages.R;
import mumble.mburger.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_RegisterTopics;
import mumble.mburger.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_SendToken;
import mumble.mburger.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_UnregisterAllTopics;
import mumble.mburger.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_UnregisterTopics;
import mumble.mburger.mbmessages.mpush.MBPushConstants.MBPushUserConstants;
import mumble.mburger.mbmessages.mpush.MBPushResultsListener.MBPushRegisterTopicsListener;
import mumble.mburger.mbmessages.mpush.MBPushResultsListener.MBPushSendTokenListener;
import mumble.mburger.mbmessages.mpush.MBPushResultsListener.MBPushUnregisterAllTopicsListener;
import mumble.mburger.mbmessages.mpush.MBPushResultsListener.MBPushUnregisterTopicsListener;
import mumble.mburger.sdk.kt.Common.MBExceptions.MBSDKInitializeException;

public class MBurgerPushTasks {

    /**
     * Register device to receive push notifications
     */
    public static void sendToken(Context context, String device_id, String token) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_SendToken(context, device_id, token).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Register device to receive push notifications with custom action callback
     */
    public static void sendToken(Context context, String custom_action, String device_id, String token) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_SendToken(context, custom_action, device_id, token).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Register device to receive push notifications with listener callback
     */
    public static void sendToken(Context context, MBPushSendTokenListener listener, String device_id, String token) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_SendToken(context, listener, device_id, token).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Register push channels
     */
    public static void registerTopics(Context context, String device_id, JSONArray topics) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_RegisterTopics(context, device_id, topics).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Register push channels with custom action callback
     */
    public static void registerTopics(Context context, String custom_action, String device_id, JSONArray topics) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_RegisterTopics(context, custom_action, device_id, topics).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Register push channels with listener callback
     */
    public static void registerTopics(Context context, MBPushRegisterTopicsListener listener, String device_id, JSONArray topics) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_RegisterTopics(context, listener, device_id, topics).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Unregister push channels
     */
    public static void unregisterTopics(Context context, String device_id, JSONArray topics) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_UnregisterTopics(context, device_id, topics).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Unregister push channels with custom action callback
     */
    public static void unregisterTopics(Context context, String custom_action, String device_id, JSONArray topics) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_UnregisterTopics(context, custom_action, device_id, topics).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Unregister push channels with listener callback
     */
    public static void unregisterTopics(Context context, MBPushUnregisterTopicsListener listener, String device_id, JSONArray topics) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_UnregisterTopics(context, listener, device_id, topics).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Unregister all push channels
     */
    public static void unregisterAllTopics(Context context, String device_id) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_UnregisterAllTopics(context, device_id).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Unregister all push channels with custom action callback
     */
    public static void unregisterAllTopics(Context context, String custom_action, String device_id) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_UnregisterAllTopics(context, custom_action, device_id).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }

    /**
     * Unregister all push channels with listener callback
     */
    public static void unregisterAllTopics(Context context, MBPushUnregisterAllTopicsListener listener, String device_id) {
        if (MBPushUserConstants.pushKey != null) {
            new MBPushAsyncTask_UnregisterAllTopics(context, listener, device_id).execute();
        } else {
            throw new MBSDKInitializeException(context.getString(R.string.exception_sdk_not_initialized));
        }
    }
}
