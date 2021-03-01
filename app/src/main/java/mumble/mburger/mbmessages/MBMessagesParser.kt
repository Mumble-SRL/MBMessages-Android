package mumble.mburger.mbmessages

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import mumble.mburger.mbmessages.iam.MBIAMConstants.MBIAMConstants
import mumble.mburger.mbmessages.iam.MBIAMData.CTA
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessage
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessageIAM
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessagePush
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

        fun parseMessages(context: Context, jCampaigns: JSONArray): ArrayList<MBMessage> {
            val campaigns = ArrayList<MBMessage>()
            try {
                for (i in 0 until jCampaigns.length()) {
                    val jCampaign = jCampaigns.getJSONObject(i)
                    val campaign = parseMessage(context, jCampaign)
                    campaigns.add(campaign)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return campaigns
        }

        fun parseMessage(context: Context, jsonObject: JSONObject): MBMessage {
            var id: Long = -1
            var title: String? = null
            var description: String? = null
            var type: String? = null
            var send_after_days: Int = 0
            var repeat: Int = 0
            var starts_at: Long = -1
            var ends_at: Long = -1
            var automation: Int = -1
            var sTriggers: String? = null
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
                    sTriggers = jsonObject.getString("triggers")
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

            return MBMessage(id, title, description, type, send_after_days, repeat, starts_at, ends_at, automation, sTriggers,
                    created_at, updated_at, content)
        }

        fun parseIAM(context: Context, jsonObject: JSONObject): MBMessageIAM {
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

                if (MBCommonMethods.isJSONOk(jsonObject, "cta_text")) {
                    var action_type:String? = null
                    var action:String? = null
                    var text_color = Color.BLACK
                    var bg_color = Color.WHITE

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta_action_type")) {
                        action_type = jsonObject.getString("cta_action_type")
                    }

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta_action")) {
                        action = jsonObject.getString("cta_action")
                    }

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta_text_color")) {
                        text_color = Color.parseColor(jsonObject.getString("cta_text_color"))
                    }

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta_background_color")) {
                        bg_color = Color.parseColor(jsonObject.getString("cta_background_color"))
                    }

                    cta1 = CTA(jsonObject.getString("cta_text"), text_color,
                            bg_color, action_type, action)
                }

                if (MBCommonMethods.isJSONOk(jsonObject, "cta2_text")) {

                    var action_type:String? = null
                    var action:String? = null
                    var text_color = Color.BLACK
                    var bg_color = Color.WHITE

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta2_action_type")) {
                        action_type = jsonObject.getString("cta2_action_type")
                    }

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta2_action")) {
                        action = jsonObject.getString("cta2_action")
                    }

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta2_text_color")) {
                        text_color = Color.parseColor(jsonObject.getString("cta2_text_color"))
                    }

                    if (MBCommonMethods.isJSONOk(jsonObject, "cta2_background_color")) {
                        bg_color = Color.parseColor(jsonObject.getString("cta2_background_color"))
                    }

                    cta2 = CTA(jsonObject.getString("cta2_text"), text_color,
                            bg_color, action_type, action)
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            return MBMessageIAM(id, type, title, content, title_color, content_color, backgroundColor, null,
                    null, cta1, cta2, durationInSeconds, expiresAt, image)
        }

        fun parsePush(jsonObject: JSONObject): MBMessagePush {
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

            return MBMessagePush(id, title, body, date, sent, topics, total, created_at, updated_at)
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