package mumble.mbmessages.iam.MBIAMResultsListener;

public interface MBMessagesPluginInitialized {
    void onInitialized();
    void onInitializedError(String error);
}
