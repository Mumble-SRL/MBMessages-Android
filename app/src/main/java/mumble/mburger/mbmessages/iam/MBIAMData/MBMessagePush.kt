package mumble.mburger.mbmessages.iam.MBIAMData

import java.io.Serializable

class MBMessagePush(
        var id: String?,
        var title: String?,
        var body: String?,
        var date: String?,
        var sent: Int,
        var topics: String?,
        var total: Int,
        var created_at: Long,
        var updated_at: Long) : Serializable