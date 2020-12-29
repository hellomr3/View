package com.looptry.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import kotlinx.coroutines.delay

/**
 * Author: mr.3
 * Date:
 * Desc:
 * Modify By:
 * Modify Date:
 */
class MainViewModel : ViewModel() {

    val content = liveData<String> {
        var v = ""
        repeat(100){
            v += "文字"
            emit(v)
            delay(100)
        }
    }
}