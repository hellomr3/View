package com.looptry.view

import androidx.activity.viewModels
import com.looptry.architecture.page.BasicActivity
import com.looptry.architecture.page.DataBindingConfig

class MainActivity : BasicActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(BR.vm, R.layout.activity_main, viewModel)
    }
}