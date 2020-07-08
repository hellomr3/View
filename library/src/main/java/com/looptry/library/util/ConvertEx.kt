package com.looptry.library.util

import android.content.res.Resources

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */

fun Float.toPx(): Float {
    return dp2px(this).toFloat()
}

private fun dp2px(dpValue: Float): Int {
    val scale = Resources.getSystem().displayMetrics.density
    return (dpValue * scale + 0.5f).toInt()
}