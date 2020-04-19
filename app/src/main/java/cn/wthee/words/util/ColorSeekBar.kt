package cn.wthee.words.util

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatSeekBar


class ColorSeekBar : AppCompatSeekBar {

    companion object {
        val DEFAULT_COLORS = arrayListOf(
            0x000000, 0xFF0000, 0xFF00FF, 0x0000FF,
            0x00FFFF, 0x00FF00, 0xFFFF00, 0xFFFFFF
        )
    }

    //背景画笔
    private lateinit var mBackgroundPaint: Paint

    //游标画笔
    private lateinit var mThumbPaint: Paint

    //默认
    private val TRACKTOUCH_NONE = -1

    //开始拖动
    private val TRACKTOUCH_START = 0
    private var mTrackTouch = TRACKTOUCH_NONE

    private var mOnChangeListener: OnChangeListener? = null
    private var mOnDrawListener: OnDrawListener? = null

    private var isTrackingTouch = false

    //渐变颜色数组
    private var backgroundColors = gradientColor(Color.GRAY, Color.GRAY, max)
    private var backgroundRects = ArrayList<RectF>()

    private var rSize = 0f
    private var leftX = 0f
    private var stepWidth = 0f

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    //初始化
    private fun init() {

        setBackgroundColor(Color.TRANSPARENT)

        //背景画笔
        mBackgroundPaint = Paint()
        mBackgroundPaint.isDither = true
        mBackgroundPaint.isAntiAlias = true
        mBackgroundPaint.color = Color.GRAY

        //游标画笔
        mThumbPaint = Paint()
        mThumbPaint.isDither = true
        mThumbPaint.isAntiAlias = true
        mThumbPaint.color = Color.BLUE

        setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (mTrackTouch == TRACKTOUCH_START) {
                    if (mOnChangeListener != null) {
                        mOnChangeListener!!.onProgressChanged(this@ColorSeekBar)
                    }
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                isTrackingTouch = true
                if (mTrackTouch == TRACKTOUCH_NONE) {
                    setTrackTouch(TRACKTOUCH_START)
                    if (mOnChangeListener != null) {
                        mOnChangeListener!!.onTrackingTouchStart(this@ColorSeekBar)
                    }
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                isTrackingTouch = false
                if (mTrackTouch == TRACKTOUCH_START) {
                    if (mOnChangeListener != null) {
                        mOnChangeListener!!.onTrackingTouchFinish(this@ColorSeekBar)
                    }
                }
            }
        })

    }

    @SuppressLint("DrawAllocation")
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        rSize = if (isTrackingTouch) {
            height / 3f
        } else {
            height / 4f
        }
        leftX = if (progress > 0) {
            0f
        } else {
            rSize
        }
        stepWidth = width.toFloat() / max

        for (i in 0 until max) {
            val backgroundRect = RectF(
                leftX, (height / 2 - height / 4 / 3).toFloat(), width.toFloat(),
                (height / 2 + height / 4 / 3).toFloat()
            )
            backgroundRects.add(backgroundRect)
            leftX += stepWidth
        }
    }

    override fun onDraw(canvas: Canvas) {
        //背景渐变
        mBackgroundPaint.color = backgroundColors[0]
        for (i in 0 until max) {
            canvas.drawRoundRect(backgroundRects[i], rSize, rSize, mBackgroundPaint)
            mBackgroundPaint.color = backgroundColors[i]
        }

        if (max != 0) {
            mThumbPaint.color = backgroundColors[progress]
            var cx = progress.toFloat() / max * width
            cx = if (cx + rSize > width) {
                width - rSize
            } else {
                cx.coerceAtLeast(rSize)
            }
            val cy = height / 2f
            canvas.drawCircle(cx, cy, rSize, mThumbPaint)
        }
        mOnDrawListener?.onDrawFinish(this)
    }

    //设置背景渐变色
    fun setBackgroundGradientColors(colorArray: ArrayList<Int>) {
        //背景渐变色数组
        backgroundColors = arrayListOf()
        val colorSize = colorArray.size - 1
        colorArray.forEachIndexed { index, _ ->
            if (index != colorSize) {
                backgroundColors.addAll(
                    gradientColor(
                        colorArray[index],
                        colorArray[index + 1],
                        max / colorSize
                    )
                )
            }
        }
        //不足的用最后的颜色填充
        for (i in backgroundColors.size..max) {
            backgroundColors.add(backgroundColors.last())
        }
    }


    @Synchronized
    override fun setProgress(progress: Int) {
        if (mTrackTouch == TRACKTOUCH_NONE && max != 0) {
            super.setProgress(progress)
        }
        postInvalidate()
    }

    @Synchronized
    override fun setSecondaryProgress(secondaryProgress: Int) {
        super.setSecondaryProgress(secondaryProgress)
        postInvalidate()
    }

    @Synchronized
    override fun setMax(max: Int) {
        super.setMax(max)
        postInvalidate()
    }

    @Synchronized
    private fun setTrackTouch(trackTouch: Int) {
        this.mTrackTouch = trackTouch
    }


    //设置游标颜色
    fun setThumbColor(thumbColor: Int) {
        mThumbPaint.color = thumbColor
        postInvalidate()
    }

    //获取游标颜色
    fun getThumbColor(): Int {
        return mThumbPaint.color
    }

    fun setOnChangeListener(onChangeListener: OnChangeListener) {
        this.mOnChangeListener = onChangeListener
    }

    fun setOnDrawListener(onDrawListener: OnDrawListener) {
        this.mOnDrawListener = onDrawListener
    }

    /**
     * 传入起始颜色 [startColor] 、终止颜色 [endColor]、渐变步长 [step]
     * 获取渐变数组
     * */
    private fun gradientColor(startColor: Int, endColor: Int, step: Int): ArrayList<Int> {
        //起始颜色
        val startR = startColor and 0xff0000 shr 16
        val startG = startColor and 0x00ff00 shr 8
        val startB = startColor and 0x0000ff
        //终止颜色
        val endR = endColor and 0xff0000 shr 16
        val endG = endColor and 0x00ff00 shr 8
        val endB = endColor and 0x0000ff
        //总差值
        val sR = (endR - startR).toDouble() / step
        val sG = (endG - startG).toDouble() / step
        val sB = (endB - startB).toDouble() / step

        val colorArr = arrayListOf<Int>()

        for (i in 0 until step) {
            //计算每一步的hex值
            val hex =
                Color.rgb(
                    (sR * i + startR).toInt(),
                    (sG * i + startG).toInt(),
                    (sB * i + startB).toInt()
                )
            colorArr.add(hex)
        }
        return colorArr
    }

    abstract class OnChangeListener {
        // 进度改变
        open fun onProgressChanged(seekBar: ColorSeekBar) {}

        //开始拖动
        open fun onTrackingTouchStart(seekBar: ColorSeekBar) {}

        //拖动结束
        open fun onTrackingTouchFinish(seekBar: ColorSeekBar) {}
    }

    interface OnDrawListener{
        fun onDrawFinish(seekBar: ColorSeekBar)
    }
}
