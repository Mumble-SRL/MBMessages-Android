package mumble.mbmessages.mpush.MBPushResultsListener;

import mumble.mbpush.Common.MBConstants.MBConstants;
import mumble.mbpush.MBPushMethods.MBPushAsyncTasks.MBPushAsyncTask_UnregisterTopics;

/**
 * Interface to use with {@link MBPushAsyncTask_UnregisterTopics (Context)}, and similar, methods,
 * send the push token
 *
 * @author Enrico Ori
 * @version {@value MBConstants#version}
 */
public interface MBPushUnregisterTopicsListener {
    void onTopicsUnregistered();

    void onTopicsUnregisteredError(String error);
}
