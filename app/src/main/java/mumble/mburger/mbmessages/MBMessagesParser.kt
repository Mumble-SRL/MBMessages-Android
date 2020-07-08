package mumble.mburger.mbmessages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import mumble.mburger.mbmessages.iam.MBIAMConstants.MBIAMConstants
import mumble.mburger.mbmessages.iam.MBIAMDBHelper
import mumble.mburger.mbmessages.iam.MBIAMData.CTA
import mumble.mburger.mbmessages.iam.MBIAMData.Campaign
import mumble.mburger.mbmessages.iam.MBIAMData.CampaignIAM
import mumble.mburger.mbmessages.iam.MBIAMData.CampaignPush
import mumble.mburger.mbmessages.triggers.*
import mumble.mburger.sdk.kt.Common.MBApiManager.MBApiManagerUtils
import mumble.mburger.sdk.kt.Common.MBCommonMethods
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

class MBMessagesParser {

    companion object {

        fun parseCampaigns(context: Context, jCampaigns: JSONArray): ArrayList<Campaign> {
            val campaigns = ArrayList<Campaign>()
            try {
                for (i in 0 until jCampaigns.length()) {
                    val jCampaign = jCampaigns.getJSONObject(i)
                    val campaign = parseCampaign(context, jCampaign)
                    campaigns.add(campaign)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return campaigns
        }

        fun parseCampaign(context: Context, jsonObject: JSONObject): Campaign {
            var id: Long = -1
            var title: String? = null
            var description: String? = null
            var type: String? = null
            var send_after_days: Int = 0
            var repeat: Int = 0
            var starts_at: Long = -1
            var ends_at: Long = -1
            var automation: Int = -1
            var triggers: MBCampaignTriggers? = null
            var created_at: Long = -1
            var updated_at: Long = -1
            var content: Any? = null

            try {
                if (MBCommonMethods.isJSONOk(jsonObject, "id")) {
                    id = jsonObject.getLong("id")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "title")) {
                    title = jsonObject.getString("title")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "description")) {
                    description = jsonObject.getString("description")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "type")) {
                    type = jsonObject.getString("type")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "send_after_days")) {
                    send_after_days = jsonObject.getInt("send_after_days")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "repeat")) {
                    repeat = jsonObject.getInt("repeat")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "starts_at")) {
                    starts_at = jsonObject.getLong("starts_at")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "ends_at")) {
                    ends_at = jsonObject.getLong("ends_at")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "automation")) {
                    automation = jsonObject.getInt("automation")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "triggers")) {
                    val jTriggers = jsonObject.getJSONObject("triggers")
                    triggers = parseTriggers(jTriggers)
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "created_at")) {
                    created_at = jsonObject.getLong("created_at")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "updated_at")) {
                    updated_at = jsonObject.getLong("updated_at")
                }

                if (type != null) {
                    content = when (type) {
                        MBIAMConstants.CAMPAIGN_PUSH -> parsePush(jsonObject.getJSONObject("content"))
                        else -> parseIAM(context, jsonObject.getJSONObject("content"))
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            val helper = MBIAMDBHelper(context)
            helper.addACampaign(id, jsonObject)

            return Campaign(id, title, description, type, send_after_days, repeat, starts_at, ends_at, automation, triggers,
                    created_at, updated_at, content)
        }

        fun parseIAM(context: Context, jsonObject: JSONObject): CampaignIAM {
            var id: Long = -1
            var type: String = MBIAMConstants.IAM_STYLE_BANNER_TOP
            var title: String? = null
            var content: String? = null
            var title_color: Int? = null
            var content_color: Int? = null
            var backgroundColor: Int? = null
            var cta1: CTA? = null
            var cta2: CTA? = null
            var durationInSeconds: Int = -1
            var expiresAt: Long? = -1
            var image: String? = null

            try {
                if (MBCommonMethods.isJSONOk(jsonObject, "id")) {
                    id = jsonObject.getLong("id")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "type")) {
                    type = jsonObject.getString("type")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "title")) {
                    title = jsonObject.getString("title")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "content")) {
                    content = jsonObject.getString("content")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "title_color")) {
                    title_color = Color.parseColor(jsonObject.getString("title_color"))
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "content_color")) {
                    content_color = Color.parseColor(jsonObject.getString("content_color"))
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "background_color")) {
                    backgroundColor = Color.parseColor(jsonObject.getString("background_color"))
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "duration")) {
                    durationInSeconds = jsonObject.getInt("duration")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "expires_at")) {
                    expiresAt = jsonObject.getLong("expires_at")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "image")) {
                    image = jsonObject.getString("image")
                    downloadImage(context, id.toString(), image)
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "cta_text") &&
                        MBCommonMethods.isJSONOk(jsonObject, "cta_action_type") &&
                        MBCommonMethods.isJSONOk(jsonObject, "cta_action")) {

                    var text_color = Color.BLACK
                    var bg_color = Color.WHITE

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta_text_color")) {
                        text_color = Color.parseColor(jsonObject.getString("cta_text_color"))
                    }

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta_background_color")) {
                        bg_color = Color.parseColor(jsonObject.getString("cta_background_color"))
                    }

                    cta1 = CTA(jsonObject.getString("cta_text"), text_color,
                            bg_color, jsonObject.getString("cta_action_type"),
                            jsonObject.getString("cta_action"))
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "cta2_text") &&
                        MBCommonMethods.isJSONOk(jsonObject, "cta2_action_type") &&
                        MBCommonMethods.isJSONOk(jsonObject, "cta2_action")) {

                    var text_color = Color.BLACK
                    var bg_color = Color.WHITE

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta2_text_color")) {
                        text_color = Color.parseColor(jsonObject.getString("cta2_text_color"))
                    }

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta2_background_color")) {
                        bg_color = Color.parseColor(jsonObject.getString("cta2_background_color"))
                    }

                    cta2 = CTA(jsonObject.getString("cta2_text"), text_color,
                            bg_color, jsonObject.getString("cta2_action_type"),
                            jsonObject.getString("cta2_action"))
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return CampaignIAM(id, type, title, content, title_color, content_color, backgroundColor, null,
                    null, cta1, cta2, durationInSeconds, expiresAt, image)
        }

        fun parsePush(jsonObject: JSONObject): CampaignPush {
            var id: String? = null
            var title: String? = null
            var body: String? = null
            var date: String? = null
            var sent: Int = -1
            var topics: String? = null
            var total: Int = -1
            var created_at: Long = -1
            var updated_at: Long = -1

            try {
                if (MBCommonMethods.isJSONOk(jsonObject, "id")) {
                    id = jsonObject.getString("id")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "created_at")) {
                    created_at = jsonObject.getLong("created_at")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "updated_at")) {
                    updated_at = jsonObject.getLong("updated_at")
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "payload")) {
                    val jPayload = jsonObject.getJSONObject("payload")

                    if (MBCommonMethods.isJSONOk(jPayload, "title")) {
                        title = jPayload.getString("title")
                    }

                    if (MBCommonMethods.isJSONOk(jPayload, "body")) {
                        body = jPayload.getString("body")
                    }

                    if (MBCommonMethods.isJSONOk(jPayload, "date")) {
                        date = jPayload.getString("date")
                    }

                    if (MBCommonMethods.isJSONOk(jPayload, "sent")) {
                        sent = jPayload.getInt("sent")
                    }

                    if (MBCommonMethods.isJSONOk(jPayload, "topics")) {
                        topics = jPayload.getString("topics")
                    }

                    if (MBCommonMethods.isJSONOk(jPayload, "total")) {
                        total = jPayload.getInt("total")
                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return CampaignPush(id, title, body, date, sent, topics, total, created_at, updated_at)
        }

        fun parseTriggers(jsonObject: JSONObject): MBCampaignTriggers {
            var triggers = ArrayList<MBTrigger>()

            val sTriggerMethod = jsonObject.getString("method")
            var method = when (sTriggerMethod) {
                "any" -> TriggerMethod.ANY
                else -> TriggerMethod.ALL
            }

            val jTriggers = jsonObject.getJSONArray("triggers")
            for (i in 0 until jTriggers.length()) {
                val jTr = jTriggers.getJSONObject(i)
                val type = jTr.getString("type")
                var trigger: MBTrigger = when (type) {
                    MBTriggersConstants.location -> {

                        var after = -1
                        var radius = -1
                        var address: String? = null
                        var latitude = (-1).toDouble()
                        var longitude = (-1).toDouble()

                        if (MBCommonMethods.isJSONOk(jTr, "after")) {
                            after = jTr.getInt("after")
                        }

                        if (MBCommonMethods.isJSONOk(jTr, "radius")) {
                            radius = jTr.getInt("radius")
                        }

                        if (MBCommonMethods.isJSONOk(jTr, "address")) {
                            address = jTr.getString("address")
                        }

                        if (MBCommonMethods.isJSONOk(jTr, "latitude")) {
                            latitude = jTr.getDouble("latitude")
                        }

                        if (MBCommonMethods.isJSONOk(jTr, "longitude")) {
                            longitude = jTr.getDouble("longitude")
                        }

                        MBTriggerLocation(after = after, radius = radius, address = address,
                                latitude = latitude, longitude = longitude)
                    }

                    MBTriggersConstants.app_opening -> {
                        MBTriggerAppOpening(times = jTr.getInt("times"))
                    }

                    MBTriggersConstants.view -> {
                        MBTriggerView(times = jTr.getInt("times"), view_name = jTr.getString("view_name"),
                                seconds_on_view = jTr.getInt("seconds_on_view"))
                    }

                    MBTriggersConstants.inactive_user -> {
                        MBTriggerInactiveUser(days = jTr.getInt("days"))
                    }

                    MBTriggersConstants.event -> {
                        var times = -1
                        var event_name: String? = null
                        var metadata: MBEventMetadata? = null

                        if (MBCommonMethods.isJSONOk(jTr, "times")) {
                            times = jTr.getInt("times")
                        }

                        if (MBCommonMethods.isJSONOk(jTr, "event_name")) {
                            event_name = jTr.getString("event_name")
                        }

                        if (MBCommonMethods.isJSONOk(jTr, "metadata")) {
                            val jMtD = jTr.getJSONObject("metadata")
                            val keys = jMtD.keys()

                            while (keys.hasNext()) {
                                val key: String = keys.next()
                                try {
                                    val value: String = jMtD.getString(key)
                                    metadata = MBEventMetadata(key, value)
                                } catch (e: JSONException) {
                                }
                            }
                        }

                        MBTriggerEvent(times = times, event_name = event_name, metadata = metadata)
                    }

                    MBTriggersConstants.tag_change -> {
                        val operator = when (jTr.getString("operator")) {
                            "=" -> TagChangeOperator.EQUALS
                            else -> TagChangeOperator.NOT_EQUAL
                        }

                        MBTriggerTagChange(tag = jTr.getString("tag"), value = jTr.getString("value"), operator = operator)
                    }

                    else -> {
                        MBTrigger("null")
                    }
                }

                triggers.add(trigger)
            }

            return MBCampaignTriggers(method, triggers)
        }

        fun downloadImage(context: Context, id: String, url: String) {
            val extStorage = context.getExternalFilesDir(null)
            if (!extStorage!!.exists()) {
                extStorage.mkdirs()
            }

            val imgFile = File(extStorage, "%s.jpg".format(id))

            val `is`: InputStream
            val imageUrl: URL
            var bmImg: Bitmap? = null
            if (!isFileWithSomething(imgFile)) {
                try {
                    if (MBApiManagerUtils.isNetworkAvailable(context)) {
                        imageUrl = URL(url)
                        val conn = imageUrl.openConnection() as HttpURLConnection
                        conn.connectTimeout = 15000
                        conn.doInput = true
                        conn.connect()
                        `is` = conn.inputStream
                        val options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_8888
                        if (Build.VERSION.SDK_INT < 22) {
                            options.inSampleSize = 2
                        }

                        bmImg = BitmapFactory.decodeStream(`is`, null, options)
                        if (bmImg != null) {
                            val out = FileOutputStream(imgFile)
                            bmImg.compress(Bitmap.CompressFormat.JPEG, 80, out)
                            out.flush()
                            out.close()
                        } else {
                            if (imgFile.exists()) {
                                imgFile.delete()
                            }
                        }
                    }
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    if (imgFile.exists()) {
                        imgFile.delete()
                    }
                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                    if (imgFile.exists()) {
                        imgFile.delete()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                    if (imgFile.exists()) {
                        imgFile.delete()
                    }
                }
            }
        }

        fun isFileWithSomething(file: File): Boolean {
            if (file.exists()) {
                val bytes = file.length().toDouble()
                val kilobytes = bytes / 1024
                if (kilobytes > 0) {
                    return true
                }
            }
            return false
        }
    }
}