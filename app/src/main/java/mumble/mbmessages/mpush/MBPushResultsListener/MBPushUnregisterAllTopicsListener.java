package mumble.mbmessages.mpush.MBPushResultsListener;

import mumble.mbpush.Common.MBConstants.MBConstants;
import mumble.mbpush.MBPushMethods.MBPushAsyncTasks.MBPushAsyncTask_UnregisterAllTopics;

/**
 * Interface to use with {@link MBPushAsyncTask_UnregisterAllTopics (Context)}, and similar, methods,
 * send the push token
 *
 * @author Enrico Ori
 * @version {@value MBConstants#version}
 */
public interface MBPushUnregisterAllTopicsListener {
    void onAllTopicsUnregistered();

    void onAllTopicsUnregisteredError(String error);
}
