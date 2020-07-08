package mumble.mburger.mbmessages

import android.content.Context
import androidx.fragment.app.FragmentActivity
import mumble.mburger.mbmessages.iam.MBIAMAsyncTasks.MBIAMAsyncTask_getMessages
import mumble.mburger.mbmessages.iam.MBIAMData.Campaign
import mumble.mburger.mbmessages.iam.MBIAMResultsListener.MBIAMCampaignListener
import mumble.mburger.mbmessages.iam.MBIAMResultsListener.MBMessagesPluginInitialized
import mumble.mburger.mbmessages.iam.MBMessagesManager
import mumble.mburger.sdk.kt.MBPlugins.MBPlugin

class MBMessages : MBIAMCampaignListener, MBPlugin() {

    companion object {
        var isAutomationConnected = false
    }

    override var id: String? = "MBMessages"
    override var order: Int = -1
    override var delayInSeconds: Long = 0
    override var error: String? = null
    override var initialized: Boolean = false

    var manager: MBMessagesManager = MBMessagesManager()

    var initListener: MBMessagesPluginInitialized? = null

    override fun init(context: Context) {
        super.init(context)
        isAutomationConnected = false
        MBIAMAsyncTask_getMessages(context, this).execute()
    }

    override fun doStart(activity: FragmentActivity) {
        super.doStart(activity)
        if (initialized) {
            manager.startFlow(activity)
        }
    }

    override fun onCampaignObtained(campaigns: ArrayList<Campaign>?) {
        initialized = true
        manager.campaigns = campaigns!!
        initListener?.onInitialized()
    }

    override fun onCampaignError(error: String?) {
        initialized = true
        this.error = error
        initListener?.onInitializedError(error)
    }
}