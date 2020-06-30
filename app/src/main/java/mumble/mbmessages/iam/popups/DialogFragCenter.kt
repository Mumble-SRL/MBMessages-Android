package mumble.mbmessages.iam.popups

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.example.mb_messages.R
import kotlinx.android.synthetic.main.dialog_frag_center.*
import mumble.mburger.sdk.kt.Common.MBCommonMethods

class DialogFragCenter : androidx.fragment.app.DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_frag_center, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dimen = MBCommonMethods.getScreenWidth(requireActivity())/2
        dfrag_center_img.layoutParams.width = dimen
        dfrag_center_img.layoutParams.height = dimen

        dfrag_center_close.setOnClickListener {
            dismiss()
        }

        dfrag_center_btn_1.setOnClickListener {
            dismiss()
        }

        dfrag_center_btn_2.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setLayout(width, height)
        }
    }
}