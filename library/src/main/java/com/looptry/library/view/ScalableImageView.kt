package com.looptry.library.view

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.OverScroller
import com.blankj.utilcode.util.ImageUtils
import com.looptry.library.R
import com.looptry.library.util.toPx
import kotlin.properties.Delegates

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
class ScalableImageView(
    context: Context,
    attr: AttributeSet? = null
) : View(context, attr), GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener {

    private var bitmap: Bitmap

    init {
        val typeArray = context.obtainStyledAttributes(attr, R.styleable.ScalableImageView)
        typeArray.getResourceId(R.styleable.ScalableImageView_android_src, R.drawable.temp).also {
            bitmap = ImageUtils.getBitmap(it)
        }
        typeArray.recycle()
    }

    //用于图片缩放处理
    private var minScale by Delegates.notNull<Float>()
    private var maxScale by Delegates.notNull<Float>()
    private var moreScale = 1.5f

    //用于初始图片偏移量
    private var originOffsetX by Delegates.notNull<Float>()
    private var originOffsetY by Delegates.notNull<Float>()

    //滑动偏移量
    private var offsetX = 0f
    private var offsetY = 0f

    //边界
    private val margin = 0.toPx()

    //是否被放大
    private var useBig = false

    //用于控制动画
    private var scaleTrans: Float = 0f
        set(value) {
            field = value
            invalidate()
        }

    //放大/缩小动画
    private val animator = ObjectAnimator.ofFloat(this, "scaleTrans", 0f, 1f)

    //处理点击事件
    private val mGestureDetector by lazy {
        GestureDetector(context, this)
    }

    //处理flip事件
    val overScroller = OverScroller(context)

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        //屏幕宽高比<图片宽高比
        if (w / h.toFloat() < bitmap.width / bitmap.height.toFloat()) {
            maxScale = (h - 2 * margin) / bitmap.height.toFloat()
            minScale = (w - 2 * margin) / bitmap.width.toFloat()
        } else {
            maxScale = (w - 2 * margin) / bitmap.width.toFloat()
            minScale = (h - 2 * margin) / bitmap.height.toFloat()
        }
        "$minScale,$maxScale".logE()

        //计算原偏移量
        originOffsetX = (width - 2 * margin - bitmap.width) / 2f
        originOffsetY = (height - 2 * margin - bitmap.height) / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        check(canvas != null)
        //动态
        var scale = minScale + scaleTrans * maxScale

        canvas.save()
        //绘制图片
        if (useBig) {
            scale *= moreScale
            canvas.translate(originOffsetX, originOffsetY)
            canvas.scale(scale, scale, bitmap.width / 2f + margin, bitmap.height / 2f + margin)
            canvas.drawBitmap(
                bitmap,
                margin + offsetX / scale,
                margin + offsetY / scale,
                Paint(Paint.ANTI_ALIAS_FLAG)
            )
        } else {
            canvas.translate(originOffsetX, originOffsetY)
            canvas.scale(scale, scale, bitmap.width / 2f + margin, bitmap.height / 2f + margin)
            canvas.drawBitmap(
                bitmap,
                margin,
                margin,
                Paint(Paint.ANTI_ALIAS_FLAG)
            )
        }

        canvas.restore()
    }

    fun String.logE(tag: String = "TAG") {
        Log.e(tag, this)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mGestureDetector.onTouchEvent(event)
    }

    override fun onDown(e: MotionEvent?): Boolean {
        return true
    }

    override fun onShowPress(e: MotionEvent?) {

    }

    override fun onSingleTapUp(e: MotionEvent?): Boolean {
        return false
    }

    override fun onScroll(
        e1: MotionEvent?,
        e2: MotionEvent?,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (useBig) {
            val borderX = (bitmap.width * maxScale * moreScale - width) / 2
            val borderY = (bitmap.height * maxScale * moreScale - height) / 2
            offsetX -= distanceX
            if (offsetX !in -borderX..borderX) {
                offsetX = if (offsetX < 0) {
                    -borderX
                } else borderX
            }
            offsetY -= distanceY
            if (offsetY !in -borderY..borderY) {
                offsetY = if (offsetY < 0) -borderY else borderY
            }
            invalidate()
        }
        return false
    }

    override fun onLongPress(e: MotionEvent?) {

    }

    override fun onFling(
        e1: MotionEvent?,
        e2: MotionEvent?,
        velocityX: Float,
        velocityY: Float
    ): Boolean {
        val borderX = (bitmap.width * maxScale * moreScale - width) / 2
        val borderY = (bitmap.height * maxScale * moreScale - height) / 2
        overScroller.fling(
            e1?.x?.toInt()!!,
            e1?.y?.toInt()!!,
            velocityX.toInt(),
            velocityY.toInt(),
            -borderX.toInt(),
            borderX.toInt(),
            -borderY.toInt(),
            borderY.toInt()
        )

        return true
    }


    override fun onSingleTapConfirmed(e: MotionEvent?): Boolean {
        return false
    }

    override fun onDoubleTap(e: MotionEvent?): Boolean {
        useBig = !useBig
        if (useBig) {
            animator.start()
        } else {
            offsetX = 0f
            offsetY = 0f
            animator.reverse()
        }
        return false
    }

    override fun onDoubleTapEvent(e: MotionEvent?): Boolean {
        return false
    }
}