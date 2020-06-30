package mumble.mbmessages.iam.MBIAMResultsListener;

import java.util.ArrayList;

import mumble.mbmessages.iam.MBIAMAsyncTasks.MBIAMAsyncTask_getCampaign;
import mumble.mbmessages.iam.MBIAMConstants.MBIAMConstants;
import mumble.mbmessages.iam.MBIAMData.Campaign;
import mumble.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_RegisterTopics;
import mumble.mbmessages.mpush.MBPushConstants.MBPushConstants;

/**
 * Interface to use with {@link MBIAMAsyncTask_getCampaign (Context)}, and similar, methods,
 * send the push token
 *
 * @author Enrico Ori
 * @version {@value MBIAMConstants#version}
 */
public interface MBIAMCampaignListener {
    void onCampaignObtained(ArrayList<Campaign> campaigns);
    void onCampaignError(String error);
}
