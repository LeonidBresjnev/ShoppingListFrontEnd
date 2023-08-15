package com.example.frontend.data
import kotlinx.serialization.Serializable


@Serializable
data class ShoppingListState(
    val connectedPlayers: List<String> = emptyList(),
    val currentList : List<ShoppingListItem> = emptyList()
)

@Serializable
data class ShoppingListItem  (
    var desc: String,
    var status: Int=0,
    var butik: String? = null,
    var antal: Float = 1f,
    var enhed: String = "stk",
    val id :  Int? = null)

