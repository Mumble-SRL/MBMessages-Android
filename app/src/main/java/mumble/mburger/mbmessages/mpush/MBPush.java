package mumble.mburger.mbmessages.mpush;

import mumble.mburger.mbmessages.mpush.MBPushConstants.MBPushUserConstants;

public class MBPush {

    public static void init(String token) {
        MBPushUserConstants.pushKey = token;
    }

}
