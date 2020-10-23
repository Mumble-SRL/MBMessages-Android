package mumble.mburger.mbmessages

import android.content.Context
import androidx.fragment.app.FragmentActivity
import mumble.mburger.mbmessages.iam.MBIAMAsyncTasks.MBIAMAsyncTask_getMessages
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessage
import mumble.mburger.mbmessages.iam.MBIAMResultsListener.MBIAMMBMessageListener
import mumble.mburger.mbmessages.iam.MBIAMResultsListener.MBMessagesPluginInitialized
import mumble.mburger.mbmessages.iam.MBMessagesManager
import mumble.mburger.sdk.kt.Common.MBPluginsManager
import mumble.mburger.sdk.kt.MBPlugins.MBPlugin

class MBMessages : MBIAMMBMessageListener, MBPlugin() {

    companion object {
        var isAutomationConnected = false
    }

    override var id: String? = "MBMessages"
    override var order: Int = -1
    override var delayInSeconds: Long = 0
    override var error: String? = null
    override var initialized: Boolean = false

    var initListener: MBMessagesPluginInitialized? = null

    override fun init(context: Context) {
        super.init(context)
        isAutomationConnected = false
        MBMessagesManager.currentPosition = 0
        MBIAMAsyncTask_getMessages(context, this).execute()
    }

    override fun doStart(activity: FragmentActivity) {
        super.doStart(activity)
        if (initialized) {
            MBMessagesManager.startFlow(activity)
        }
    }

    override fun onMessagesObtained(messages: ArrayList<MBMessage>?) {
        initialized = true
        initListener?.onInitialized()

        val noAutomationMessages = ArrayList<MBMessage>()
        val automationMessages = ArrayList<MBMessage>()
        for (message in messages!!) {
            if (message.automation == 1) {
                automationMessages.add(message)
            }else{
                noAutomationMessages.add(message)
            }
        }

        MBMessagesManager.MBMessages = noAutomationMessages
        MBPluginsManager.messagesReceived(automationMessages, false)
    }

    override fun onMessagesError(error: String?) {
        initialized = true
        this.error = error
        initListener?.onInitializedError(error)
    }
}