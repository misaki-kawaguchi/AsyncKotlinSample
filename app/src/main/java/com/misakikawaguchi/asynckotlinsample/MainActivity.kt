package com.misakikawaguchi.asynckotlinsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {

    companion object {
        // ログに記載するタグ用の文字列
        private const val DEBUG_TAG =  "Async Test"

        // お天気情報のURL
        private const val WEATHERINFO_URL = "https://api.openweathermap.org/data/2.5/weather?lang=ja"

        // お天気APIにアクセスするためのAPIキー
        private const val APP_ID = ""
    }

    // リストビューに表示させるリストデータ
    private var _list: MutableList<MutableMap<String, String>> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}