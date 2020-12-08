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
        var content =
            "****${System.lineSeparator()}法       功${System.lineSeparator()}${System.lineSeparator()}三去**"
        delay(2000)
        emit(content)
    }
}