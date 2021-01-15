package com.looptry.library.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.ActivityUtils
import com.looptry.library.R
import com.looptry.library.util.dp2Px

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
class BlockButton(
    context: Context,
    private val attrs: AttributeSet?
) : ConstraintLayout(context, attrs) {

    private val DEFAULT_TEXT_SIZE = 14f.dp2Px()
    private val DEFAULT_TEXT_COLOR = ContextCompat.getColor(context, R.color.text999)
    private val DEFAULT_SEPARATOR_COLOR = ContextCompat.getColor(context, R.color.colorLightGray)

    //view
    private val leadIcon: ImageView
    private val leadTitle: TextView
    private val tailDesc: TextView
    private val tailIcon: ImageView
    private val separatorLine: View


    init {
        LayoutInflater.from(context).inflate(R.layout.view_block_button, this, true)
        leadIcon = findViewById(R.id.leadIcon)
        leadTitle = findViewById(R.id.leadTitle)
        tailDesc = findViewById(R.id.tailDesc)
        tailIcon = findViewById(R.id.tailIcon)
        separatorLine = findViewById(R.id.line)
        //initAttr
        initTypeArray()

    }

    private fun initTypeArray() {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.BlockButton)
        //leadIcon
        ta.getDrawable(R.styleable.BlockButton_leadIcon).let {
            if (it == null) {
                leadIcon.visibility = View.GONE
                return@let
            }
            leadIcon.setImageDrawable(it)
        }
        //leadTitle
        ta.getString(R.styleable.BlockButton_leadTitle)?.let {
            leadTitle.text = it
        }
        ta.getDimension(R.styleable.BlockButton_titleTextSize, DEFAULT_TEXT_SIZE).let {
            leadTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
        ta.getColor(R.styleable.BlockButton_titleTextColor, DEFAULT_TEXT_COLOR).let {
            leadTitle.setTextColor(it)
        }
        //tailDesc
        ta.getString(R.styleable.BlockButton_tailDesc)?.let {
            tailDesc.text = it
        }
        ta.getDimension(R.styleable.BlockButton_descTextSize, DEFAULT_TEXT_SIZE).let {
            tailDesc.setTextSize(TypedValue.COMPLEX_UNIT_PX, it)
        }
        ta.getColor(R.styleable.BlockButton_descTextColor, DEFAULT_TEXT_COLOR).let {
            tailDesc.setTextColor(it)
        }
        //tailIcon
        ta.getDrawable(R.styleable.BlockButton_tailIcon).let {
            if (it == null) {
                tailIcon.visibility = View.GONE
                return@let
            }
            tailIcon.setImageDrawable(it)
        }
        //showSeparatorLine
        ta.getInt(R.styleable.BlockButton_showSeparatorLine, View.INVISIBLE).let {
            separatorLine.visibility = it
        }
        ta.getColor(R.styleable.BlockButton_separatorLineColor, DEFAULT_SEPARATOR_COLOR).let {
            separatorLine.setBackgroundColor(it)
        }
        ta.recycle()
    }
}