package mumble.mburger.mbmessages.mpush.MBPushConstants;

import mumble.mburger.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_RegisterTopics;
import mumble.mburger.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_SendToken;
import mumble.mburger.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_UnregisterAllTopics;
import mumble.mburger.mbmessages.mpush.MBPushAsyncTasks.MBPushAsyncTask_UnregisterTopics;

public class MBPushAPIConstants {

    /**Used with the API {@link MBPushAsyncTask_SendToken MBPushAsyncTask_SendToken}*/
    public static final String ACTION_SEND_TOKEN = "mumble.mburger.ACTION_SEND_TOKEN";

    /**Used with the API {@link MBPushAsyncTask_RegisterTopics MBPushAsyncTask_RegisterTopics}*/
    public static final String ACTION_REGISTER_TOPICS = "mumble.mburger.ACTION_REGISTER_TOPICS";

    /**Used with the API {@link MBPushAsyncTask_UnregisterTopics MBPushAsyncTask_UnregisterTopics}*/
    public static final String ACTION_UNREGISTER_TOPICS = "mumble.mburger.ACTION_UNREGISTER_TOPICS";

    /**Used with the API {@link MBPushAsyncTask_UnregisterAllTopics MBPushAsyncTask_UnregisterAllTopics}*/
    public static final String ACTION_UNREGISTER_ALL_TOPICS = "mumble.mburger.ACTION_UNREGISTER_ALL_TOPICS";

    public static final String endpoint_push = "https://push.mumbleserver.it";
    public static final String SERVER_HOSTNAME_PUSH = "push.mumbleserver.it";

    public static final String API_TOKENS_PUSH = "/api/tokens";
    public static final String API_REGISTER_TOPICS = "/api/register";
    public static final String API_UNREGISTER_TOPICS = "/api/unregister";
    public static final String API_UNREGISTER_ALL_TOPICS = "/api/unregister-all";

}
