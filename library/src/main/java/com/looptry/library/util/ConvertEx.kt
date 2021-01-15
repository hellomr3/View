package com.looptry.library.util

import android.content.res.Resources
import android.util.TypedValue

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */

fun Float.dp2Px(): Float {
    val metrics = Resources.getSystem().displayMetrics
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this, metrics)
}

fun Int.dp2Px(): Float {
    return this.toFloat().dp2Px()
}
