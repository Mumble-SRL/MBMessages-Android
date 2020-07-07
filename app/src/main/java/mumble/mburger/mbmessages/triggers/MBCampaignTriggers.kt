package mumble.mburger.mbmessages.triggers

import java.io.Serializable

class MBCampaignTriggers(var method: TriggerMethod, var triggers: ArrayList<MBTrigger>) : Serializable

enum class TriggerMethod(val operator: String) {
    ALL("all"),
    ANY("any")
}