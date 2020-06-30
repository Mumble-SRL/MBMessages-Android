package mumble.mbmessages.mpush.MBPushResultsListener;


import mumble.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_UnregisterTopics;
import mumble.mbmessages.mpush.MBPushConstants.MBPushConstants;

/**
 * Interface to use with {@link MBPushAsyncTask_UnregisterTopics (Context)}, and similar, methods,
 * send the push token
 *
 * @author Enrico Ori
 * @version {@value MBPushConstants#version}
 */
public interface MBPushUnregisterTopicsListener {
    void onTopicsUnregistered();

    void onTopicsUnregisteredError(String error);
}
