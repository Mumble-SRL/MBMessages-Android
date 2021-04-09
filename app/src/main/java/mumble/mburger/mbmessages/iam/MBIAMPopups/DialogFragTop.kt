package mumble.mburger.mbmessages.iam.MBIAMPopups

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_frag_top.*
import mumble.mburger.mbmessages.MBMessagesManager
import mumble.mburger.mbmessages.R
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessage
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessageIAM
import mumble.mburger.mbmessages.metrics.MBMessagesMetrics
import mumble.mburger.sdk.kt.Common.MBCommonMethods

class DialogFragTop : DialogFragment() {

    lateinit var father: MBMessagesManager.Companion
    lateinit var content: MBMessageIAM
    lateinit var mbMessage: MBMessage

    var clickListener: MBMessagesManager.MNBIAMClickListener? = null

    var i = 0
    var y = -1f
    var downPoint = -1f

    var highestPoint = -1f
    var threshold = -1f

    fun initialize(father: MBMessagesManager.Companion, mbMessage: MBMessage, content: MBMessageIAM) {
        this.father = father
        this.content = content
        this.mbMessage = mbMessage
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_frag_top, container, false)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        if (content.is_blocking) {
            isCancelable = false
            dialog?.setCancelable(false)
        }

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(content.is_blocking){
            dfrag_top_behind_view.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_very_alpha))
            dfrag_top_behind_view.isFocusable = true
            dfrag_top_behind_view.isClickable = true
            dfrag_top_behind_view.visibility = View.INVISIBLE

            Handler(Looper.getMainLooper()).postDelayed({
                dfrag_top_behind_view.visibility = View.VISIBLE
            }, 700)
        }

        val dimen = MBCommonMethods.getScreenWidth(requireActivity()) / 5
        dfrag_top_img.layoutParams.width = dimen
        dfrag_top_img.layoutParams.height = dimen

        val dragDimen = MBCommonMethods.getScreenWidth(requireActivity()) / 10
        dfrag_top_drag_layout.layoutParams.height = dragDimen

        dfrag_top_btn_1.setOnClickListener {
            father.setClick(requireActivity(), this, content.cta1, mbMessage.id.toString(), content.is_blocking)
        }

        dfrag_top_btn_2.setOnClickListener {
            father.setClick(requireActivity(), this, content.cta2, mbMessage.id.toString(), content.is_blocking)
        }

        father.putDataInIAM(requireContext(), content, dfrag_top_layout, dfrag_top_txt_title,
                dfrag_top_txt_message, dfrag_top_img, dfrag_top_btn_1, dfrag_top_btn_2, dfrag_top_space, null)

        setLayout()

        MBMessagesMetrics.trackShowMessage(requireContext(), mbMessage.id.toString())
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.window!!.attributes.windowAnimations = R.style.top_style
            dialog.window!!.setLayout(width, height)
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        father.addMessageSeen(content.id, requireContext())
        father.continueFlow(requireActivity())
    }

    fun hideUp() {
        val va = ValueAnimator.ofFloat(dfrag_top_whole_layout.y, -dfrag_top_whole_layout.height.toFloat())
        val mDuration = 300
        va.duration = mDuration.toLong()
        va.interpolator = DecelerateInterpolator()
        va.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                dismiss()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        va.addUpdateListener { animation -> dfrag_top_whole_layout.y = ((animation.animatedValue as Float) - resources.getDimensionPixelSize(R.dimen.padding_mmlarge) * 2) }
        va.start()
    }

    fun returnDown() {
        val va = ValueAnimator.ofFloat(dfrag_top_whole_layout.y, resources.getDimensionPixelSize(R.dimen.padding_small).toFloat())
        val mDuration = 300
        va.duration = mDuration.toLong()
        va.interpolator = AccelerateDecelerateInterpolator()
        va.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        va.addUpdateListener { animation -> dfrag_top_whole_layout.y = animation.animatedValue as Float }
        va.start()
    }

    fun setLayout() {
        if (!content.is_blocking) {
            dfrag_top_drag_layout.setOnTouchListener { v, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        downPoint = event.rawY
                        true
                    }

                    MotionEvent.ACTION_MOVE -> {
                        val tmpY = event.rawY
                        if (tmpY < highestPoint) {
                            y = tmpY
                            val diff = downPoint - y
                            downPoint = y
                            if (event.action == MotionEvent.ACTION_MOVE) {
                                dfrag_top_whole_layout.y = dfrag_top_whole_layout.y - diff
                            }

                            if (++i > 50) {
                                false
                            }
                        } else {
                            false
                        }

                        true
                    }

                    MotionEvent.ACTION_UP -> {
                        if (y < threshold) {
                            hideUp()
                            true
                        } else {
                            returnDown()
                            true
                        }
                    }

                    else -> true
                }
            }

            dfrag_top_drag_layout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    highestPoint = (MBCommonMethods.getStatusBarHeight(requireActivity()) + dfrag_top_whole_layout.height + resources.getDimensionPixelSize(R.dimen.padding_small)).toFloat()
                    threshold = highestPoint / 2.5f
                    dfrag_top_drag_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            })
        }
    }
}