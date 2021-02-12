package com.misakikawaguchi.asynckotlinsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import android.widget.SimpleAdapter

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

        // リストのIDを取得
        val lvCityList = findViewById<ListView>(R.id.lvCityList)
        // アダプタの作成
        val from = arrayOf("name")
        val to = intArrayOf(android.R.id.text1)
        val adapter = SimpleAdapter(applicationContext, _list, android.R.layout.simple_list_item_1, from, to)
        lvCityList.adapter = adapter
    }
}