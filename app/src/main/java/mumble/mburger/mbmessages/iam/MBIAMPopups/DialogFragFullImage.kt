package mumble.mburger.mbmessages.iam.MBIAMPopups

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import kotlinx.android.synthetic.main.dialog_frag_fullimage.*
import mumble.mburger.mbmessages.R
import mumble.mburger.mbmessages.iam.MBIAMData.CampaignIAM
import mumble.mburger.mbmessages.iam.MBMessagesManager
import mumble.mburger.mbmessages.metrics.MBMessagesMetrics

class DialogFragFullImage : androidx.fragment.app.DialogFragment() {

    lateinit var father: MBMessagesManager
    lateinit var content: CampaignIAM

    fun initialize(father: MBMessagesManager, content: CampaignIAM) {
        this.father = father
        this.content = content
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_frag_fullimage, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imageSizeArr = father.getImageSizeFullScreen(requireActivity(), content.id.toString())
        dfrag_fullimage_layout.layoutParams.width = imageSizeArr[0]
        dfrag_fullimage_layout.layoutParams.height = imageSizeArr[1]

        dfrag_fullimage_close.setOnClickListener {
            dismiss()
        }

        dfrag_fullimage_btn_1.setOnClickListener {
            father.setClick(requireActivity(), this, content.cta1, content.id.toString())
        }

        dfrag_fullimage_btn_2.setOnClickListener {
            father.setClick(requireActivity(), this, content.cta2, content.id.toString())
        }

        father.putDataInIAM(requireContext(), content, dfrag_fullimage_layout, null, null,
                dfrag_fullimage_img, dfrag_fullimage_btn_1, dfrag_fullimage_btn_2, null,
                dfrag_fullimage_close)

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