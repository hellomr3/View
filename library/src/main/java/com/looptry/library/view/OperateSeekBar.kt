package com.looptry.library.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.looptry.library.R
import kotlinx.android.synthetic.main.view_operate_seekbar.view.*

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
class OperateSeekBar(
    context: Context,
    attr: AttributeSet? = null
) : ConstraintLayout(context, attr) {

    // 顶部描述文字
    var title: String
        get() {
            return tvTitle.text.toString()
        }
        set(value) {
            tvTitle.text = value
        }

    //maxProgress
    var maxProgress: Int
        get() = operateSeekBar.max
        set(value) {
            operateSeekBar.max = value
        }

    //progress
    var progress: Int
        get() = operateSeekBar.progress
        set(value) {
            operateSeekBar.progress = value
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_operate_seekbar, this, true)
        val ta = context.obtainStyledAttributes(attr, R.styleable.OperateSeekBar)
        //title
        (ta.getString(R.styleable.OperateSeekBar_seekBar_title) ?: "").also {
            title = it
        }
        //title-color
        (ta.getColor(
            R.styleable.OperateSeekBar_seekBar_titleColor,
            getColorRes(R.color.colorWhite)
        )).also {
            tvTitle.setTextColor(it)
        }
        //seekBar-->maxProgress
        (ta.getInt(R.styleable.OperateSeekBar_seekBar_maxProgress, 100)).also {
            maxProgress = it
        }
        //seekBar-->progress
        (ta.getInt(R.styleable.OperateSeekBar_seekBar_progress, 0)).also {
            progress = it
        }
        //减事件
        setMinusListener(OnClickListener {
            progress -= 1
        })
        //加事件
        setAddListener(View.OnClickListener {
            progress += 1
        })
        //回收
        ta.recycle()
    }

    private fun getColorRes(@ColorRes color: Int): Int {
        return ContextCompat.getColor(context, color)
    }

    //设置减事件
    fun setMinusListener(listener: View.OnClickListener): OperateSeekBar {

        btnMinus.setOnClickListener {
            if (progress > 0)
                listener.onClick(it)
        }
        return this
    }

    //设置加事件
    fun setAddListener(listener: View.OnClickListener): OperateSeekBar {
        btnAdd.setOnClickListener {
            if (progress < maxProgress)
                listener.onClick(it)
        }
        return this
    }
}