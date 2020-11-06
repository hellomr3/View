package com.looptry.library.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.ViewGroup
import com.looptry.library.R

/**
 * Author: mr.3
 * Date:
 * Desc: 仿朋友圈图片实现方式
 * Modify By:
 * Modify Date:
 */
class FriendLayout(context: Context, attrs: AttributeSet?) : ViewGroup(context, attrs) {
    val TAG = this::class.java.simpleName

    companion object {
        //View间的公共间隔
        const val NORMAL_MARGIN = 20f
    }

    //当前Layout宽度
    private var layoutWidth: Float = 0f

    //当前单子View的宽度
    private var singleWidth: Float = 0f

    //处理多View的间隔
    private var normalSpace: Float = 0f

    //记录当前Top
    private var nowTop: Float = 0f

    //记录左侧
    private var nowLeft: Float = 0f

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.FriendLayout)
        val normalMargin =
            typedArray.getDimension(R.styleable.FriendLayout_normalMargin, NORMAL_MARGIN)
        this.normalSpace = normalMargin
        typedArray.recycle()
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (childCount > 9) throw  Throwable("FriendLayout子View不能超过9个")

        when (childCount) {
            1 -> {
                layout1()
            }
            2, 4 -> {
                layout24()
            }
            else -> {
                layout3()
            }
        }
    }

    //1
    private fun layout1() {

        val view = getChildAt(0)
        val widthSpec = MeasureSpec.makeMeasureSpec(singleWidth.toInt(), MeasureSpec.EXACTLY)
        view.measure(widthSpec, widthSpec)
        view.layout(0, 0, singleWidth.toInt(), singleWidth.toInt())
    }

    //2,4
    private fun layout24() {

        repeat(childCount) { index ->
            //处理当前top(最上方不处理space)
            nowTop = if (index / 2 == 0) {
                0f
            } else {
                (index / 2) * singleWidth + normalSpace
            }
            //处理左侧
            nowLeft = if (index % 2 == 0) {
                0f
            } else {
                singleWidth + normalSpace
            }
            val view = getChildAt(index)
            //改变view的大小
            val widthSpec = MeasureSpec.makeMeasureSpec(singleWidth.toInt(), MeasureSpec.EXACTLY)
            view.measure(widthSpec, widthSpec)
            //计算在Layout中的位置
            view.layout(
                nowLeft.toInt(),
                nowTop.toInt(),
                (nowLeft + singleWidth).toInt(),
                (nowTop + singleWidth).toInt()
            )
        }
    }

    //3,5,6,7,8,9
    private fun layout3() {
        repeat(childCount) { index ->
            val view = getChildAt(index)
            //处理当前top(最上方不处理space)
            nowTop = if (index / 3 == 0) {
                0f
            } else {
                val n = index / 3
                n * (singleWidth + normalSpace)
            }
            //处理左侧
            nowLeft = when (index % 3) {
                0 -> {
                    0f
                }
                1 -> {
                    singleWidth + normalSpace
                }
                else -> {
                    2 * (singleWidth + normalSpace)
                }
            }
            //重新测量子View
            val widthSpec = MeasureSpec.makeMeasureSpec(singleWidth.toInt(), MeasureSpec.EXACTLY)
            view.measure(widthSpec, widthSpec)
            //layout
            view.layout(
                nowLeft.toInt(),
                nowTop.toInt(),
                (nowLeft + singleWidth).toInt(),
                (nowTop + singleWidth).toInt()
            )
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        layoutWidth = w.toFloat()
        //计算不同模式下单个View的宽高
        measureSingleWidth()
    }

    private fun measureSingleWidth(){
        //计算不同模式下单个View的宽高
        singleWidth = when (childCount) {
            1 -> {
                layoutWidth * 2 / 3
            }
            2, 4 -> {
                (layoutWidth * 2 / 3 - normalSpace) / 2
            }
            else -> {
                (layoutWidth - 2 * normalSpace) / 3
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        //重新计算单个View的边长
        measureSingleWidth()
        var accumulateHeight = 0
        repeat(childCount) { index ->
            if (index == 0 || index == 3 || index == 6) {
                accumulateHeight += singleWidth.toInt()
            }
        }
        val width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, accumulateHeight)
    }


}