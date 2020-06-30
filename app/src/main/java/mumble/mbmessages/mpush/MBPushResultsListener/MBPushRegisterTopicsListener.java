package mumble.mbmessages.mpush.MBPushResultsListener;

import mumble.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_RegisterTopics;
import mumble.mbmessages.mpush.MBPushConstants.MBPushConstants;

/**
 * Interface to use with {@link MBPushAsyncTask_RegisterTopics (Context)}, and similar, methods,
 * send the push token
 *
 * @author Enrico Ori
 * @version {@value MBPushConstants#version}
 */
public interface MBPushRegisterTopicsListener {
    void onTopicsRegistered();

    void onTopicsRegisteredError(String error);
}
