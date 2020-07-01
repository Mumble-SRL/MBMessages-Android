package mumble.mbmessages.iam

import android.content.Context
import androidx.fragment.app.FragmentActivity
import mumble.mbmessages.iam.MBIAMAsyncTasks.MBIAMAsyncTask_getCampaign
import mumble.mbmessages.iam.MBIAMData.Campaign
import mumble.mbmessages.iam.MBIAMResultsListener.MBIAMCampaignListener
import mumble.mbmessages.iam.MBIAMResultsListener.MBMessagesPluginInitialized
import mumble.mburger.sdk.kt.MBPlugins.MBPlugin

class MBMessages : MBIAMCampaignListener, MBPlugin() {

    override var id: String? = "MBMessages"
    override var order: Int = -1
    override var delayInSeconds: Long = 0
    override var error: String? = null
    override var initialized: Boolean = false

    var manager: MBMessagesManager = MBMessagesManager()

    var initListener: MBMessagesPluginInitialized? = null

    override fun init(context: Context) {
        super.init(context)
        MBIAMAsyncTask_getCampaign(context, this).execute()
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