package com.looptry.view

import androidx.activity.viewModels
import com.looptry.architecture.page.BasicActivity
import com.looptry.architecture.page.DataBindingConfig
import kotlinx.android.synthetic.main.activity_timer.*

class MainActivity : BasicActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(BR.vm, R.layout.activity_timer, viewModel)
            .addBindingParams(BR.click, ClickProxy())
    }

    inner class ClickProxy {
        fun start() {
            timer.startRecord()
        }

        fun stop() {
            timer.stopRecord()
        }
    }
}