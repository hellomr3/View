package com.looptry.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        val recordList = mutableListOf<Pair<Long, Long>>()
        val current = System.currentTimeMillis()
        Pair<Long, Long>(current - 1 * 60 * 60 * 1000, current).also { recordList.add(it) }
        Pair<Long, Long>(
            current - 3 * 60 * 60 * 1000,
            current - 2 * 60 * 60 * 1000
        ).also { recordList.add(it) }
        Pair<Long, Long>(
            current + 1 * 60 * 60 * 1000,
            current + 2 * 60 * 60 * 1000
        ).also { recordList.add(it) }
        timeLine.setRecordList(recordList)
    }
}