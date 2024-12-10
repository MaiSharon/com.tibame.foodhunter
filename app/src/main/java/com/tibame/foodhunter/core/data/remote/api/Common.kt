package com.tibame.foodhunter.core.data.remote.api

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

const val serverUrl = "http://10.2.17.40:8080/com.tibame.foodhunter_server"
suspend fun CommonPost(
    url:String,
    dataOut: String
): String {
    var respose = ""
    withContext(Dispatchers.IO){
        (URL(url).openConnection() as? HttpURLConnection)?.run{
            Log.d("Http","openConnection()")
            doInput = true
            doOutput = true
            setChunkedStreamingMode(0)
            useCaches = false
            requestMethod = "POST"
            setRequestProperty("content-type", "application/json")
            setRequestProperty("charset", "utf-8")
            Log.d("dataOut", dataOut)
            outputStream.bufferedWriter().use{ it.write(dataOut) }
            if(responseCode == 200){
                respose = inputStream.bufferedReader().use { it.readText() }
                Log.d("respose", respose)
            } else {
                println("false")
            }
        }
    }
    return respose
}