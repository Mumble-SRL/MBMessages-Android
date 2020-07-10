package mumble.mburger.mbmessages.iam.MBIAMResultsListener;

import java.util.ArrayList;

import mumble.mburger.mbmessages.iam.MBIAMAsyncTasks.MBIAMAsyncTask_getMessages;
import mumble.mburger.mbmessages.iam.MBIAMConstants.MBIAMConstants;
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessage;

/**
 * Interface to use with {@link MBIAMAsyncTask_getMessages (Context)}, and similar, methods,
 * send the push token
 *
 * @author Enrico Ori
 * @version {@value MBIAMConstants#version}
 */
public interface MBIAMMBMessageListener {
    void onCampaignObtained(ArrayList<MBMessage> MBMessages);
    void onCampaignError(String error);
}
