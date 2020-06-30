package mumble.mbmessages.iam

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.view.View
import android.widget.LinearLayout
import android.widget.Space
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import mumble.mbmessages.iam.MBIAMData.CampaignIAM
import java.io.File

class MBMessagesStyler {

    companion object {

        fun putDataInIAM(context: Context, father: MBMessagesManager, content: CampaignIAM,
                         layout: LinearLayout, txt_title: AppCompatTextView, txt_message: AppCompatTextView,
                         image: AppCompatImageView, ctaBtn1: AppCompatButton, ctaBtn2: AppCompatButton, btnSpace: Space?) {

            if (father.titleFont != null) {
                txt_title.typeface = ResourcesCompat.getFont(context, father.titleFont!!)
            }

            if (father.bodyFont != null) {
                txt_message.typeface = ResourcesCompat.getFont(context, father.bodyFont!!)
            }

            if (father.buttonsTextFont != null) {
                ctaBtn1.typeface = ResourcesCompat.getFont(context, father.buttonsTextFont!!)
                ctaBtn2.typeface = ResourcesCompat.getFont(context, father.buttonsTextFont!!)
            }

            if (content.title_color != null) {
                txt_title.setTextColor(content.title_color!!)
            }

            if (content.content_color != null) {
                txt_message.setTextColor(content.content_color!!)
            }

            if (content.title != null) {
                txt_title.text = content.title
            } else {
                txt_title.visibility = View.GONE
            }

            if (content.content != null) {
                txt_message.text = content.content
            } else {
                txt_message.visibility = View.GONE
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
    }
}