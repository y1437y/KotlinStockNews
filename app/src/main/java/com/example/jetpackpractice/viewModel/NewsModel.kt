package com.example.jetpackpractice.viewModel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.IOException
import org.json.JSONObject

open class NewsModel : ViewModel() {
    val _stockNews = mutableStateOf(listOf(NewsData("", "", "")))
    val stockNews: State<List<NewsData>> get() = _stockNews

    fun getNewsBySymbol(symbol: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.marketaux.com/v1/news/all?symbols=$symbol&filter_entities=true&language=en&api_token=fOgBm4YTAeo2waAqZDPlTP0WabLsdQj20lYjAxjj")
                .build()
            try {
                val response = client.newCall(request).execute()
                val result = response.body?.string() ?: ""
                val json = JSONObject(result)
                val dataArray = json.getJSONArray("data")

                val newsList = mutableListOf<NewsData>()
                for (i in 0 until dataArray.length()) {
                    val bigObject = dataArray.getJSONObject(i)
                    newsList.add(NewsData(title = bigObject.getString("title"), imageUrl = bigObject.getString("image_url"), url = bigObject.getString("url")))
                }

                launch(Dispatchers.Main) {
                    _stockNews.value = newsList
                }

            } catch (e: IOException) {
                println(e.toString())
            }
        }
    }
}

data class NewsData(val title: String, val imageUrl: String, val url: String)