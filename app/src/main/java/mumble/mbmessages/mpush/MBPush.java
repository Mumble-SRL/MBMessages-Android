package mumble.mbmessages.mpush;

import mumble.mbmessages.mpush.MBPushConstants.MBPushUserConstants;

public class MBPush {

    public static void init(String token) {
        MBPushUserConstants.pushKey = token;
    }

}
