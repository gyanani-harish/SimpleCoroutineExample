package com.myhiltexample

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val coroutine = lifecycle.coroutineScope.async(Dispatchers.IO) {
            val a = callApi()
            Log.e("after api", a)
        }
        Log.e("api", "parallel to api")
    }

    private fun callApi(): String {
        val url = URL("https://jsonplaceholder.typicode.com/todos/1")
        try {
            Log.e("api", "before connection")
            val connection: HttpsURLConnection =
                url.openConnection()
                        as HttpsURLConnection
            connection.requestMethod = "GET"
            connection.connect()
            Log.e("api", "before 200 check"+connection.responseCode)
            //return "khali"
            if (connection.responseCode == 200) {
                Log.e("api", "got 200")
                val `in` = BufferedReader(
                    InputStreamReader(
                        connection.inputStream
                    )
                )
                var inputLine: String?
                val response = StringBuffer()

                while (`in`.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                `in`.close()
                return response.toString()
            } else {
                Log.e("api", "not 200")
                return "not 200 http response"
            }
        } catch (e: Exception) {
            Log.e("api", "got exception"+e.message)
            return e.message.toString()
        }
    }
}
