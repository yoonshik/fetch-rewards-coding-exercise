package com.example.fetchrewardscodingexercise


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier
                        .background(Color(0xFFF5F5F5))
                        .padding(16.dp)) {

                        FetchItemList(
                            fetchItemList,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun FetchItemList(items: List<FetchItem>?, modifier: Modifier = Modifier) {
        if (items == null) {
            return
        }

        LazyColumn(modifier = modifier.padding(8.dp)) {
            items(items) { item ->
                FetchItemCard(item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    @Composable
    fun FetchItemCard(item: FetchItem) {
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Text(
                    text = "${item.name}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "List: ${item.listId}",
                    fontSize = 14.sp,
                    color = Color(0xFF666666)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "ID: ${item.id}",
                    fontSize = 14.sp,
                    color = Color(0xFF444444)
                )
            }
        }
    }

    private fun getFetchItemList(): MutableList<FetchItem> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://fetch-hiring.s3.amazonaws.com/hiring.json")
            .build()

        val hashMap = HashMap<String, MutableList<FetchItem>>()

        // Run this network request on a separate thread
        val thread = Thread {
            try {
                val response: Response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    val jsonData = response.body?.string()
                    val jsonArray = JSONArray(jsonData)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getString("id")
                        val listId = jsonObject.getString("listId")
                        val name = jsonObject.getString("name")

                        // Avoid adding items with null or empty names
                        if (!name.isNullOrEmpty() && name != "null") {
                            if (!hashMap.containsKey(listId)) {
                                hashMap[listId] = mutableListOf()
                            }
                            hashMap[listId]?.add(FetchItem(id, listId, name))
                        }
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        thread.start()
        thread.join()

        val result = mutableListOf<FetchItem>()
        for (key in hashMap.keys) {
            val list = hashMap[key]
            list?.sort()
            result.addAll(list ?: emptyList())
        }
        return result
    }
}