package cn.wthee.words.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity


object CommonUtil {
    //默认值
    const val wordSize = 60f
    const val wordColor = Color.BLUE
    const val wordColorProgress1 = 0
    const val wordColorProgress2 = 50
    const val lineWidth = 4f
    const val lineSize = 100f
    const val tzgColor = Color.RED
    const val tzgColorProgress1 = 0
    const val tzgColorProgress2 = 50
    val word: String? = "词"
    val wordEx: String? = "解释一下"

    //根据手机的分辨率从 dp 转成为 px(像素)
    fun dip2px(context: Context, dpValue: Float): Int {
        val scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    //根据手机的分辨率从 px(像素) 的单位 转成为 dp
    fun px2dip(context: Context, pxValue: Float) : Int{
        val scale = context.resources.displayMetrics.density
        return  (pxValue / scale + 0.5f).toInt()
    }

    /**
     * 显示键盘
     */
    fun showInput(activity: FragmentActivity, input: View) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(input, 0)
    }

    /**
     * 隐藏键盘
     */
    fun hideInput(activity: FragmentActivity, input: View) {
        val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(input.windowToken, 0)
    }
}