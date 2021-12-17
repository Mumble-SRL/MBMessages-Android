package mumble.mburger.mbmessages

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.Space
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.NotificationCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Lifecycle
import mumble.mburger.mbmessages.iam.MBIAMConstants.MBIAMConstants
import mumble.mburger.mbmessages.iam.MBIAMData.CTA
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessage
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessageIAM
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessagePush
import mumble.mburger.mbmessages.iam.MBIAMPopups.DialogFragBottom
import mumble.mburger.mbmessages.iam.MBIAMPopups.DialogFragCenter
import mumble.mburger.mbmessages.iam.MBIAMPopups.DialogFragFullImage
import mumble.mburger.mbmessages.iam.MBIAMPopups.DialogFragTop
import mumble.mburger.mbmessages.metrics.MBMessagesMetrics
import mumble.mburger.sdk.kt.Common.MBCommonMethods
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.random.Random


class MBMessagesManager {

    companion object {

        lateinit var MBMessages: ArrayList<MBMessage>

        var currentPosition = 0

        /**
         * STYLING AND CUSTOMIZATION
         **/

        /**Force the message to be one of the following: MBIAMConstants#IAM_STYLE_BANNER_TOP,
         * MBIAMConstants#IAM_STYLE_BANNER_BOTTOM, MBIAMConstants#IAM_STYLE_BANNER_CENTER, MBIAMConstants#IAM_STYLE_FULL_SCREEN_IMAGE
         **/
        var forceMessageStyle: String? = null

        /**Int Color reference for the background of the card**/
        var backgroundColor: Int? = null

        /**Int Color reference for the title**/
        var titleColor: Int? = null

        /**Int Color reference for the body**/
        var bodyColor: Int? = null

        /**Int Color reference to tint the close button**/
        var closeButtonColor: Int? = null

        /**Int Color reference to tint the close button**/
        var closeButtonBackgroundColor: Int? = null

        /**Int Color reference for the first button background**/
        var button1BackgroundColor: Int? = null

        /**Int Color reference for the first button text**/
        var button1TitleColor: Int? = null

        /**Int Color reference for the second button background**/
        var button2BackgroundColor: Int? = null

        /**Int Color reference for the second button text**/
        var button2TitleColor: Int? = null

        /**Title font resource**/
        var titleFontRes: Int? = null

        /**Body font resource**/
        var bodyFontRes: Int? = null

        /**Buttons font resource**/
        var buttonsTextFontRes: Int? = null

        /**Title text size resource**/
        var titleSizeRes: Int? = null

        /**Body text size resource**/
        var bodySizeRes: Int? = null

        /**Buttons text size resource**/
        var buttonsSizeRes: Int? = null

        var debugMode = false

        var clickListener: MNBIAMClickListener? = null


        fun overrideColorsAndStyle(content: MBMessageIAM) {
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

            if (closeButtonBackgroundColor != null) {
                content.closeButtonBGColor = closeButtonBackgroundColor!!
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

        fun startFlow(mbMessage: MBMessage, activity: FragmentActivity) {
            val isActivityInForeground = activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (!activity.isFinishing && isActivityInForeground) {
                if ((mbMessage.type == MBIAMConstants.CAMPAIGN_MESSAGE) && (mbMessage.content is MBMessageIAM)) {
                    val content = mbMessage.content as MBMessageIAM
                    if (debugMode) {
                        show(activity, mbMessage, content)
                    } else {
                        if (canShow(activity.applicationContext, content.id, mbMessage.repeat, mbMessage.ends_at)) {
                            show(activity, mbMessage, content, mbMessage)
                        }
                    }
                }
            }
        }

        fun getSeenMapIds(context: Context): ArrayList<MutableMap<Long, Int>> {
            val maps = ArrayList<MutableMap<Long, Int>>()
            val jMessagesSeen = JSONArray(MBCommonMethods.getSharedPreferences(context)!!
                    .getString(MBIAMConstants.PROPERTIES_MESSAGES_SEEN, "[]"))

            for (i in 0 until jMessagesSeen.length()) {
                val map = mutableMapOf<Long, Int>()
                if (jMessagesSeen.get(i) is JSONObject) {
                    val jObj = jMessagesSeen.getJSONObject(i)
                    map[jObj.getLong("id")] = jObj.getInt("repeated")
                    maps.add(map)
                } else {
                    return maps
                }
            }

            return maps
        }

        fun addMessageSeen(id: Long, context: Context) {
            val mapIds = getSeenMapIds(context)
            var found = false
            for (map in mapIds) {
                if (map.containsKey(id)) {
                    val repeated = if (map[id] != null) map[id]!! else 0
                    map[id] = repeated + 1
                    found = true
                    break
                }
            }

            if (!found) {
                val mp = mutableMapOf<Long, Int>()
                mp[id] = 1
                mapIds.add(mp)
            }

            val jMessagesSeen = JSONArray()
            for (map in mapIds) {
                val id = map.keys.toList()[0]
                val jObj = JSONObject().put("id", id)
                        .put("repeated", map[id])
                jMessagesSeen.put(jObj)
            }

            MBCommonMethods.getSharedPreferencesEditor(context)!!
                    .putString(MBIAMConstants.PROPERTIES_MESSAGES_SEEN, jMessagesSeen.toString()).commit()
        }

        fun canShow(context: Context, id: Long, repeat: Int, ends_at: Long): Boolean {
            val mapIds = getSeenMapIds(context)
            for (map in mapIds) {
                if (map.containsKey(id)) {
                    val repeated = if (map[id] != null) map[id]!! else 0
                    if (repeated == 0) return true
                    return repeated < repeat
                }
            }

            //Added here ends_at check, to check if it's correct
            if(ends_at != null){
                if(System.currentTimeMillis() > TimeUnit.SECONDS.toMillis(ends_at)){
                    return false;
                }
            }

            return true
        }

        private fun show(activity: FragmentActivity, mbMessage: MBMessage, content: MBMessageIAM) {
            val isActivityInForeground = activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (!activity.isFinishing && isActivityInForeground) {
                overrideColorsAndStyle(content)
                when (content.type) {
                    MBIAMConstants.IAM_STYLE_BANNER_TOP -> {
                        val dialog = DialogFragTop()
                        dialog.initialize(this, mbMessage, content)
                        dialog.show(activity.supportFragmentManager, null)
                    }

                    MBIAMConstants.IAM_STYLE_FULL_SCREEN_IMAGE -> {
                        val dialog = DialogFragFullImage()
                        dialog.initialize(this, mbMessage, content)
                        dialog.show(activity.supportFragmentManager, null)
                    }

                    MBIAMConstants.IAM_STYLE_BANNER_BOTTOM -> {
                        val dialog = DialogFragBottom()
                        dialog.initialize(this, mbMessage, content)
                        dialog.show(activity.supportFragmentManager, null)
                    }

                    else -> {
                        val dialog = DialogFragCenter()
                        dialog.initialize(this, mbMessage, content)
                        dialog.show(activity.supportFragmentManager, null)
                    }
                }
            }
        }

        fun startFlow(activity: FragmentActivity) {
            val isActivityInForeground = activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (!activity.isFinishing && isActivityInForeground) {
                currentPosition = 0
                for (i in 0 until MBMessages.size) {
                    val mbMessage = MBMessages[i]
                    if ((mbMessage.type == MBIAMConstants.CAMPAIGN_MESSAGE) && (mbMessage.content is MBMessageIAM) && (mbMessage.automation == 0)) {
                        val content = mbMessage.content as MBMessageIAM
                        if (debugMode) {
                            currentPosition = i
                            show(activity, mbMessage, content)
                            break
                        } else {
                            if (canShow(activity.applicationContext, content.id, mbMessage.repeat, mbMessage.ends_at)) {
                                currentPosition = i
                                show(activity, mbMessage, content)
                                break
                            }
                        }
                    }
                }
            }
        }

        fun continueFlow(activity: FragmentActivity) {
            val isActivityInForeground = activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (!activity.isFinishing && isActivityInForeground) {
                for (i in (currentPosition + 1) until MBMessages.size) {
                    val mbMessage = MBMessages[i]
                    if ((mbMessage.type == MBIAMConstants.CAMPAIGN_MESSAGE) && (mbMessage.content is MBMessageIAM)) {
                        val content = mbMessage.content as MBMessageIAM
                        if (debugMode) {
                            currentPosition = i
                            show(activity, mbMessage, content)
                            break
                        } else {
                            if (canShow(activity.applicationContext, content.id, mbMessage.repeat, mbMessage.ends_at)) {
                                currentPosition = i
                                show(activity, mbMessage, content)
                                break
                            }
                        }
                    }
                }x
            }
        }

        fun putDataInIAM(context: Context, content: MBMessageIAM, layout: ViewGroup, txt_title: AppCompatTextView?, txt_message: AppCompatTextView?,
                         image: AppCompatImageView, ctaBtn1: AppCompatButton, ctaBtn2: AppCompatButton, btnSpace: Space?, closeBtn: AppCompatImageView?) {

            if ((titleFontRes != null) && (txt_title != null)) {
                txt_title.typeface = ResourcesCompat.getFont(context, titleFontRes!!)
            }

            if ((bodyFontRes != null) && (txt_message != null)) {
                txt_message.typeface = ResourcesCompat.getFont(context, bodyFontRes!!)
            }

            if (buttonsTextFontRes != null) {
                ctaBtn1.typeface = ResourcesCompat.getFont(context, buttonsTextFontRes!!)
                ctaBtn2.typeface = ResourcesCompat.getFont(context, buttonsTextFontRes!!)
            }

            if ((titleSizeRes != null) && (txt_title != null)) {
                txt_title.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimensionPixelSize(titleSizeRes!!).toFloat())
            }

            if ((bodySizeRes != null) && (txt_message != null)) {
                txt_message.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimensionPixelSize(bodySizeRes!!).toFloat())
            }

            if (buttonsSizeRes != null) {
                ctaBtn1.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimensionPixelSize(buttonsSizeRes!!).toFloat())
                ctaBtn2.setTextSize(TypedValue.COMPLEX_UNIT_PX, context.resources.getDimensionPixelSize(buttonsSizeRes!!).toFloat())
            }

            if (content.title_color != null) {
                txt_title?.setTextColor(content.title_color!!)
            }

            if (content.content_color != null) {
                txt_message?.setTextColor(content.content_color!!)
            }

            if (content.title != null) {
                txt_title?.text = content.title
            } else {
                txt_title?.visibility = View.GONE
            }

            if (content.content != null) {
                txt_message?.text = content.content
            } else {
                txt_message?.visibility = View.GONE
            }

            if (content.image != null) {
                setImageFromMemory(context, content.id.toString(), image)
            } else {
                image.visibility = View.GONE
            }

            if (content.backgroundColor != null) {
                ViewCompat.setBackgroundTintList(layout, ColorStateList.valueOf(content.backgroundColor!!))
            }

            if (content.cta1 != null) {
                val cta1 = content.cta1!!
                ctaBtn1.text = cta1.text

                if (cta1.background_color != null) {
                    ViewCompat.setBackgroundTintList(ctaBtn1, ColorStateList.valueOf(cta1.background_color!!))
                }

                if (cta1.text_color != null) {
                    ctaBtn1.setTextColor(cta1.text_color!!)
                }
            } else {
                ctaBtn1.visibility = View.GONE
                btnSpace?.visibility = View.GONE
            }

            if (content.cta2 != null) {
                val cta2 = content.cta2!!
                ctaBtn2.text = cta2.text

                if (cta2.background_color != null) {
                    ViewCompat.setBackgroundTintList(ctaBtn2, ColorStateList.valueOf(cta2.background_color!!))
                }

                if (cta2.text_color != null) {
                    ctaBtn2.setTextColor(cta2.text_color!!)
                }
            } else {
                ctaBtn2.visibility = View.GONE
                btnSpace?.visibility = View.GONE
            }

            if ((closeButtonColor != null) && (closeBtn != null)) {
                ImageViewCompat.setImageTintList(closeBtn, ColorStateList.valueOf(closeButtonColor!!))
            }

            if ((closeButtonBackgroundColor != null) && (closeBtn != null)) {
                ViewCompat.setBackgroundTintList(closeBtn, ColorStateList.valueOf(closeButtonBackgroundColor!!))
            }
        }

