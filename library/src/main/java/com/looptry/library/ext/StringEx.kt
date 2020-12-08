package com.looptry.library.ext

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.*
import androidx.annotation.ColorInt
import com.blankj.utilcode.util.EncryptUtils


fun String.toSpan(): SpannableString {
    return SpannableString(this)
}

/**
 * 设置文字颜色
 * @receiver SpannableString
 * @param color Int
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setTextColor(@ColorInt color: Int, start: Int, end: Int = -1): SpannableString {
    //flag 只有在EditText上才起作用,在TextView上，均为左闭右开
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(ForegroundColorSpan(color), start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置文字背景色
 * @receiver SpannableString
 * @param color Int
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setBgColor(@ColorInt color: Int, start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(BackgroundColorSpan(color), start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置文字绝对大小
 * @receiver SpannableString
 * @param size Int
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setAbSize(size: Int, start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(AbsoluteSizeSpan(size, true), start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}


/**
 * 设置删除线
 * @receiver SpannableString
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setCenterLine(start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(StrikethroughSpan(), start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置下划线
 * @receiver SpannableString
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setUnderLine(start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(UnderlineSpan(), start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置上标
 * @receiver SpannableString
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setSuperscript(start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(SuperscriptSpan(), start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置下标
 * @receiver SpannableString
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setSubscript(start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(SubscriptSpan(), start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置style
 * @receiver SpannableString
 * @param style Int Typeface.BOLD Typeface.ITALIC...
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setStyle(style: Int, start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(StyleSpan(style), start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置图片
 * @receiver SpannableString
 * @param imageSpan ImageSpan
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setImage(imageSpan: ImageSpan, start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(imageSpan, start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置点击事件
 * @receiver SpannableString
 * @param clickableSpan ClickableSpan
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setClickable(
    clickableSpan: ClickableSpan,
    start: Int,
    end: Int = -1
): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(clickableSpan, start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * 设置url事件(跳转自带浏览器)
 * @receiver SpannableString
 * @param urlSpan URLSpan
 * @param start Int
 * @param end Int
 * @return SpannableString
 */
fun SpannableString.setURL(urlSpan: URLSpan, start: Int, end: Int = -1): SpannableString {
    var trueEnd = end
    if (end == -1) {
        trueEnd = this.length
    }
    setSpan(urlSpan, start, trueEnd, SpannableString.SPAN_INCLUSIVE_INCLUSIVE)
    return this
}

/**
 * SpannableString拼接
 * @receiver SpannableString
 * @param text Array<out CharSequence> 需要拼接的CharSequence数组
 * @return SpannableStringBuilder
 */
fun SpannableString.plus(vararg text: CharSequence): SpannableStringBuilder {
    val builder = SpannableStringBuilder()
    builder.append(this)
    text.forEach {
        builder.append(it)
    }
    return builder
}

/**
 * SpannableStringBuilder拼接
 * @receiver SpannableStringBuilder
 * @param text Array<out CharSequence> 需要拼接的CharSequence数组
 * @return SpannableStringBuilder
 */
fun SpannableStringBuilder.plus(vararg text: CharSequence): SpannableStringBuilder {
    text.forEach {
        this.append(it)
    }
    return this
}

//string加密MD5
fun String.encryptMD5(): String {
    return EncryptUtils.encryptMD5ToString(this)
}

/*复制文字*/
fun CharSequence.copyString(activity: Activity) {
    val myClipboard = activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val myClip = ClipData.newPlainText("text", this)
    myClipboard.setPrimaryClip(myClip)
}