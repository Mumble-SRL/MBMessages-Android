package mumble.mburger.mbmessages.iam.MBIAMData

import java.io.Serializable

class Campaign (

        /**
         * Unique id of the campaign
         */
        var id: Long,

        /**
         * Title for the campaign
         */
        var title: String?,

        /**
         * Description for the campaign
         */
        var description: String?,

        /**
         * Type of object for the campaign
         */
        var type: String?,

        /**
         * Send after days
         */
        var send_after_days: Int,

        /**
         * Repeat = 1
         */
        var repeat: Int,

        /**
         * Milliseconds to start at
         */
        var starts_at: Long,

        /**
         * Milliseconds to end at
         */
        var ends_at: Long,

        /**
         * Automation SDK is enabled
         */
        var automation: Int,

        /**
         * Triggers
         */
        var triggers: String?,

        /**
         * Creation time in millis
         */
        var created_at: Long,

        /**
         * Creation time in millis
         */
        var updated_at: Long,

        /**
         * Campaign object
         */
        var content: Any?) : Serializable