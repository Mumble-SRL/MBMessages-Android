package mumble.mbmessages.mpush.MBPushResultsListener;

import mumble.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_UnregisterAllTopics;
import mumble.mbmessages.mpush.MBPushConstants.MBPushConstants;

/**
 * Interface to use with {@link MBPushAsyncTask_UnregisterAllTopics (Context)}, and similar, methods,
 * send the push token
 *
 * @author Enrico Ori
 * @version {@value MBPushConstants#version}
 */
public interface MBPushUnregisterAllTopicsListener {
    void onAllTopicsUnregistered();

    void onAllTopicsUnregisteredError(String error);
}