        fun setImageFromMemory(context: Context, id: String, image: AppCompatImageView) {
            val extStorage = context.getExternalFilesDir(null)
            if (!extStorage!!.exists()) {
                extStorage.mkdirs()
            }

            val imgFile = File(extStorage, "%s.jpg".format(id))
            if (imgFile.exists()) {
                image.setImageBitmap(BitmapFactory.decodeFile(imgFile.absolutePath))
            }
        }

        fun getImageSizeFullScreen(act: FragmentActivity, id: String): Array<Int> {
            val sArray = arrayOf(0, 0)
            val isActivityInForeground = act.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (!act.isFinishing && isActivityInForeground) {
                val extStorage = act.getExternalFilesDir(null)
                if (!extStorage!!.exists()) {
                    extStorage.mkdirs()
                }

                val imgFile = File(extStorage, "%s.jpg".format(id))
                if (imgFile.exists()) {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(imgFile.absolutePath, options)
                    val imageWidth = options.outWidth
                    val imageHeight = options.outHeight
                    val margins = act.resources.getDimensionPixelSize(R.dimen.padding_large) * 2
                    val resultWidth = MBCommonMethods.getScreenWidth(act) - margins
                    val resultHeight = ((resultWidth.toFloat() * imageHeight.toFloat()) / imageWidth.toFloat()).toInt()
                    sArray[0] = resultWidth
                    sArray[1] = resultHeight
                } else {
                    val dimen = MBCommonMethods.getScreenWidth(act) / 2
                    sArray[0] = dimen
                    sArray[1] = dimen
                }
            }

            return sArray
        }

