package cn.wthee.words.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.view.View
import kotlin.math.cos

class FlodAnim(context: Context, private var view: View, private var duration: Long) {

    private var height = 0
    private var interpolator = TimeInterpolator {
        (cos((it + 1) * Math.PI) / 2.0f).toFloat() + 0.5f
    }
    private val density = context.resources.displayMetrics.density

    fun exec() {
        if (view.visibility == View.VISIBLE) {
            animateClose()
        } else {
            animateOpen()
        }
    }

    private fun animateOpen() {
        view.visibility = View.VISIBLE
        val animator = createDropAnimator(view, 0, height)
        animator.duration = duration
        animator.interpolator = interpolator
        animator.start()
    }


    private fun animateClose() {
        height = view.height
        val animator = createDropAnimator(view, view.height, 0)
        animator.duration = duration
        animator.interpolator = interpolator
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                view.visibility = View.GONE
            }
        })
        animator.start()
    }

    private fun createDropAnimator(v: View, start: Int, end: Int): ValueAnimator {
        val animator = ValueAnimator.ofInt(start, end)
        animator.addUpdateListener { arg0 ->
            val value = arg0.animatedValue as Int
            val layoutParams = v.layoutParams
            layoutParams.height = value
            v.layoutParams = layoutParams
        }
        return animator
    }
}