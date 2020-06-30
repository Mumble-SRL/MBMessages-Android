package mumble.mbmessages.iam

import android.content.Context
import androidx.fragment.app.FragmentActivity
import mumble.mbmessages.iam.MBIAMConstants.MBIAMConstants
import mumble.mbmessages.iam.MBIAMData.Campaign
import mumble.mbmessages.iam.MBIAMData.CampaignIAM
import mumble.mbmessages.iam.MBIAMPopups.DialogFragBottom
import mumble.mbmessages.iam.MBIAMPopups.DialogFragCenter
import mumble.mbmessages.iam.MBIAMPopups.DialogFragTop
import mumble.mburger.sdk.kt.Common.MBCommonMethods
import org.json.JSONArray

class MBMessagesManager {

    lateinit var campaigns: ArrayList<Campaign>

    var debugMode = false

    /**STYLING AND CUSTOMIZATION**/
    var forceMessageStyle: String? = null

    var backgroundColor: Int? = null
    var titleColor: Int? = null
    var bodyColor: Int? = null
    var closeButtonColor: Int? = null
    var button1BackgroundColor: Int? = null
    var button1TitleColor: Int? = null
    var button2BackgroundColor: Int? = null
    var button2TitleColor: Int? = null

    var titleFont: Int? = null
    var bodyFont: Int? = null
    var buttonsTextFont: Int? = null

    var currentPosition = 0

    fun addMessageSeen(id: Long, context: Context) {
        val jMessagesSeen = JSONArray(MBCommonMethods.getSharedPreferences(context)!!
                .getString(MBIAMConstants.PROPERTIES_MESSAGES_SEEN, "[]"))

        jMessagesSeen.put(id)

        MBCommonMethods.getSharedPreferencesEditor(context)!!
                .putString(MBIAMConstants.PROPERTIES_MESSAGES_SEEN, jMessagesSeen.toString()).commit()
    }

    fun startFlow(activity: FragmentActivity) {
        currentPosition = 0
        val ids = getSeenIds(activity)
        for (i in 0 until campaigns.size) {
            val campaign = campaigns[i]
            if ((campaign.type == MBIAMConstants.CAMPAIGN_MESSAGE) && (campaign.content is CampaignIAM)) {
                val content = campaign.content as CampaignIAM
                if (debugMode) {
                    currentPosition = i
                    show(activity, content)
                    break
                } else {
                    if (!ids.contains(content.id)) {
                        currentPosition = i
                        show(activity, content)
                        break
                    }
                }
            }
        }
    }

    fun continueFlow(activity: FragmentActivity) {
        val ids = getSeenIds(activity)
        for (i in (currentPosition + 1) until campaigns.size) {
            val campaign = campaigns[i]
            if ((campaign.type == MBIAMConstants.CAMPAIGN_MESSAGE) && (campaign.content is CampaignIAM)) {
                val content = campaign.content as CampaignIAM
                if (debugMode) {
                    currentPosition = i
                    show(activity, content)
                    break
                } else {
                    if (!ids.contains(content.id)) {
                        currentPosition = i
                        show(activity, content)
                        break
                    }
                }
            }
        }
    }

    private fun show(activity: FragmentActivity, content: CampaignIAM) {
        overrideColorsAndStyle(content)
        when (content.type) {
            MBIAMConstants.IAM_STYLE_BANNER_TOP -> {
                val dialog = DialogFragTop()
                dialog.initialize(this, content)
                dialog.show(activity.supportFragmentManager, null)
            }

            MBIAMConstants.IAM_STYLE_BANNER_CENTER -> {
                val dialog = DialogFragCenter()
                dialog.initialize(this, content)
                dialog.show(activity.supportFragmentManager, null)
            }

            MBIAMConstants.IAM_STYLE_BANNER_BOTTOM -> {
                val dialog = DialogFragBottom()
                dialog.initialize(this, content)
                dialog.show(activity.supportFragmentManager, null)
            }

            else -> {

            }
        }
    }

    fun overrideColorsAndStyle(content: CampaignIAM) {
        if (forceMessageStyle != null) {
            content.type = forceMessageStyle!!
        }

        if (backgroundColor != null) {
            content.backgroundColor = backgroundColor!!
        }

        if (titleColor != null) {
            content.title_color = titleColor!!
        }

        if (bodyColor != null) {
            content.content_color = bodyColor!!
        }

        if (closeButtonColor != null) {
            content.closeButtonColor = closeButtonColor!!
        }

        if (button1BackgroundColor != null) {
            if (content.cta1 != null) {
                content.cta1!!.background_color = button1BackgroundColor!!
            }
        }

        if (button1TitleColor != null) {
            if (content.cta1 != null) {
                content.cta1!!.text_color = button1TitleColor!!
            }
        }

        if (button2BackgroundColor != null) {
            if (content.cta2 != null) {
                content.cta2!!.background_color = button2BackgroundColor!!
            }
        }

        if (button2TitleColor != null) {
            if (content.cta2 != null) {
                content.cta2!!.text_color = button2TitleColor!!
            }
        }
    }

    fun getSeenIds(activity: FragmentActivity): ArrayList<Long> {
        val ids = ArrayList<Long>()
        val jMessagesSeen = JSONArray(MBCommonMethods.getSharedPreferences(activity)!!
                .getString(MBIAMConstants.PROPERTIES_MESSAGES_SEEN, "[]"))

        for (i in 0 until jMessagesSeen.length()) {
            ids.add(jMessagesSeen.getLong(i))
        }

        return ids
    }
}