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

        _list = createList()

        // リストのIDを取得
        val lvCityList = findViewById<ListView>(R.id.lvCityList)
        // アダプタの作成
        val from = arrayOf("name")
        val to = intArrayOf(android.R.id.text1)
        val adapter = SimpleAdapter(applicationContext, _list, android.R.layout.simple_list_item_1, from, to)
        lvCityList.adapter = adapter

    }

    // リストビューに表示させる天気ポイントリストデータを生成するメソッド
    private fun createList(): MutableList<MutableMap<String, String>> {
        val list:MutableList<MutableMap<String, String>> = mutableListOf()

        var city = mutableMapOf("name" to "大阪", "q" to "Osaka")
        list.add(city)
        city = mutableMapOf("name" to "神戸", "q" to "Kobe")
        list.add(city)
        city = mutableMapOf("name" to "京都", "q" to "Kyoto")
        list.add(city)
        city = mutableMapOf("name" to "大津", "q" to "Otsu")
        list.add(city)
        city = mutableMapOf("name" to "奈良", "q" to "Nara")
        list.add(city)
        city = mutableMapOf("name" to "和歌山", "q" to "Wakayama")
        list.add(city)
        city = mutableMapOf("name" to "姫路", "q" to "Himeji")
        list.add(city)
        city = mutableMapOf("name" to "東京", "q" to "Tokyo")
        list.add(city)

        return  list
    }
}