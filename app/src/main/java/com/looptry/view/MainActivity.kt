package com.looptry.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
//        val url = "http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f20.mp4"
//        val url = "https://stream7.iqilu.com/10339/upload_transcode/202002/18/20200218114723HDu3hhxqIT.mp4"
//        val url ="http://192.168.2.22:12003/file/download/5fa2497643442c48a9e731d3"
        val url ="http://192.168.2.22:12003/file/downloadVideo/5fa2497643442c48a9e731d3"
//        PlayerFactory.setPlayManager(Exo2PlayerManager::class.java)

        player.setUp(
            url,
            true,
            "title"
        )
//        view.setVideoURI(Uri.parse(url))
//        view.start()
    }
}