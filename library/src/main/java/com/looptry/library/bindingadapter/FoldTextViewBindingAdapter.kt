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
    content?.let {
        this.content = content
    }
}