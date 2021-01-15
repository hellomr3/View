package com.looptry.library.view

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.IntDef
import androidx.appcompat.widget.AppCompatTextView
import com.looptry.library.R
import com.looptry.library.ext.setClickable
import com.looptry.library.ext.setTextColor
import com.looptry.library.ext.toSpan


/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
class FoldTextView(
    context: Context,
    attrs: AttributeSet?
) : AppCompatTextView(context, attrs) {

    companion object {

        val TAG: String
            get() = FoldTextView.javaClass.simpleName

        //无需折叠展开
        const val NO_FOLD = 0x00

        //折叠、折叠中
        const val FOLD = 0X01

        //展平、展开状态
        const val FLAT = 0X02
    }

    @IntDef(value = [NO_FOLD, FOLD, FLAT])
    @Target(AnnotationTarget.TYPE)
    @Retention(AnnotationRetention.SOURCE)
    annotation class Mode

    //展开模型
    var foldMode: @Mode Int
        get() {
            this.getTag(99)
            return this.getTag(R.id.fold_mode) as? @Mode Int ?: NO_FOLD
        }
        set(value) {
            this.setTag(R.id.fold_mode, value)
        }


    //属性动画
    var step: Float = 0f
        set(value) {
            field = value
            initView()
        }

    //动画
    private val objectAnimator = ObjectAnimator.ofFloat(this, "step", 0f, 1f)

    //默认展示行数
    var showLines = 3

    //内容
    var content: CharSequence = ""
        set(value) {
            field = value
            measureMode()
        }

    //点击按钮文字颜色
    var btnTextColor = paint.color

    //点击按钮文字大小
    var btnTextSize = paint.textSize

    //折叠状态下文字
    var foldText = context.getString(R.string.foldTextView_foldText)

    //展开状态下文字
    var flatText = context.getString(R.string.foldTextView_flatText)


    init {
        //attr
        val ta = context.obtainStyledAttributes(attrs, R.styleable.FoldTextView)
        initAttrs(ta)
        ta.recycle()
        //点击事件生效
        movementMethod = LinkMovementMethod.getInstance()

        viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                measureMode()
                viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
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
        //foldText
        ta.getString(R.styleable.FoldTextView_fold_foldText)?.let {
            foldText = it
        }
        //flatText
        ta.getString(R.styleable.FoldTextView_fold_flatText)?.let {
            flatText = it
        }
        //btnTextColor
        ta.getColor(R.styleable.FoldTextView_fold_btnTextColor, paint.color).let {
            btnTextColor = it
        }
        //btnTextSize
        ta.getDimension(R.styleable.FoldTextView_fold_btnTextSize, paint.textSize).let {
            btnTextSize = it
        }
    }

    //计算当前的显示模式
    private fun measureMode() {
        //测量内容占lineCount
        val lineCount = content.getPreLineCount()
        //判定当前显示类型
        foldMode = when {
            content.isBlank() || width == 0 -> {
                NO_FOLD
            }
            lineCount <= showLines -> {
                NO_FOLD
            }
            lineCount > showLines && foldMode == NO_FOLD -> {
                FOLD
            }
            else -> FLAT
        }


        initView()
    }

    //更新页面
    private fun initView() {
        when (foldMode) {
            NO_FOLD -> {
                text = content
            }
            FOLD -> {
                setTextWithAction(foldText) {
                    foldMode = FLAT
                    objectAnimator.start()
                }
            }
            FLAT -> {
                setTextWithAction(flatText) {
                    foldMode = FOLD
                    objectAnimator.reverse()
                }
            }
        }
    }

    private fun setTextWithAction(displayText: String, block: () -> Unit) {
        if (content.isBlank()) return
        val limitLength = getLimitLineLength(content, showLines)
        val totalLength = limitLength + (content.length - limitLength) * step
        val showContent = "${content.substring(0, totalLength.toInt())}\n${displayText}"
        val start = showContent.length - displayText.length
        val end = showContent.length
        showContent
            .toSpan()
            .apply {
                setTextColor(btnTextColor, start, end)
                setClickable(object : ClickableSpan() {
                    override fun onClick(widget: View) {
                        block.invoke()
                    }

                    override fun updateDrawState(ds: TextPaint) {
                        ds.isUnderlineText = false
                    }
                }, start, end)
            }
            .also {
                text = it
            }
    }

    //尝试获取到指定行
    private fun getLimitLineLength(content: CharSequence, limitLine: Int): Int {
        val lineLength = ((width - paddingStart - paddingEnd) / paint.textSize).toInt()
        val subContent = lineLength * limitLine
        if (content.length > subContent) {
            return getLimitLineLength(content.subSequence(0, subContent), limitLine)
        }
        return if (content.getPreLineCount() > limitLine) {
            getLimitLineLength(content.subSequence(0, content.length - 1), limitLine)
        } else {
            content.length
        }

    }

    //根据View宽计算文字
    private fun CharSequence.getPreLineCount(): Int {
        if (this.isEmpty() || width == 0) return 0
        val layout = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            StaticLayout.Builder.obtain(this, 0, this.length, paint, width)
                .build()
        } else {
            StaticLayout(this, paint, width, Layout.Alignment.ALIGN_NORMAL, 1f, 0f, true)
        }
        return layout.lineCount
    }
}