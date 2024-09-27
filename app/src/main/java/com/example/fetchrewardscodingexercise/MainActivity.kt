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
        val list = LinkedList<FetchItem>()
        for (i in 0..10) {
            list.add(FetchItem("id$i", "listId$i", "name$i"))
        }
        return list
    }
}

