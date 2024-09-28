package com.example.fetchrewardscodingexercise

data class FetchItem(val id: String, val listId: String, val name: String) : Comparable<FetchItem> {
    override fun compareTo(other: FetchItem) = compareValuesBy(this, other,
        { it.name },
        { it.name }
    )
}