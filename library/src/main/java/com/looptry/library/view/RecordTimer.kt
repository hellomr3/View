package com.looptry.library.view

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.blankj.utilcode.util.TimeUtils
import com.looptry.library.R
import java.util.*

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
class RecordTimer(
    context: Context,
    attr: AttributeSet?
) : ConstraintLayout(context, attr) {

    companion object {
        const val TICK = 0x666
        const val CLEAR = 0x999

        const val DEFAULT = 0L
    }

    private var root: View =
        LayoutInflater.from(context).inflate(R.layout.view_record_timer, this, true)

    private var imageView: ImageView = root.findViewById(R.id.icon)

    private var textView: TextView = root.findViewById(R.id.tick)

    //动画
    private val animator = ObjectAnimator.ofFloat(imageView, "alpha", 0f, 1f).apply {
        duration = 1000
    }

    //当前时间
    var tickTime: Long = DEFAULT
        private set(value) {
            val format = TimeUtils.millis2String(value - 8 * 60 * 60 * 1000, "HH:mm:ss")
            field = value
            textView.text = format
            if ((value / 1000) % 2 == 0L) {
                animator.start()
            } else animator.reverse()
        }


    val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                TICK -> {
                    tickTime += 1000
                }
                CLEAR -> {
                    tickTime = DEFAULT
                }
            }
        }
    }

    private var timer: Timer? = null

    init {
        initAttrs(attr)
    }

    private fun initAttrs(attr: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attr, R.styleable.RecordTimer)
        //设置默认时间
        typedArray.getInt(R.styleable.RecordTimer_defaultTimeSecond, 0).also {
            tickTime = it * 1000L
        }
        typedArray.recycle()
    }

    fun startRecord() {
        clearTickTime()
        //timer
        if (timer == null) {
            timer = Timer()
        }
        timer?.schedule(object : TimerTask() {
            override fun run() {
                val msg = Message()
                msg.what = TICK
                mHandler.sendMessage(msg)
            }
        }, 1000, 1000)
    }

    fun stopRecord() {
        //清空timer
        timer?.cancel()
        timer?.purge()
        timer = null
        //重新展示icon
        animator.start()
    }

    fun clearTickTime() {
        val msg = Message()
        msg.what = CLEAR
        mHandler.sendMessage(msg)
    }

}