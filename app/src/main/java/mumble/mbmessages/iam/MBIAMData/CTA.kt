package mumble.mbmessages.iam.MBIAMData

import java.io.Serializable

class CTA(
        var text: String,
        var text_color: Int?,
        var background_color: Int?,
        var action_type: String,
        var action: String) : Serializable