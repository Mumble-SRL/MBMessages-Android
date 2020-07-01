package mumble.mbmessages.iam.MBIAMPopups

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.example.mb_messages.R
import kotlinx.android.synthetic.main.dialog_frag_center.*
import mumble.mbmessages.iam.MBIAMData.CampaignIAM
import mumble.mbmessages.iam.MBMessagesManager
import mumble.mbmessages.metrics.MBMessagesMetrics
import mumble.mburger.sdk.kt.Common.MBCommonMethods

class DialogFragCenter : androidx.fragment.app.DialogFragment() {

    lateinit var father: MBMessagesManager
    lateinit var content: CampaignIAM

    fun initialize(father: MBMessagesManager, content: CampaignIAM) {
        this.father = father
        this.content = content
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_frag_center, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dimen = MBCommonMethods.getScreenWidth(requireActivity()) / 2
        dfrag_center_img.layoutParams.width = dimen
        dfrag_center_img.layoutParams.height = dimen

        dfrag_center_close.setOnClickListener {
            dismiss()
        }

        dfrag_center_btn_1.setOnClickListener {
            father.setClick(requireActivity(), this, content.cta1, content.id.toString())
        }

        dfrag_center_btn_2.setOnClickListener {
            father.setClick(requireActivity(), this, content.cta2, content.id.toString())
        }

        father.putDataInIAM(requireContext(), content, dfrag_center_layout, dfrag_center_txt_title,
                dfrag_center_txt_message, dfrag_center_img, dfrag_center_btn_1, dfrag_center_btn_2, null, dfrag_center_close)

        MBMessagesMetrics.trackShowMessage(requireContext(), content.id.toString())
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

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        father.addMessageSeen(content.id, requireContext())
        father.continueFlow(requireActivity())
    }
}