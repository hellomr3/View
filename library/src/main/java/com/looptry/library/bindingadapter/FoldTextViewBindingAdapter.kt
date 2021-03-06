package com.looptry.library.bindingadapter

import androidx.databinding.BindingAdapter
import com.looptry.library.view.FoldTextView

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
@BindingAdapter(value = ["bindFoldContent"])
fun FoldTextView.bindContent(
    content: CharSequence?
) {
    if (content == null)
        return
    if (this.content == content) return
    this.content = content
}