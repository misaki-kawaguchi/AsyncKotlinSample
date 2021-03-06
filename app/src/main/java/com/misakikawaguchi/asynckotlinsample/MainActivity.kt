package com.misakikawaguchi.asynckotlinsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

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

        // リストをタップ
        lvCityList.onItemClickListener = ListItemClickListener()
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

    // リストがタップされた時の処理が記述されたリスナクラス
    private inner class ListItemClickListener : AdapterView.OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {

            // クリックしたデータの番号を取得
            val item = _list.get(position)

            // q：天気情報を表示させる都市名をアルファベットで指定
            // クリックしたデータの都市名を取得
            val q = item.get("q")

            // nullではない時に処理が実行される
            q?.let {
                // urlを取得
                val url = "$WEATHERINFO_URL&q=$q&appid=$APP_ID"
                // お天気情報の取得処理を行う
                asyncExecute(url)
            }
        }
    }

    // お天気情報の取得処理を行うメソッド
    @UiThread
    private fun asyncExecute(url: String) {
        // ライフサイクルオブジェクトごとに定義され、 対応するライフサイクルが破棄（destroy）されると、コルーチンもキャンセルされるようになる
        lifecycleScope.launch {
            // 非同期でお天気情報APIにアクセスする
            val result = backgroundTaskRunner(url)
            // JSON解析と画面表示処理を行う
            postExecutorRunner(result)
        }
    }

    // 非同期でお天気情報APIにアクセスするためのクラス
    @WorkerThread
    private suspend fun backgroundTaskRunner(url: String): String {
        // メソッド内の処理スレッドを分ける
        // メインスレッドの外部でディスクまたはネットワークの I/O を実行する場合に適している
        val returnVal = withContext(Dispatchers.IO) {
            var result = ""
            // URLオブジェクトを生成
            val url = URL(url)
            // URlオブジェクトからHttpURLConnectionオブジェクトを取得（HTTP接続を行うため）
            // 取得するにはopenConnection()を使うが、戻り値の型はURLConnectionなのでHttpURLConnectionにキャストする
            val con = url.openConnection() as? HttpURLConnection
            con?.run {
                // http接続メソッドを設定
                requestMethod = "GET"
                // 接続
                connect()
                // HttpURLConnectionオブジェクトからレスポンスデータを取得（天気情報が格納）
                // レスポンスデータであるInputStreamオブジェクトを文字列（JSON文字列）に変換
                result = is2String(inputStream)
                // HttpURLConnectionオブジェクトを解放
                // これを行う前にJSON文字列に変換
                disconnect()
                // InputStreamオブジェクトを解放
                inputStream.close()
            }
            result
        }
        return returnVal
    }

    // 取得したお天気情報を画面に表示するメソッド。
    @UiThread
    private fun postExecutorRunner(result: String) {
        // JSON文字列からJSONObjectオブジェクトを生成。これをルートオブジェクトとする。
        val rootJSON = JSONObject(result)
        // getStringで値（都市名）を取得
        val cityName = rootJSON.getString("name")
        // JSONデータ（天気）を取得して配列に代入する
        val weatherJSONArray = rootJSON.getJSONArray("weather")
        // 配列からJSONデータを取得する
        val weatherJSON = weatherJSONArray.getJSONObject(0)
        val description = weatherJSON.getString("description")
        val telop = cityName + "の天気"
        val desc = "現在は" + description + "です。"

        val tvWeatherTelop = findViewById<TextView>(R.id.tvWeatherTelop)
        val tvWeatherDesc = findViewById<TextView>(R.id.tvWeatherDesc)
        // ○○の天気
        tvWeatherTelop.text = telop
        // 現在は○○です
        tvWeatherDesc.text = desc
    }

    // InputStreamオブジェクトを文字列に変換するメソッド。 変換文字コードはUTF-8。
    private fun is2String(stream: InputStream): String {
        // Stringクラスを使って文字列を連結する
        val sb = StringBuilder()
        // データをUTF-8に変換してから配列にしてバッファに取り込む（設定しないと文字化けする）
        val reader = BufferedReader(InputStreamReader(stream, "UTF-8"))
        // バッファの１行を取り出す
        var line = reader.readLine()
        // 取り出したデータがnullでない場合
        while(line != null) {
            // 文字列に追加する
            sb.append(line)
            line = reader.readLine()
        }
        reader.close()
        return sb.toString()
    }

}