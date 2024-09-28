package com.example.fetchrewardscodingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.fetchrewardscodingexercise.ui.theme.FetchRewardsCodingExerciseTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fetchItemList: MutableList<FetchItem> = getFetchItemList()

        setContent {
            FetchRewardsCodingExerciseTheme {
                Box(modifier = Modifier.background(Color.LightGray)) {
                    FetchItemList(
                        fetchItemList,
                        modifier = Modifier.fillMaxSize(1.0f).padding(24.dp))
                }

            }
        }
    }

    @Composable
    fun FetchItemList(items: List<FetchItem>?, modifier: Modifier = Modifier) {
        if (items == null) {
            return
        }
        LazyColumn(modifier) {
            items(items) { item ->
                Text(text = "id=${item.id}, list=${item.listId}, ${item.name}")
            }
        }
    }

    private fun getFetchItemList(): MutableList<FetchItem> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            .build()

        var hashMap = HashMap<String, MutableList<FetchItem>>()

        // Run this network request on a separate thread
        val thread = Thread {
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    val jsonArray = JSONArray(jsonData)

                    // Parse the JSON array and add the items to the list
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("id")
                        val listId = jsonObject.getString("listId")
                        val name = jsonObject.getString("name")

                        // Avoid adding items with null or empty names
                        if (!name.isNullOrEmpty() and !name.equals("null")) {
                            if (!hashMap.containsKey(listId)) {
                                hashMap.put(listId, mutableListOf<FetchItem>())
                            } else {
                                var list : MutableList<FetchItem>? = hashMap.get(listId)
                                list?.add(FetchItem(id, listId, name))
                            }
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
        thread.join()  // Wait for the thread to finish


        var result = mutableListOf<FetchItem>()
        for (key in hashMap.keys) {
            var l = hashMap[key]
            if (l != null) {
                l.sort()
                result.addAll(l)
            }
        }
        return result
    }
}

