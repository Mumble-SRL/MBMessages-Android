package mumble.mbmessages.iam.MBIAMData

import java.io.Serializable

class CampaignIAM(
        var id: Long,
        var type: String,
        var title: String?,
        var content: String?,
        var title_color: Int?,
        var content_color: Int?,
        var backgroundColor: Int?,
        var closeButtonColor: Int? = null,
        var closeButtonBGColor: Int? = null,
        var cta1: CTA? = null,
        var cta2: CTA? = null,
        var durationInSeconds: Int,
        var expiresAt: Long? = null,
        var image: String? = null) : Serializable