        fun getImageSizeCenter(act: FragmentActivity, id: String): Array<Int> {
            val sArray = arrayOf(0, 0)
            val isActivityInForeground = act.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (!act.isFinishing && isActivityInForeground) {
                val extStorage = act.getExternalFilesDir(null)
                if (!extStorage!!.exists()) {
                    extStorage.mkdirs()
                }

                val imgFile = File(extStorage, "%s.jpg".format(id))
                if (imgFile.exists()) {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeFile(imgFile.absolutePath, options)
                    val imageWidth = options.outWidth
                    val imageHeight = options.outHeight
                    val resultHeight = MBCommonMethods.getScreenWidth(act) / 3
                    val resultWidth = ((imageWidth.toFloat() * resultHeight.toFloat()) / imageHeight.toFloat()).toInt()
                    sArray[0] = resultWidth
                    sArray[1] = resultHeight
                } else {
                    val dimen = MBCommonMethods.getScreenWidth(act) / 2
                    sArray[0] = dimen
                    sArray[1] = dimen
                }
            }

            return sArray
        }

        fun setClick(activity: FragmentActivity, fragDialog: DialogFragment, cta: CTA?, message_id: String, is_blocking: Boolean) {
            val isActivityInForeground = activity.lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
            if (!activity.isFinishing && isActivityInForeground) {
                if (cta != null) {
                    if ((cta.action_type != null) && (cta.action != null)) {
                        if (cta.action_type == MBIAMConstants.ACTION_LINK) {
                            try {
                                val intent = Intent(Intent.ACTION_VIEW).setData(Uri.parse(cta.action))
                                activity.startActivity(intent)
                            } catch (exception: ActivityNotFoundException) {
                                Log.d("MBMessages", "Activity not found for handling content")
                            }
                        } else {
                            clickListener?.onCTAClicked(cta)
                        }
                    }
                }

                MBMessagesMetrics.trackInteractionMessage(activity.applicationContext, message_id)

                if (!is_blocking)
                    fragDialog.dismiss()
            }
        }

