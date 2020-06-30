package mumble.mbmessages.iam.popups

import android.animation.Animator
import android.animation.Animator.AnimatorListener
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import com.example.mb_messages.R
import kotlinx.android.synthetic.main.dialog_frag_bottom.*
import mumble.mburger.sdk.kt.Common.MBCommonMethods

class DialogFragBottom : androidx.fragment.app.DialogFragment() {

    var i = 0
    var x = -1f
    var y = -1f

    var highestPoint = -1f
    var threshold = -1f

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.dialog_frag_bottom, container, false)

        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dimen = MBCommonMethods.getScreenWidth(requireActivity()) / 6
        dfrag_bottom_img.layoutParams.width = dimen
        dfrag_bottom_img.layoutParams.height = dimen

        dfrag_bottom_btn_1.setOnClickListener {
            dismiss()
        }

        dfrag_bottom_btn_2.setOnClickListener {
            dismiss()
        }

        dfrag_bottom_drag_layout.setOnTouchListener { v, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    val tmpY = event.rawY
                    if (tmpY > highestPoint) {
                        y = tmpY
                        if (event.action == MotionEvent.ACTION_MOVE) {
                            dfrag_bottom_layout.y = (y - resources.getDimensionPixelSize(R.dimen.padding_mmlarge) * 2)
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
                    if (y > threshold) {
                        hideDown()
                        true
                    } else {
                        returnUp()
                        true
                    }
                }

                else -> true
            }
        }

        dfrag_bottom_drag_layout.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                highestPoint = dfrag_bottom_layout.y + resources.getDimensionPixelSize(R.dimen.padding_mmlarge)
                threshold = highestPoint + (dfrag_bottom_layout.height / 2)
                dfrag_bottom_drag_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    fun hideDown() {
        val va = ValueAnimator.ofFloat(y, MBCommonMethods.getScreenHeight(requireActivity()).toFloat() + resources.getDimensionPixelSize(R.dimen.padding_small))
        val mDuration = 300
        va.duration = mDuration.toLong()
        va.interpolator = DecelerateInterpolator()
        va.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                dismiss()
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        va.addUpdateListener { animation -> dfrag_bottom_layout.y = ((animation.animatedValue as Float) - resources.getDimensionPixelSize(R.dimen.padding_mmlarge)) }
        va.start()
    }

    fun returnUp() {
        val va = ValueAnimator.ofFloat(y, highestPoint)
        val mDuration = 300
        va.duration = mDuration.toLong()
        va.interpolator = AccelerateDecelerateInterpolator()
        va.addListener(object : AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        va.addUpdateListener { animation -> dfrag_bottom_layout.y = ((animation.animatedValue as Float) - resources.getDimensionPixelSize(R.dimen.padding_mmlarge)) }
        va.start()
    }


    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window!!.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.window!!.attributes.windowAnimations = R.style.bottom_style
            dialog.window!!.setLayout(width, height)
        }
    }
}