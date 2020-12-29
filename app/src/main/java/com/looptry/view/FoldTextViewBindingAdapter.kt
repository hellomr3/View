package com.looptry.view

import androidx.databinding.BindingAdapter
import com.looptry.library.view.FoldTextView

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
@BindingAdapter(value = ["bindContent"])
fun FoldTextView.setContent(
    content: String?
) {
    if (content.isNullOrBlank()) return
    if (this.content == content) return
    this.content = content
}