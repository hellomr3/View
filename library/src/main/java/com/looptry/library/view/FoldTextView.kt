package com.looptry.library.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.looptry.library.R
import com.looptry.library.util.getTintDrawable
import com.looptry.library.util.toPx
import kotlin.properties.Delegates


/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
class FoldTextView(context: Context, attrs: AttributeSet?) : LinearLayout(context, attrs) {

    //0折叠 1展开 2无操作
    var isFoldFlag = 2

    //折叠内容
    private val textView = TextView(context)

    private val textViewLayoutParams by lazy {
        LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER_VERTICAL or Gravity.START
        }
    }

    //控制按钮
    private val btnView = TextView(context)

    private val btnViewLayoutParams by lazy {
        LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.END
        }
    }

    //默认行
    var showLines by Delegates.notNull<Int>()

    //内容信息
    var content: CharSequence
        get() = textView.text
        set(value) {
            textView.text = value
        }

    //内容字体颜色
    var contentTextColor: Int
        get() = textView.textColors.defaultColor
        set(value) {
            textView.setTextColor(value)
        }

    //内容字体大小
    var contentTextSize: Float
        get() = textView.textSize
        set(value) {
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    //Btn 字
    var btnText: CharSequence
        get() = btnView.text
        set(value) {
            btnView.text = if (value.isBlank()) "点击展开" else value
        }

    //Btn 字体颜色
    var btnTextColor: Int
        get() = btnView.textColors.defaultColor
        set(value) {
            btnView.setTextColor(value)
        }

    //内容字体大小
    var btnTextSize: Float
        get() = btnView.textSize
        set(value) {
            btnView.setTextSize(TypedValue.COMPLEX_UNIT_PX, value)
        }

    //展开  折叠
    var unFoldIcon: Drawable?
        get() = btnView.compoundDrawables.getOrNull(2)
        set(value) {
            btnView.setCompoundDrawables(null, null, value, null)
        }

    var foldIcon: Drawable?
        get() = btnView.compoundDrawables.getOrNull(2)
        set(value) {
            value?.setBounds(0, 0, 32, 32)
            btnView.setCompoundDrawablesRelativeWithIntrinsicBounds(value, null, value, null)
        }

    init {
        orientation = LinearLayout.VERTICAL
        addView(textView, textViewLayoutParams)
        addView(btnView, btnViewLayoutParams)
        //attr
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FoldTextView)
        initAttrs(ta)
        //处理页面逻辑
        initView()
        ta.recycle()
    }

    private fun initAttrs(ta: TypedArray) {
        //lines
        ta.getInt(R.styleable.FoldTextView_fold_showLines, 4).also {
            showLines = it
        }
        //content
        ta.getString(R.styleable.FoldTextView_fold_content)?.let {
            content = it
        }
        //contentColor
        ta.getColor(R.styleable.FoldTextView_fold_contentColor, Color.GRAY).also {
            contentTextColor = it
        }
        //contentTextSize
        ta.getDimension(R.styleable.FoldTextView_fold_contentSize, 12f.toPx()).also {
            contentTextSize = it
        }
        //btn
        ta.getString(R.styleable.FoldTextView_fold_btnText).also {
            btnText = it ?: ""
        }
        //btnColor
        ta.getColor(R.styleable.FoldTextView_fold_btnTextColor, Color.GRAY).also {
            btnTextColor = it
        }
        //btnSize
        ta.getDimension(R.styleable.FoldTextView_fold_btnTextSize, 12f.toPx()).also {
            btnTextSize = it
        }
        //foldIcon
        ta.getDrawable(R.styleable.FoldTextView_fold_foldIcon).also {
            foldIcon = it
            Log.e("TAG", "$it")
        }
        //unFoldIcon
        ta.getDrawable(R.styleable.FoldTextView_fold_unFoldIcon).also {
            unFoldIcon = it
        }
    }

    private fun initView() {
        //
        textView.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                if (textView.lineCount > showLines) {
                    textView.setLines(showLines)
                    isFoldFlag = 0
                } else {
                    isFoldFlag = 2
                    btnView.visibility = View.GONE
                }
                textView.viewTreeObserver.removeOnPreDrawListener(this)
                return false
            }

        })
        //设置点击事件
        btnView.setOnClickListener {
            when (isFoldFlag) {
                //折叠状态
                0 -> {
                    textView.maxLines = (Int.MAX_VALUE)
                    btnView.visibility = View.GONE
                }
                //收起
                1 -> {
                    textView.setLines(showLines)
                    requestLayout()
                }
                else -> {

                }
            }
        }
    }

    private fun getRealTextViewHeight(textView: TextView): Int {
        val textHeight = textView.layout.getLineTop(textView.lineCount)
        val padding = textView.compoundPaddingTop + textView.compoundPaddingBottom
        return textHeight + padding
    }

    private val defaultDuration = 350L
}