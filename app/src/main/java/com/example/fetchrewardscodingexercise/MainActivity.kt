package com.example.fetchrewardscodingexercise

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fetchrewardscodingexercise.ui.theme.FetchRewardsCodingExerciseTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import java.io.IOException
import java.util.LinkedList

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fetchItemList: List<FetchItem> = getFetchItemList()

        setContent {
            FetchRewardsCodingExerciseTheme {
                FetchItemList(fetchItemList)
            }
        }
    }

    @Composable
    fun FetchItemList(items: List<FetchItem>, modifier: Modifier = Modifier) {
        LazyColumn(modifier) {
            items(items) { item ->
                Text(text = "${item.id} ${item.listId} ${item.name}")
            }
        }
    }

    private fun getFetchItemList(): List<FetchItem> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            .build()

        val fetchItemList = mutableListOf<FetchItem>()

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
                        if (!name.isNullOrEmpty()) {
                            fetchItemList.add(FetchItem(id, listId, name))
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
        thread.join()  // Wait for the thread to finish

        return fetchItemList
    }
}