        fun showNotification(context: Context, channel_id: String, small_icon: Int, message: MBMessage) {
            if (message.content is MBMessagePush) {
                val content = message.content as MBMessagePush
                if (!canShow(context, message.id, 0, message.ends_at)) {
                    return
                } else {
                    if (message.send_after_days > 0) {
                        val calNow = Calendar.getInstance()
                        calNow.add(Calendar.DAY_OF_YEAR, message.send_after_days)
                        scheduleNotification(context, channel_id, small_icon, message.id, calNow.timeInMillis, content, message.repeat)
                    } else {
                        showLocal(context, channel_id, small_icon, message.id, content.title, content.body, message.repeat)
                    }
                }
            }
        }

        fun scheduleNotification(context: Context, channel_id: String, small_icon: Int, message_id: Long,
                                 new_millis: Long, content: MBMessagePush, repeat: Int) {
            val reqCode = message_id.toInt()
            val intent = Intent(context, AlarmReceiverScheduledNotifications::class.java)
            intent.putExtra("channel_id", channel_id)
            intent.putExtra("small_icon", small_icon)
            intent.putExtra("title", content.title)
            intent.putExtra("body", content.body)
            intent.putExtra("id", message_id)
            intent.putExtra("repeat", repeat)

            val pendingIntent = PendingIntent.getBroadcast(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(pendingIntent)
            if (Build.VERSION.SDK_INT >= 19) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, new_millis, pendingIntent)
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, new_millis, pendingIntent)
            }
        }

        fun showLocal(context: Context, channel_id: String, small_icon: Int, message_id: Long,
                      title: String?, body: String?, repeat: Int) {

            if (!canShow(context, message_id, repeat, null)) {
                return
            }

            var curTitle = title
            if (curTitle == null) {
                curTitle = "";
            }

            var curBody = body
            if (curBody == null) {
                curBody = "";
            }

            val nId = Random.nextInt()
            val pm = context.packageManager
            val intent = pm.getLaunchIntentForPackage(context.packageName)
            val contentIntent = PendingIntent.getActivity(context, nId, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            val mNotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val notificationBuilder = NotificationCompat.Builder(context, channel_id)
            notificationBuilder.setContentTitle(curTitle)
                    .setSmallIcon(small_icon)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(curBody))
                    .setAutoCancel(true)
                    .setContentText(curBody)
                    .setContentIntent(contentIntent)

            mNotificationManager.notify(nId, notificationBuilder.build())
            addMessageSeen(message_id, context)
        }
    }

    interface MNBIAMClickListener {
        fun onCTAClicked(cta: CTA)
    }
}